package no.uio.ifi.in2000.team54.ui.splashscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.mapbox.maps.extension.style.expressions.dsl.generated.color


@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE0A6)) // Bakgrunnsfarge
            .padding(16.dp),
        verticalArrangement = Arrangement.Center, // Sentrer vertikalt
        horizontalAlignment = Alignment.CenterHorizontally // Sentrer horisontalt
    ) {
        MyLottie()
    }
}

@Composable
fun MyLottie() {
    val url = "https://cdn.lottielab.com/l/3GQDw3a8A87E6L.json"
    val composition by rememberLottieComposition(LottieCompositionSpec.Url(url))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
    LottieAnimation(composition, { progress })
}

@Preview(showBackground = true)
@Composable
fun PreviewMessagecard(){
    SplashScreen()
}
