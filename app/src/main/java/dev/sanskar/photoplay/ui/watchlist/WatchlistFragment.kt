package dev.sanskar.photoplay.ui.watchlist

import android.os.Bundle
import android.service.autofill.Validators.and
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Title
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import dagger.hilt.android.AndroidEntryPoint
import dev.sanskar.photoplay.R
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
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                    Text(
                                        text = "Add Watchlist",
                                        style = MaterialTheme.typography.h3,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    ) {
                        WatchlistScreen(Modifier.padding(it))
                        if (showAddDialog) AddWatchlistDialog { showAddDialog = false }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun AddWatchlistDialog(modifier: Modifier = Modifier, onDismiss: () -> Unit) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            ),
        ) {
            Surface(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                ) {
                    var title by remember { mutableStateOf("") }
                    var description by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        maxLines = 1,
                        label = { Text("Watchlist Title") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Title, contentDescription = null) },
                        shape = RoundedCornerShape(8.dp),
                        isError = title.isEmpty(),
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        maxLines = 5,
                        label = { Text("Watchlist Description") },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Description, contentDescription = null) },
                        shape = RoundedCornerShape(8.dp),
                        isError = description.isEmpty(),
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (title.isNotEmpty() and description.isNotEmpty()) {
                                viewModel.addWatchlist(title, description)
                                onDismiss()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null
                        )
                        Text("Add")
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier.fillMaxSize()
                    ) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
                        LottieAnimation(
                            composition = composition,
                            modifier = Modifier
                                .fillMaxSize(0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Create a watchlist to get started!",
                            style = MaterialTheme.typography.h2,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                is UiState.Success -> {
                    LazyColumn(
                        modifier = modifier
                    ) {
                        items(state.data) { watchlist ->
                            Text(watchlist.name)
                        }
                    }
                }
            }
        }
    }


}