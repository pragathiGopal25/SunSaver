package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.theme.Lighter
import no.uio.ifi.in2000.team54.ui.theme.YellowNav

@Composable
fun NavBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {

    var selectedIndex by remember { mutableIntStateOf(0) }

    val navIconList = listOf(
        NavItem("Hjem", R.drawable.homeclicked, R.drawable.homeunclicked),
        NavItem("Statistikk", R.drawable.statsclicked, R.drawable.statsunclicked),
        NavItem("Vær", R.drawable.weatherclicked, R.drawable.weatherunclicked),
        NavItem("Innstillinger", R.drawable.settingsclicked, R.drawable.settingsunclicked)
    )

    Scaffold (
        Modifier.fillMaxSize().background(YellowNav),
        bottomBar = {
            NavigationBar (
                containerColor = YellowNav,
                modifier = Modifier
                    .height(90.dp)

            ){
                navIconList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    if (selectedIndex == index) navItem.selected else navItem.unselected
                                ),
                                contentDescription = navItem.label,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(top = 2.dp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Lighter),
                        label = { Text(navItem.label) }
                    )

                }
            }
        }
    ){
            innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding),selectedIndex)
    }
}
