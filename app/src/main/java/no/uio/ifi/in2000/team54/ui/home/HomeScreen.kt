package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.maps.extension.style.style


@Composable
fun HomeScreenTopBar() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Row {
            Text(
                text = "Oversikt",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = " & ",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFFEABA0E)
            )
        }
        Text(
            text = "Status ",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFFEABA0E)
        )
    }
}

@Composable
fun PropertyCards() {

    Card(
        modifier = Modifier
            .height(200.dp)
            .width(201.dp)
            .padding(5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Black,
            containerColor = Color(0xFFF6A35A),
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(5.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text(
                    text = "* sett inn bilde *: ",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(70.dp))

                Text(
                    text = "Adresse: ",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Penger spart : ",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ElectricityCard() {

    Card(
        modifier = Modifier
            .height(200.dp)
            .width(374.dp)
            .padding(5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Black,
            containerColor = Color(0xFFF0C571),
        )
    ) {
        Row(
            Modifier.padding(10.dp)
        ) {
            Text(
                text = "Strøm ",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "& Sparing",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }

    }
}

@Composable
fun WeatherCard() {

    Card(
        modifier = Modifier
            .height(64.dp)
            .width(374.dp)
            .padding(5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Black,
            containerColor = Color(0xFF98B0D5),
        )
    ) {
        Row(
            Modifier.padding(15.dp)
        ) {
            Text(
                text = "Væroversikt ",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "& Temperatur",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewPropertyCard() {


    Column{

        HomeScreenTopBar()

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            PropertyCards()
          // PropertyCards()
        }

        ElectricityCard()

        WeatherCard()
    }
}

