package no.uio.ifi.in2000.team54.ui.info


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.theme.YellowText


@Composable
fun InfoScreen() {

    Box (
        Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.sunny),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )

        Column(
            Modifier.fillMaxSize()
        ) {
            InfoScreenTopBar()

        }
    }
}

@Composable
fun InfoScreenTopBar() {

    Column (
        Modifier
            .fillMaxWidth()
            .padding(top = 23.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(Modifier.padding(top = 23.dp))
        Text(
            text = "Velkommen til",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "SunSavers",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = YellowText
        )
        Text(
            text = "Informasjonsside",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

    }
}
