package dev.sanskar.photoplay.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.photoplay.db.Watchlist
import dev.sanskar.photoplay.ui.composables.AddWatchlistDialog
import dev.sanskar.photoplay.ui.composables.ConfirmActionDialog
import dev.sanskar.photoplay.ui.composables.LottieEmpty
import dev.sanskar.photoplay.ui.theme.PhotoPlayTheme
import dev.sanskar.photoplay.util.UiState

@AndroidEntryPoint
class WatchlistFragment : Fragment() {
    private val viewModel by viewModels<WatchlistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PhotoPlayTheme {
                    var showAddDialog by remember { mutableStateOf(false) }
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(onClick = { showAddDialog = true }) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                    Text(
                                        text = "Add Watchlist",
                                        style = MaterialTheme.typography.h3,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                            }
                        }
                    ) {
                        WatchlistScreen(Modifier.padding(it))
                        if (showAddDialog) AddWatchlistDialog { title, description ->
                            viewModel.addWatchlist(title, description)
                            showAddDialog = false
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalAnimationApi::class)
    @Composable
    fun WatchlistScreen(modifier: Modifier = Modifier) {
        val state by viewModel.watchlists.collectAsStateWithLifecycle()
        AnimatedContent(targetState = state) { state ->
            when (state) {
                is UiState.Loading -> {}
                is UiState.Error -> {}
                is UiState.Empty -> {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LottieEmpty("Create a watchlist to get started!")
                    }
                }
                is UiState.Success -> {
                    Watchlists(watchlists = state.data)
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Watchlists(watchlists: List<Watchlist>, modifier: Modifier = Modifier) {
        LazyColumn(
            modifier = modifier
        ) {
            items(watchlists) { watchlist ->
                var menuOpened by remember { mutableStateOf(false) }
                var openDeleteConfirmDialog by remember { mutableStateOf(false) }
                var openEditDialog by remember { mutableStateOf(false) }

                if (openEditDialog) AddWatchlistDialog(
                    initialTitle = watchlist.name,
                    initialDescription = watchlist.description,
                ) { title, description ->
                    viewModel.updateWatchlist(watchlist, title, description)
                    openEditDialog = false
                }

                if (openDeleteConfirmDialog) ConfirmActionDialog(message = "Are you sure you want to delete the watchlist named ${watchlist.name}?") {
                    if (it) {
                        viewModel.deleteWatchlist(watchlist.id)
                    }
                    openDeleteConfirmDialog = false
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .combinedClickable(
                            onClick = {
                                findNavController().navigate(
                                    WatchlistFragmentDirections.actionWatchlistFragmentToWatchlistDetailsFragment(
                                        watchlist.id)
                                )
                            },
                            onLongClick = {
                                menuOpened = true
                            }
                        ),
                ) {
                    Column {
                        Text(
                            text = watchlist.name,
                            style = MaterialTheme.typography.h2,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        Text(
                            text = watchlist.description,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuOpened,
                        onDismissRequest = { menuOpened = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                openEditDialog = true
                                menuOpened = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Edit")
                        }
                        DropdownMenuItem(
                            onClick = {
                                openDeleteConfirmDialog = true
                                menuOpened = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}