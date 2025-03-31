package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.theme.YellowNav

@Composable
fun NavBar(navController: NavController) {

    var selectedIndex by remember { mutableIntStateOf(0) }

    val navIconList = listOf(
        NavItem("Hjem", R.drawable.homeclicked, R.drawable.homeunclicked, "home"),
        NavItem("Statistikk", R.drawable.statsclicked, R.drawable.statsunclicked, "stats"),
        NavItem("", R.drawable.addproperty, R.drawable.addproperty, "managesolararray"),
        NavItem("Vær", R.drawable.weatherclicked, R.drawable.weatherunclicked, "weather"),
        NavItem("Innstillinger", R.drawable.settingsclicked, R.drawable.settingsunclicked, "settings")
    )

    NavigationBar(

        containerColor = YellowNav,
        modifier = Modifier.height(90.dp)
    ) {
        navIconList.forEachIndexed { index, navItem ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = {
                    selectedIndex = index
                    navController.navigate(navItem.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(
                            if (selectedIndex == index) navItem.selected else navItem.unselected
                        ),
                        contentDescription = navItem.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(navItem.label) }

            )
        }
    }
}

