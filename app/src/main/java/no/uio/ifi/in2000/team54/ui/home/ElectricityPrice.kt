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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.theme.Beige
import no.uio.ifi.in2000.team54.ui.theme.BrightYellow
import no.uio.ifi.in2000.team54.ui.theme.DarkBeige
import no.uio.ifi.in2000.team54.ui.theme.DarkYellow
import no.uio.ifi.in2000.team54.ui.theme.LightestYellow
import no.uio.ifi.in2000.team54.ui.theme.RandomBeige
import kotlin.math.round


@Composable
fun PriceContainer(viewModel: HomeViewModel) {
    val uiState by viewModel.homeUiState.collectAsState()
    val loadingState by viewModel.priceLoadingState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    if (loadingState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(302.dp), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = DarkYellow,
                modifier = Modifier
                    .width(70.dp)
            )
        }
        return
    } else if (loadingState.loadingMessage != "") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(302.dp), contentAlignment = Alignment.Center
        ) {
            Text(text = loadingState.loadingMessage)
        }
        return
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 8.dp, top = 3.dp, start = 4.dp, end = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                TimeScopeSegmentedButton(viewModel, uiState)
            }
            Spacer(Modifier.padding(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                ExpensesStatBox(false, "${uiState.priceData.realPrice}", R.drawable.withoutsolar)
                Spacer(Modifier.padding(7.dp))
                ExpensesStatBox(true, "${uiState.priceData.saved}", R.drawable.coin)
                Spacer(Modifier.padding(7.dp))
                ExpensesStatBox(false, "${uiState.priceData.solarPrice}", R.drawable.solar)
            }
            Spacer(Modifier.padding(8.dp))
            val timePerspective = mapOf(
                TimeScope.DAY to "denne dagen",
                TimeScope.MONTH to "denne måneden",
                TimeScope.YEAR to "dette året"
            )
            Box(
                Modifier
                    .clickable(onClick = {
                        expanded = !expanded
                    })
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(20.dp))
                    .background(Beige)
                    .padding(2.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(horizontalAlignment = AbsoluteAlignment.Left, modifier = Modifier.padding(2.dp)) {
                    if (!expanded) {
                        Text(
                            text = "Hva betyr dette? Trykk her for mer informasjon",
                            fontWeight = FontWeight.Light
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    if (expanded) {
                        if (uiState.priceData.solarPrice < 0) {
                            Text(
                                """
                                    >Du har produsert et overskudd med strøm, og kan derfor selge tilbake til markedet. Overskuddet du kan selge er markert i den høyre boksen med et negativt tall.
                                    >- Den venstre boksen viser hva du hadde betalt i strømutgifter uten solcellepanel.
                                    >- Boksen i midten forteller deg hva du sparer ved å vise differansen mellom utgiftene. 
                                    >- Den høyre boksen viser hva du hadde betalt i strømutgifter med solcellepanel.
                                    """.trimMargin(">")
                            )
                        } else {
                            Text(
                                """
                                     >Du vil spare ${uiState.priceData.saved} NOK ${timePerspective[uiState.timeScope]}, fordi strømmen i utgangspunktet koster ${uiState.priceData.realPrice} NOK uten solcelleanlegget ditt, mens du vil betale ${uiState.priceData.solarPrice} NOK.
                                    >- Den venstre boksen viser hva du hadde betalt i strømutgifter uten solcellepanel.
                                    >- Boksen i midten forteller deg hva du sparer ved å vise differansen mellom utgiftene. 
                                    >- Den høyre boksen viser hva du hadde betalt i strømutgifter med solcellepanel.
                                    """.trimMargin(">")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpensesStatBox(
    mainBox: Boolean,
    priceAmount: String,
    image: Int,
) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .height(if (mainBox) 155.dp else 110.dp)
            .width(if (mainBox) 126.dp else 99.dp)
            .background(if (mainBox) RandomBeige else LightestYellow)
            .border(
                color = DarkBeige,
                width = 1.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = if (mainBox) Alignment.TopCenter else Alignment.Center
    ) {
        Column(
            Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mainBox) Text("Spart", fontWeight = FontWeight.Bold)
            val img = painterResource(image)
            val size = if (mainBox) 60.dp else 50.dp
            Image(
                modifier = Modifier
                    .shadow(
                        shape = RoundedCornerShape(20.dp),
                        elevation = 0.dp
                    )
                    .size(size)
                    .clip(CircleShape),
                painter = img,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Text(priceAmount)
            Text("NOK")
        }
    }
}

@Composable
fun TimeScopeSegmentedButton(
    viewModel: HomeViewModel,
    uiState: HomeUiState
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Dag", "Måned", "År")

    val map = mapOf(
        TimeScope.DAY to 0,
        TimeScope.MONTH to 1,
        TimeScope.YEAR to 2
    )

    if (map[uiState.timeScope] != selectedIndex) selectedIndex = map[uiState.timeScope]!!

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    selectedIndex = index
                    viewModel.changeTimeScope(TimeScope.entries[selectedIndex])
                },
                selected = index == selectedIndex,
                icon = {},
                colors = SegmentedButtonColors(
                    activeContainerColor = LightestYellow,
                    activeContentColor = Color.Black,
                    activeBorderColor = LightestYellow,
                    inactiveContainerColor = RandomBeige,
                    inactiveContentColor = Color.Gray,
                    inactiveBorderColor = RandomBeige,
                    disabledActiveContainerColor = Color.Red,
                    disabledActiveContentColor = Color.Red,
                    disabledActiveBorderColor = Color.Red,
                    disabledInactiveContainerColor = Color.Red,
                    disabledInactiveContentColor = Color.Red,
                    disabledInactiveBorderColor = Color.Red,
                ),
                label = { Text(text = label) }
            )
        }
    }
}
