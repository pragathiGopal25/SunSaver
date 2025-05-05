package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import no.uio.ifi.in2000.team54.ui.composables.Snackbar
import no.uio.ifi.in2000.team54.ui.info.InfoScreen
import no.uio.ifi.in2000.team54.ui.managesolararray.ManageSolarArrayScreen
import no.uio.ifi.in2000.team54.ui.managesolararray.ManageSolarArrayViewModel
import no.uio.ifi.in2000.team54.ui.network.NetworkObserver

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val networkObserver = remember { NetworkObserver(context) }

    val navController = rememberNavController()

    val manageSolarArrayViewModel = remember { ManageSolarArrayViewModel() }
    val homeViewModel = remember { HomeViewModel(networkObserver) }

    val snackbarState = remember { SnackbarHostState() }
    val isOnline by homeViewModel.isOnline.collectAsState(initial = true)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { Snackbar(snackbarState) },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute.equals("managesolararray") || currentRoute.equals("editsolararrays/{arrayId}")) {
                return@Scaffold
            }

            NavBar(navController)
        }

    ) { innerpadding ->
        LaunchedEffect(isOnline) {
            if (isOnline) {
                snackbarState.currentSnackbarData?.dismiss()
            } else {
                snackbarState.showSnackbar("Ingen internettforbindelse", duration = SnackbarDuration.Indefinite)
            }
        }

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerpadding)
        ) {
            composable("home") { HomeScreen(homeViewModel = homeViewModel, navController = navController) }
            composable("info") { InfoScreen() }
            composable("managesolararray") { ManageSolarArrayScreen(manageSolarArrayViewModel, navController, snackbarState) }

            composable(
                "editsolararrays/{arrayId}",
                arguments = listOf(navArgument("arrayId") { type = NavType.LongType })
            ) { backStackEntry ->
                val arrayId = backStackEntry.arguments?.getLong("arrayId") ?: -1L
                ManageSolarArrayScreen(manageSolarArrayViewModel, navController, snackbarState, arrayId)
            }
        }
    }
}