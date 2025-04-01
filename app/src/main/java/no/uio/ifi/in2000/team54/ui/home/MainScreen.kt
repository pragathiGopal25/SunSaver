package no.uio.ifi.in2000.team54.ui.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team54.ui.home.pages.SettingsScreen
import no.uio.ifi.in2000.team54.ui.home.pages.StatScreen
import no.uio.ifi.in2000.team54.ui.home.pages.WeatherScreen
import no.uio.ifi.in2000.team54.ui.managesolararray.ManageSolarArrayScreen
import no.uio.ifi.in2000.team54.ui.managesolararray.ManageSolarArrayViewModel
import no.uio.ifi.in2000.team54.ui.theme.Background

@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val viewModel = ManageSolarArrayViewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute.equals("managesolararray")) {
                return@Scaffold
            }

            NavBar(navController)
        }

    ) { innerpadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerpadding)
        ) {
            composable("home") { HomeScreen() }
            composable("stats") { StatScreen() }
            composable("managesolararray") { ManageSolarArrayScreen(viewModel, navController) }
            composable("weather") { WeatherScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}

@Composable
fun HomeScreen() {
    //val navController = rememberNavController()
    val homeScreenViewModel = HomeScreenViewModel()
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        HomeScreenTopBar()
        PropertyCard()
        ElectricityCard(viewModel = homeScreenViewModel)
        WeatherCard()
    }
}