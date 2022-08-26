package dev.sanskar.photoplay.ui.composables

import android.app.Dialog
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Title
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import dev.sanskar.photoplay.data.Movie
import dev.sanskar.photoplay.db.Watchlist
import dev.sanskar.photoplay.util.getDownloadUrl

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddWatchlistDialog(modifier: Modifier = Modifier, onDone: (String, String) -> Unit) {
    Dialog(
        onDismissRequest = { onDone("", "") },
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
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (title.isNotEmpty()) {
                            onDone(title, description)
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddMovieToWatchLists(
    movie: Movie,
    modifier: Modifier = Modifier,
    checklist: List<Pair<Watchlist, Boolean>>,
    onWatchlistCreate: (String, String) -> Unit,
    onClick: (List<Pair<Watchlist, Boolean>>) -> Unit,
) {
    val watchlistStates = remember {
        mutableStateListOf<Pair<Watchlist, Boolean>>().also {
            it.addAll(checklist)
        }
    }
    var showCreateWatchlistDialog by remember { mutableStateOf(false) }
    if (showCreateWatchlistDialog) AddWatchlistDialog { title, description ->
        showCreateWatchlistDialog = false
        onWatchlistCreate(title, description)
    }

    val onCheck: (Int, Boolean) -> Unit = { index, state ->
        watchlistStates[index] = watchlistStates[index].copy(second = state)
    }

    Dialog(
        onDismissRequest = { onClick(emptyList()) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize(0.9f),
            elevation = 3.dp,
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    elevation = 5.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .background(brush = Brush.horizontalGradient(listOf(
                                Color(0xFF4A148C),
                                Color(0xFF1A237E),
                                Color(0xFF004D40),
                                Color(0xFF01579B),
                                Color(0xFFBF360C)
                            )))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = movie.poster_path?.getDownloadUrl()
                                    ?: movie.backdrop_path?.getDownloadUrl() ?: "",
                                contentDescription = null,
                                modifier = Modifier
                                    .size(128.dp),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = movie.title,
                                style = MaterialTheme.typography.h2,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    item {
                        if (watchlistStates.isEmpty()) LottieEmpty("Add a watchlist to get started!")
                    }
                    itemsIndexed(watchlistStates) { index, pair ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable { onCheck(index, !pair.second) },
                        ) {
                            Text(
                                pair.first.name,
                                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(Modifier.weight(1f))
                            Checkbox(
                                checked = pair.second,
                                onCheckedChange = { onCheck(index, it) }
                            )
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { showCreateWatchlistDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                        Text(
                            text = "Add Watchlist",
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    Button(
                        onClick = { onClick(watchlistStates.toList())},
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                        )
                        Text(
                            text = "Save",
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}