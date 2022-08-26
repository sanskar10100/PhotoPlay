package dev.sanskar.photoplay.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.sanskar.photoplay.R

@Composable
fun LottieEmpty(text: String = "Add some content to get started!") {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    LottieAnimation(
        composition = composition,
        modifier = Modifier
            .size(256.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.h2,
        textAlign = TextAlign.Center,
    )
}