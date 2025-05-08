package no.uio.ifi.in2000.team54.ui.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.theme.BarleyWhite
import no.uio.ifi.in2000.team54.ui.theme.FjordkraftOrange
import no.uio.ifi.in2000.team54.ui.theme.RipeLemon
import no.uio.ifi.in2000.team54.ui.theme.SolcelleCard
import no.uio.ifi.in2000.team54.ui.theme.SupplierYellow

@Composable
fun SupplierScreenTopBar() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 23.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.padding(top = 10.dp))

        Text(
            text = "Leverandører",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = RipeLemon
        )
        Text(
            text = "som tilbyr solcelleanlegg",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )
    }
}

@Composable
fun SupplierScreen(navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp)
    ) {
        BackButton(navController)
        Image(
            painter = painterResource(R.drawable.sun),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            SupplierScreenTopBar()

            Column {
                Otovo()
                Spacer(Modifier.height(30.dp))

                Solcellespesialisten()
                Spacer(Modifier.height(30.dp))

                Solcelleleverandoren()
                Spacer(Modifier.height(30.dp))

                Fjordkraft()
                Spacer(Modifier.height(30.dp))

                Solcelle()
                Spacer(Modifier.height(15.dp))
            }
        }
    }
}


@Composable
fun Otovo() {
    OutlinedCard(
        modifier = Modifier
            .height(108.dp)
            .width(389.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = SupplierYellow)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.otovo),
                contentDescription = "Logo til Otovo - leverandør av solcellepanel.",
                modifier = Modifier
                    .width(163.dp)
                    .height(60.dp)
            )
        }
    }
}


@Composable
fun Solcellespesialisten() {

    OutlinedCard(
        modifier = Modifier
            .height(108.dp)
            .width(389.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = SolcelleCard)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.solcellespesialisten),
                contentDescription = "Logo til Solcellespesialisten - leverandør av solcellepanel.",
                modifier = Modifier
                    .width(80.dp)
                    .height(60.dp)
            )
            Text(
                text = "Solcellespesialisten",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}


@Composable
fun Solcelleleverandoren() {

    OutlinedCard(
        modifier = Modifier
            .height(108.dp)
            .width(389.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(R.drawable.solcelleleverandoren),
                contentDescription = "Logo til Solcelleleverandøren - leverandør av solcellepanel.",
                modifier = Modifier
                    .padding(start = 17.dp, end = 10.dp)
                    .width(100.dp)
                    .height(60.dp)
            )
            Text(
                text = "Solcelleleverandøren",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}


@Composable
fun Fjordkraft() {

    OutlinedCard(
        modifier = Modifier
            .height(108.dp)
            .width(389.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = FjordkraftOrange)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.fjordkraft),
                contentDescription = "Logo til Fjordkraft - leverandør av solcellepanel.",
                modifier = Modifier
                    .width(290.dp)
                    .height(80.dp)
            )
        }
    }
}


@Composable
fun Solcelle() {

    OutlinedCard(
        modifier = Modifier
            .height(108.dp)
            .width(389.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = SupplierYellow)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.solcelle),
                contentDescription = "Logo til Solcelle - leverandør av solcellepanel.",
                modifier = Modifier
                    .width(290.dp)
                    .height(80.dp)
            )
        }
    }

}


@Composable
private fun BackButton(navController: NavController) {
    Spacer(Modifier.height(10.dp))
    Icon(
        Icons.AutoMirrored.Default.ArrowBack,
        contentDescription = "Gå tilbake",
        tint = RipeLemon,
        modifier = Modifier
            .padding(10.dp)
            .width(35.dp)
            .height(35.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(BarleyWhite)
            .padding(7.dp)
            .clickable { navController.popBackStack() }
    )
}

