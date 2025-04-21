package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.theme.DarkBeige
import no.uio.ifi.in2000.team54.ui.theme.LightestYellow
import no.uio.ifi.in2000.team54.ui.theme.RandomBeige


@Composable
fun ElectricityPriceContainer(viewModel: HomeScreenViewModel) {
    val uiState by viewModel.priceUiState.collectAsState()
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
            Spacer(Modifier.padding(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                ExpensesStatBox(false, "${uiState.realPrice}", R.drawable.withoutsolar, uiState)
                Spacer(Modifier.padding(8.dp))
                ExpensesStatBox(true, "${uiState.saved}", R.drawable.coin, uiState)
                Spacer(Modifier.padding(8.dp))
                ExpensesStatBox(false, "${uiState.solarPrice}", R.drawable.solar, uiState)
            }
            Spacer(Modifier.padding(10.dp))
        }
    }
}

@Composable
fun IndeterminateCircularIndicator(uiState: HomeScreenViewModel.PriceUiState) {
    if (!uiState.loading) return
    CircularProgressIndicator(
        modifier = Modifier.width(23.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
fun ExpensesStatBox(
    main: Boolean,
    text: String,
    image: Int,
    uiState: HomeScreenViewModel.PriceUiState
) {
    if (uiState.error) {
        Text("Det oppstod en feil i beregningen av strømkostnader")
        return
    }
    Box(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .height(if (main) 134.dp else 95.dp)
            .width(if (main) 121.dp else 85.dp)
            .background(if (main) RandomBeige else LightestYellow)
            .border(
                color = DarkBeige,
                width = 1.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = if (main) Alignment.TopCenter else Alignment.Center
    ) {
        Column(
            Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (main) Text("Spart", fontWeight = FontWeight.Bold)
            val img = painterResource(image)
            val size = if (main) 60.dp else 50.dp
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
            IndeterminateCircularIndicator(uiState)
            Text("$text,-")
        }
    }
}

@Composable
fun TimeScopeSegmentedButton(
    viewModel: HomeScreenViewModel,
    uiState: HomeScreenViewModel.PriceUiState
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Dag", "Måned", "År")

    val map = mapOf(
        HomeScreenViewModel.Scope.DAY to 0,
        HomeScreenViewModel.Scope.MONTH to 1,
        HomeScreenViewModel.Scope.YEAR to 2
    )

    if (map[uiState.scope] != selectedIndex) selectedIndex = map[uiState.scope]!!

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    selectedIndex = index
                    viewModel.changeTimeScope(HomeScreenViewModel.Scope.entries[selectedIndex])
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
