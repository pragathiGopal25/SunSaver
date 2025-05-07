package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.theme.LightOrange
import no.uio.ifi.in2000.team54.ui.theme.YellowNav

@Composable
fun NavBar(navController: NavController) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val navIconList = listOf(
        NavItem(R.drawable.homeclicked, R.drawable.homeunclicked, "home"),
        NavItem(R.drawable.info_clicked, R.drawable.info_unclicked, "info")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.addproperty),
            contentDescription = "Add solar array",
            tint = Color.Unspecified,
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .padding()
                .align(Alignment.Center)
                .absoluteOffset(y = (-20).dp)
                .zIndex(1f)
                .clickable {
                    navController.navigate("managesolararray")
                }
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                .background(YellowNav)
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
                            contentDescription = navItem.route,
                            modifier = Modifier.size(23.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = if (selectedIndex == index) LightOrange else YellowNav),
                )
            }
        }
    }
}

