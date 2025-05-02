package no.uio.ifi.in2000.team54.ui.home.pages


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.home.GreetingMessage
import no.uio.ifi.in2000.team54.ui.home.getGreeting
import no.uio.ifi.in2000.team54.ui.theme.Background
import no.uio.ifi.in2000.team54.ui.theme.YellowText


@Composable
fun InfoScreen() {

    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
    ){
        InfoScreenTopBar()
    }
}

@Composable
fun InfoScreenTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 15.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()

        ) {
            Column {
                GreetingMessage()
                Spacer(Modifier.padding(top = 13.dp))
                Text(
                    text = "Velkommen til",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "SunSaver!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = YellowText
                )
            }

            Image(
                painter = painterResource(R.drawable.mediumsizelogo),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-30).dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInfoScreen() {

    InfoScreen()

}