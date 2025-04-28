package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.ui.theme.Background
import no.uio.ifi.in2000.team54.ui.theme.DarkYellow
import no.uio.ifi.in2000.team54.ui.theme.GreyText
import no.uio.ifi.in2000.team54.ui.theme.Light
import no.uio.ifi.in2000.team54.ui.theme.LightOrange
import no.uio.ifi.in2000.team54.ui.theme.Lighter
import no.uio.ifi.in2000.team54.ui.theme.WeatherBlue
import no.uio.ifi.in2000.team54.ui.theme.WeatherBorder
import no.uio.ifi.in2000.team54.ui.theme.YellowBorder
import no.uio.ifi.in2000.team54.ui.theme.YellowText
import no.uio.ifi.in2000.team54.ui.theme.YellowerBorder

@Composable
fun HomeScreen(homeViewModel: HomeViewModel, navController: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        HomeScreenTopBar()
        SolarArrayList(homeViewModel, navController)
        SwitchContent(homeViewModel)
        WeatherCard(navController)
    }
}

@Composable
fun GreetingMessage() {
    Text(
        text = getGreeting(),
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 18.sp
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
            Column {
                GreetingMessage()
                Row(Modifier.padding(top = 13.dp)) {
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

@Composable
fun SolarArrayList(homeViewModel: HomeViewModel, navController: NavController) {
    val solarArrays = homeViewModel.solarArrays.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Row {
            if (solarArrays.value.isEmpty()) {
                NoSolarArrayCard()
            } else {
                solarArrays.value.forEach {
                    SolarArrayCard(it, homeViewModel, navController)
                }
            }
        }
    }
}

@Composable
fun SolarArrayCard(
    solarArray: SolarArray,
    viewModel: HomeViewModel,
    navController: NavController
) {
    val uiState by viewModel.homeUiState.collectAsState()
    val baseModifier = Modifier
        .width(200.dp)
        .height(250.dp)
        .padding(15.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(LightOrange)
        .clickable { viewModel.selectSolarArray(solarArray) }
    Box(
        modifier = Modifier
            .then(
                if (solarArray == uiState.selectedSolarArray) {
                    baseModifier.border(4.dp, YellowerBorder, shape = RoundedCornerShape(20.dp))
                } else {
                    baseModifier.border(1.dp, YellowBorder, shape = RoundedCornerShape(20.dp))
                }
            ),
    ) {
        Column {
            IconButton(
                onClick = {
                    navController.navigate("editsolararrays/${solarArray.name}")
                },
                modifier = Modifier
                    .background(LightOrange)
                    .padding(top = 6.dp, end = 4.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .align(Alignment.End)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.circle),
                    contentDescription = "Redigere Anlegg",
                    tint = Color.Unspecified // if you don't want to tint it
                )
            }
            Image(
                painter = painterResource(R.drawable.house),
                contentDescription = "Hus med solcelleplaneter",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(end = 15.dp, start = 15.dp, bottom = 10.dp, top = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Lighter)
                    .padding(15.dp)
            )

            Text(
                text = solarArray.name,
                fontSize = 20.sp,
                color = GreyText,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )
        }
    }
}

@Composable
fun NoSolarArrayCard() {
    Column(
        modifier = Modifier
            .width(200.dp)
            .height(220.dp)
            .padding(15.dp)
            .drawBehind {
                drawRoundRect(
                    color = YellowBorder,
                    cornerRadius = CornerRadius(20.dp.toPx()),
                    style = Stroke(
                        width = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }
            .padding(18.dp)
    ) {
        Text(
            "Ingen anlegg",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
        )

        Spacer(Modifier.padding(13.dp))

        Text(
            "Legg til et nytt anlegg ved å trykke på + symbolet på bunnen av skjermen.",
            fontSize = 12.sp,
            color = Color.Gray,
        )
    }
}

@Composable
fun SwitchContent(homeViewModel: HomeViewModel) {
    var isFlipped by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .height(300.dp)
            .width(409.dp),
        contentAlignment = Alignment.Center
    ) {
        ElectricityCard(
            flipped = isFlipped,
            onFlipClick = { isFlipped = !isFlipped }
        ) {
            if (!isFlipped) {
                GraphContainer(viewModel = homeViewModel)

            } else {
                PriceContainer(viewModel = homeViewModel)
            }
        }
    }
}


@Composable
private fun NextCard(flipped: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {

    if (!flipped)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Neste kort",
            tint = DarkYellow,
            modifier = modifier
                .padding(10.dp)
                .size(35.dp)
                .clickable { onClick() }
                .padding(7.dp)
        )

    // Skal kun vises før neste kort er i fokus

}

@Composable
private fun PreviousCard(flipped: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {

    if (flipped)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Forrige kort",
            tint = DarkYellow,
            modifier = modifier
                .padding(10.dp)
                .size(35.dp)
                .clickable { onClick() }
                .padding(7.dp)
        )

    // Skal kun vises når det er mulig for bruker å navigere seg tilbake
}


@Preview(showBackground = true)
@Composable
fun PreviewSwitchContent() {

    val hvm = HomeViewModel()
    SwitchContent(hvm)
}


@Composable
fun ElectricityCard(

    flipped: Boolean,
    onFlipClick: () -> Unit,
    content: @Composable () -> Unit

) {
    Card(
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, YellowBorder, shape = RoundedCornerShape(20.dp))
            .height(262.dp)
            .width(395.dp),

        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Black,
            containerColor = Light,
        )
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            NextCard(
                flipped = flipped,
                onClick = onFlipClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            )

            PreviousCard(
                flipped = flipped,
                onClick = onFlipClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "Strømutgifter ",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "& Sparing",
                        style = MaterialTheme.typography.bodyLarge,
                        color = YellowText,
                        fontSize = 18.sp
                    )
                }
                content()
            }
        }
    }
}

@Composable
fun WeatherCard(navController: NavController) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(90.dp)
            .width(409.dp)
            .padding(15.dp)
            .border(1.dp, WeatherBorder, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(WeatherBlue)
            .clickable { navController.navigate("weather") }
    ) {
        Row(
            modifier = Modifier
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
                .offset(x = (-15).dp)
        )
    }
}