package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.ui.theme.Background
import no.uio.ifi.in2000.team54.ui.theme.GreyText
import no.uio.ifi.in2000.team54.ui.theme.Light
import no.uio.ifi.in2000.team54.ui.theme.LightOrange
import no.uio.ifi.in2000.team54.ui.theme.Lighter
import no.uio.ifi.in2000.team54.ui.theme.YellowBorder
import no.uio.ifi.in2000.team54.ui.theme.YellowText
import no.uio.ifi.in2000.team54.ui.theme.YellowerBorder

@Composable
fun HomeScreen(homeViewModel: HomeViewModel, navController: NavController) {
    val scroll = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        HomeScreenTopBar()
        Column(
            modifier = Modifier
                .verticalScroll(scroll)
        ) {
            SolarArrayList(homeViewModel, navController)
            SelectedSolarArrayTitle(homeViewModel)
            //Putter strømproduksjon øverst fordi den laster inn mye raskere
            HomeCard(
                name = "Strømproduksjon", modifier = Modifier.height(302.dp),
                content = { GraphContainer(viewModel = homeViewModel) }
            )
            HomeCard(
                name = "Sparing", modifier = Modifier,
                content = { PriceContainer(viewModel = homeViewModel) }
            )
        }
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
    val homeUiState = homeViewModel.homeUiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Row {
            if (homeUiState.value.solarArrays.isEmpty()) {
                NoSolarArrayCard()
            } else {
                homeUiState.value.solarArrays.forEach {
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
fun SelectedSolarArrayTitle(viewModel: HomeViewModel) {
    val uiState by viewModel.homeUiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .border(1.dp, YellowerBorder, RoundedCornerShape(100))
                .clip(RoundedCornerShape(100))
                .background(LightOrange)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = "Valgt anlegg: ",
            )
            Text(
                text = uiState.selectedSolarArray?.name ?: "Ingen",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HomeCard(
    name: String,
    content: @Composable () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, YellowBorder, shape = RoundedCornerShape(20.dp))
            .width(395.dp),

        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Black,
            containerColor = Light,
        )
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.padding(10.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp
                    )
                }
                content()
            }
        }
    }
}