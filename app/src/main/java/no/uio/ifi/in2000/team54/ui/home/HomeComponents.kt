package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.theme.Background
import no.uio.ifi.in2000.team54.ui.theme.GreyText
import no.uio.ifi.in2000.team54.ui.theme.Light
import no.uio.ifi.in2000.team54.ui.theme.LightOrange
import no.uio.ifi.in2000.team54.ui.theme.Lighter
import no.uio.ifi.in2000.team54.ui.theme.Panels
import no.uio.ifi.in2000.team54.ui.theme.SavingsYellow
import no.uio.ifi.in2000.team54.ui.theme.WeatherBlue
import no.uio.ifi.in2000.team54.ui.theme.WeatherBorder
import no.uio.ifi.in2000.team54.ui.theme.YellowBorder
import no.uio.ifi.in2000.team54.ui.theme.YellowText
import no.uio.ifi.in2000.team54.ui.theme.YellowerBorder


@Composable
fun HomeScreen(homeViewModel: HomeScreenViewModel , navController: NavController) {
    Column(
        Modifier.fillMaxSize().background(Background)
    ) {
        HomeScreenTopBar()
        PropertyCard()
        SwitchContent(homeViewModel)
        WeatherCard(navController)
    }
}

@Composable
fun GreetingMessage() {
    Text(
        text = getGreeting(),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun HomeScreenTopBar() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 15.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column{
                GreetingMessage()
                Row (Modifier.padding(top = 13.dp)) {
                    Text(
                        text = "Oversikt",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " & ",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = YellowText
                    )
                }
                Text(
                    text = "Status ",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = YellowText
                )
            }

            Image(
                painter = painterResource(R.drawable.sun),
                contentDescription = null,
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-25).dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun PropertyCard() {

    Card(
        modifier = Modifier
            .height(205.dp)
            .width(201.dp)
            .padding(15.dp)
            .border(1.dp, YellowBorder, shape = RoundedCornerShape(20.dp)),

        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightOrange,
        )
    ) {
        Card(
            modifier = Modifier
                .height(131.dp)
                .width(159.dp)
                .padding(15.dp),

            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                contentColor = Color.Black,
                containerColor = Lighter,
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
                }
            }
        }

        Column(
            modifier = Modifier.padding(start = 15.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Adresse: ",
                style = MaterialTheme.typography.bodySmall,
                color = GreyText,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Penger spart : ",
                style = MaterialTheme.typography.bodySmall,
                color = GreyText,
                fontWeight = FontWeight.Bold

            )
        }
    }
}


@Composable
fun ElectricityCard(viewModel: HomeScreenViewModel) {

    Card(
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, YellowBorder, shape = RoundedCornerShape(20.dp))
            .height(250.dp)
            .width(395.dp),

        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Black,
            containerColor = Light,
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.padding(10.dp)
            ) {
                Text(
                    text = "Strømutgifter ",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "& Sparing",
                    style = MaterialTheme.typography.bodyLarge,
                    color = YellowText
                )
            }
            EletricityGraphContainer(viewModel = viewModel)

        }
    }
}


@Composable
fun SwitchContent(homeScreenViewModel: HomeScreenViewModel) {
    var isFlipped by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .height(250.dp)
            .width(409.dp)
            .clickable { isFlipped = !isFlipped },
        contentAlignment = Alignment.Center
    ) {
        if (!isFlipped) {
            ElectricityCard(homeScreenViewModel)
        } else {
            SavingsCard()
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        SlidePageButton(
            selected = !isFlipped,
            onClick = { isFlipped = false }
        )
        Spacer(modifier = Modifier.width(8.dp))

        SlidePageButton(
            selected = isFlipped,
            onClick = { isFlipped = true }
        )
    }
}


@Composable
fun SavingsCard() {

    ElectricityBillOverview()
}


@Composable
fun WeatherCard(navController: NavController) {

    Card(
        modifier = Modifier
            .height(80.dp)
            .width(409.dp)
            .padding(15.dp)
            .border(1.dp, WeatherBorder, shape = RoundedCornerShape(20.dp))
            .clickable { navController.navigate("weather") },

        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Black,
            containerColor = WeatherBlue,
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(15.dp)
            ) {
                Text(
                    text = "Værforhold ",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "& Skydekke",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }

            Image(
                painter = painterResource(R.drawable.weather),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = (-15).dp)
            )
        }
    }
}

/*
@Preview
@Composable
fun PreviewSavingsCard() {
    SwitchContent()
}*/