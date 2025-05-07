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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import no.uio.ifi.in2000.team54.ui.composables.Snackbar
import no.uio.ifi.in2000.team54.ui.info.InfoScreen
import no.uio.ifi.in2000.team54.ui.managesolararray.ManageSolarArrayScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val homeViewModel = hiltViewModel<HomeViewModel>()

    val snackbarState = remember { SnackbarHostState() }
    val isOnline by homeViewModel.isOnline.collectAsState(initial = true)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
            composable("home") {
                HomeScreen(
                    navController = navController,
                    snackbarHostState = snackbarState
                )
            }
            composable("home") {
                HomeScreen(
                    navController = navController,
                    snackbarHostState = snackbarState
                )
            }
            composable("info") { InfoScreen() }
            composable("managesolararray") { ManageSolarArrayScreen(navController, snackbarState) }

            composable(
                "editsolararrays/{arrayId}",
                arguments = listOf(navArgument("arrayId") { type = NavType.LongType })
            ) { backStackEntry ->
                val arrayId = backStackEntry.arguments?.getLong("arrayId") ?: -1L
                ManageSolarArrayScreen(navController, snackbarState, arrayId)
            }
        }

        Snackbar(snackbarState)
    }
}