package dev.sanskar.photoplay.ui.composables

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
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
fun AddWatchlistDialog(
    modifier: Modifier = Modifier,
    initialTitle: String = "",
    initialDescription: String = "",
    onDone: (String, String) -> Unit,
) {
    Dialog(
        onDismissRequest = { onDone(initialTitle, initialDescription) },
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
                var titleState by remember { mutableStateOf(initialTitle) }
                var descriptionState by remember { mutableStateOf(initialDescription) }
                OutlinedTextField(
                    value = titleState,
                    onValueChange = { titleState = it },
                    maxLines = 1,
                    label = { Text("Watchlist Title") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Title,
                            contentDescription = null)
                    },
                    shape = RoundedCornerShape(8.dp),
                    isError = titleState.isEmpty(),
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = descriptionState,
                    onValueChange = { descriptionState = it },
                    maxLines = 5,
                    label = { Text("Watchlist Description") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Description,
                            contentDescription = null)
                    },
                    shape = RoundedCornerShape(8.dp),
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (titleState.isNotEmpty()) {
                            onDone(titleState, descriptionState)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (initialTitle.isNotEmpty()) Icons.Filled.Done else Icons.Filled.Add,
                        contentDescription = null
                    )
                    Text(if (initialTitle.isNotEmpty()) "Save" else "Add")
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
    val animatedBackgroundColor by rememberInfiniteTransition().animateColor(
        initialValue = Color(0xFF4A148C),
        targetValue = Color(0xFFBF360C),
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 10000
                Color(0xFF1A237E) at 2000
                Color(0xFF004D40) at 5000
                Color(0xFF01579B) at 8000
            },
            repeatMode = RepeatMode.Reverse
        )
    )

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
                            .background(animatedBackgroundColor)
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
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colors.onPrimary
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
                        onClick = { onClick(watchlistStates.toList()) },
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

@Composable
fun ConfirmActionDialog(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "Please confirm the action",
    onConfirm: (Boolean) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onConfirm(false) },
        confirmButton = {
            Button(onClick = { onConfirm(true) }) {
                Text(text = "Confirm")
            }
        },
        title = { Text(text = title) },
        text = { Text(text = message) },
        modifier = modifier
    )
}

@Composable
fun ErrorDialog(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "There was an error",
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "Okay")
            }
        },
        title = { Text(text = title) },
        text = { Text(text = message) },
        modifier = modifier
    )
}