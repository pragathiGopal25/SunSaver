package no.uio.ifi.in2000.team54.ui.info


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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.common.shape.rounded
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.info.TutorialDialog
import no.uio.ifi.in2000.team54.ui.theme.Butter
import no.uio.ifi.in2000.team54.ui.theme.YellowText


@Composable
fun InfoScreen(navController: NavController) {

    var showFactDialog by remember { mutableStateOf(false) }
    var showSolarDialog by remember { mutableStateOf(false) }
    var showTutorialDialog by remember { mutableStateOf(false) }
    Box (
        Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.sunny),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            InfoScreenTopBar()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lær mer om solcellepaneler",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            Text(
                text = "og solenergi.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(). padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SolarFacts(modifier = Modifier.weight(1f), onClick = { showFactDialog = true })

                SupplierInfo(modifier = Modifier.weight(1f), onClick = { navController.navigate("supplier")})
            }
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth() .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SolarInfo(modifier = Modifier.weight(1f), onClick = { showSolarDialog = true })
                GetStarted(modifier = Modifier.weight(1f), onClick = { showTutorialDialog = true })
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
fun InfoScreenTopBar() {

    Column (
        Modifier
            .fillMaxWidth()
            .padding(top = 23.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(Modifier.padding(top = 10.dp))
        Text(
            text = "Velkommen til",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "SunSavers",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = YellowText
        )
        Text(
            text = "Informasjonsside",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SolarFacts(modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard( modifier = modifier
        .height(200.dp)
        .width(200.dp)
        .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Butter)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Solcellepaneler",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(R.drawable.solarpanel),
                contentDescription = null,
                modifier = Modifier.size(75.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Finn ut mer om \n" +
                        "hvordan solcellepaneler fungerer.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SupplierInfo(modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard( modifier = modifier
        .height(200.dp)
        .width(200.dp)
        .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Butter)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Leverandører",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(R.drawable.handsshaking),
                contentDescription = "Solcellepanel",
                modifier = Modifier
                    .size(75.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Liste over leverandører \n" +
                        "som tilbyr \n" +
                        "solcelleanlegg.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SolarInfo(modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard( modifier = modifier
        .height(200.dp)
        .width(200.dp)
        .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Butter)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Fordeler med \n" +
                        "solenergi ",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Image(
                painter = painterResource(R.drawable.boardgraph),
                contentDescription = "Fordeler med \n" +
                        "solenergi ",
                modifier = Modifier
                    .size(80.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Utforsk fordeler ved å \n " +
                        "installere solcelleanlegg ",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun GetStarted(modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard( modifier = modifier
        .height(200.dp)
        .width(200.dp)
        .clickable (onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Butter)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Kom i gang! ",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(R.drawable.todolist),
                contentDescription = "Fordeler med \n" +
                        "solenergi ",
                modifier = Modifier
                    .size(75.dp)
            )

            Text(
                text = "Bli en " +
                        " profesjonell SunSaver!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

