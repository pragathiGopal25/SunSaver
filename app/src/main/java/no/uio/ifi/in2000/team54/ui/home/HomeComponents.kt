package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team54.R
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
fun ElectricityCard() {

    Card(
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, YellowBorder, shape = RoundedCornerShape(20.dp))
            .height(250.dp)
            .width(395.dp),
        colors = CardDefaults.cardColors( containerColor = Light )

    ) {
        Row(
            Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Strøm ",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black


            )
            Text(
                text = "& Sparing",
                style = MaterialTheme.typography.bodyLarge,
                color = YellowText
            )
        }

    }
}

@Composable
fun SavingsCard() {

    Card(
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, YellowBorder, shape = RoundedCornerShape(20.dp))
            .height(250.dp)
            .width(395.dp),
        colors = CardDefaults.cardColors( containerColor = Light )
    ) {
       Column(
           Modifier.fillMaxWidth(),
           horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Row(
               Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
               horizontalArrangement = Arrangement.Center
           ) {
               Text(
                   text = "Strøm ",
                   style = MaterialTheme.typography.bodyLarge,
                   color = Color.Black,
                   modifier = Modifier.padding(top = 15.dp)

               )
               Text(
                   text = "& Sparing",
                   style = MaterialTheme.typography.bodyLarge,
                   color = YellowText,
                   modifier = Modifier.padding(top = 15.dp)
               )
           }

           Spacer(Modifier.padding(5.dp))

           TimeSpan()

           Spacer(Modifier.padding(5.dp))


           Row  {
               Spacer(Modifier.padding(5.dp))

               WithoutSolarPanels()
               Spacer(Modifier.padding(5.dp))

               TotalSavings()
               Spacer(Modifier.padding(5.dp))

               WithSolarPanels()
           }
       }
    }
}

@Composable
fun TimeSpan() {
    Card(
        modifier = Modifier
            .height(20.dp)
            .width(325.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE4C283))

    ) {

    }

}
@Composable
fun WithoutSolarPanels() {
    Box(
        modifier = Modifier
            .offset(y = 10.dp)
            .border(1.dp, YellowerBorder, shape = RoundedCornerShape(20.dp))
    ) {
        Card(
            modifier = Modifier
                .height(100.dp)
                .width(85.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Panels)
        ) {

            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly

            ) {
                Image (
                    painter = painterResource(R.drawable.withoutsolar),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )

                Text(
                    text = "4003 NOK",
                    style = MaterialTheme.typography.bodySmall,
                )

            }

        }
    }
}


@Composable
fun TotalSavings() {
    Box(
        modifier = Modifier
            .border(1.dp, YellowerBorder, shape = RoundedCornerShape(20.dp))
    ) {
        Card(
            modifier = Modifier
                .height(120.dp)
                .width(85.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SavingsYellow)
        ) {

            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly

            ) {

                Text(
                    text = "Spart",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                Image (
                    painter = painterResource(R.drawable.coin),
                    contentDescription = null,
                    modifier = Modifier.size(55.dp)
                )


                Text(
                    text = "500 NOK",
                    style = MaterialTheme.typography.bodySmall,
                )

            }
        }
    }
}


@Composable
fun WithSolarPanels() {
    Box(
        modifier = Modifier
            .offset(y = 10.dp)
            .border(1.dp, YellowerBorder, shape = RoundedCornerShape(20.dp))
    ) {
        Card(
            modifier = Modifier
                .height(100.dp)
                .width(85.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Panels)
        ) {

            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly

            ) {
                Image (
                    painter = painterResource(R.drawable.solar),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )


                Text(
                    text = "3500 NOK",
                    style = MaterialTheme.typography.bodySmall,
                )

            }
        }
    }
}


@Composable
fun WeatherCard(navController: NavController) {

    Card(
        modifier = Modifier
            .height(80.dp)
            .width(395.dp)
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


@Preview
@Composable
fun PreviewSavingsCard() {
    SavingsCard()
}