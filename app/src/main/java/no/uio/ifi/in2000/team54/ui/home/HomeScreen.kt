package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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


@Composable
fun HomeScreenTopBar() {


}

@Composable
fun PropertyCards() {

    Card(
        modifier = Modifier
            .height(200.dp)
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
            Text(
                text = "Solcelleanlegg (navn)",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(5.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text(text = "Oversikt", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(3.dp))

                Text(text = "Adresse: ", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Antall solceller",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Antall måneder siden installasjon: ",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Penger spart på denne eiendommen: ",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}



@Composable
fun AddProperty() {

    Card(
        modifier = Modifier
            .height(60.dp)
            .padding(5.dp),
        //.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Black,
            containerColor = Color(0xFFF6A35A),
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Legg til eiendom    (+)",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPropertyCard() {


    Column{

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Sveip for å se andre eiendommer!",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(10.dp))

        PropertyCards()

        AddProperty()
    }
}

