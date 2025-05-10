package no.uio.ifi.in2000.team54.ui.info


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.theme.Astra
import no.uio.ifi.in2000.team54.ui.theme.RipeLemon


@Composable
fun InfoScreen(navController: NavController) {
    var showFactDialog by remember { mutableStateOf(false) }
    var showSolarDialog by remember { mutableStateOf(false) }
    var showTutorialDialog by remember { mutableStateOf(false) }

    val scroll = rememberScrollState()

    Box(
        Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.sun),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(scroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoScreenTopBar()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lær mer om solcellepaneler \n og solenergi.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(
                    "Solcellepaneler",
                    "Finn ut mer om hvordan solcellepaneler fungerer.",
                    R.drawable.solar_panel,
                    modifier = Modifier.weight(1f)
                ) { showFactDialog = true }

                InfoCard(
                    "Leverandører",
                    "Liste over leverandører som tilbyr solcelleanlegg.",
                    R.drawable.handshake,
                    modifier = Modifier.weight(1f)
                ) { navController.navigate("suppliers") }
            }
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(
                    "Fordeler med solenergi",
                    "Utforsk fordeler ved å installere solcelleanlegg.",
                    R.drawable.graph,
                    modifier = Modifier.weight(1f)
                ) { showSolarDialog = true }
                InfoCard(
                    "Kom i gang!",
                    "Bli en profesjonell SunSaver.",
                    R.drawable.todo_list,
                    modifier = Modifier.weight(1f)
                ) { showTutorialDialog = true }
            }
        }
    }
    if (showFactDialog) {
        FactDialog(onDismissRequest = { showFactDialog = false })
    }

    if (showSolarDialog) {
        SolarDialog(onDismissRequest = { showSolarDialog = false })
    }
    if (showTutorialDialog) {
        TutorialDialog(onDismissRequest = { showTutorialDialog = false })
    }

}

@Composable
fun InfoCard(name: String, description: String, icon: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard(
        modifier = modifier
            .height(200.dp)
            .width(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, RipeLemon),
        colors = CardDefaults.outlinedCardColors(containerColor = Astra)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(75.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun InfoScreenTopBar() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 23.dp)
            .semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.padding(top = 23.dp))
        Text(
            text = "Velkommen til",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "SunSavers",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = RipeLemon
        )
        Text(
            text = "Informasjonsside",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

