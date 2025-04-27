package no.uio.ifi.in2000.team54.ui.home


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team54.ui.composables.Snackbar
import no.uio.ifi.in2000.team54.ui.home.pages.SettingsScreen
import no.uio.ifi.in2000.team54.ui.home.pages.StatScreen
import no.uio.ifi.in2000.team54.ui.home.pages.WeatherScreen
import no.uio.ifi.in2000.team54.ui.managesolararray.ManageSolarArrayScreen
import no.uio.ifi.in2000.team54.ui.managesolararray.ManageSolarArrayViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val manageSolarArrayViewModel = remember { ManageSolarArrayViewModel() }
    val homeViewModel = remember { HomeViewModel() }
    val snackbarState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { Snackbar(snackbarState) },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute.equals("managesolararray") || currentRoute.equals("EditSolarPanels/{arrayName}")) {
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
            composable("home") { HomeScreen(homeViewModel = homeViewModel, navController = navController) }
            composable("stats") { StatScreen() }
            composable("managesolararray") { ManageSolarArrayScreen(manageSolarArrayViewModel, navController, snackbarState, homeViewModel) }
            composable("weather") { WeatherScreen() }
            composable("settings") { SettingsScreen() }

            composable(
                "EditSolarPanels/{arrayName}",
                arguments = listOf(navArgument("arrayName") { type = NavType.StringType })
            ) { backStackEntry ->
                val arrayName = backStackEntry.arguments?.getString("arrayName") ?: ""
                ManageSolarArrayScreen(manageSolarArrayViewModel, navController, snackbarState, homeViewModel, arrayName)
            }
        }
    }
}

/*
dependencies:
implementation(libs.androidx.compose.material)

libs.versions.toml:
   composeMaterial = "1.4.0"
   androidx-compose-material = { group = "androidx.wear.compose", name = "compose-material", version.ref = "composeMaterial" }
*/
