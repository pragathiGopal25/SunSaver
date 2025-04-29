package no.uio.ifi.in2000.team54.ui.managesolararray

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.enums.SolarPanelType
import no.uio.ifi.in2000.team54.ui.theme.DarkYellow
import no.uio.ifi.in2000.team54.ui.theme.Light
import no.uio.ifi.in2000.team54.util.calculateSubsidy
import java.util.Locale

@Composable
fun PriceSummaryCard(
    solarPanelType: SolarPanelType,
    roofSections: SnapshotStateList<RoofSection>,

    ) {
    val totalPanels = roofSections.sumOf { it.panels }
    val grossPrice = solarPanelType.totalPrice(totalPanels)
    val subsidy = calculateSubsidy(solarPanelType, totalPanels)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Light)
            .border(1.dp, DarkYellow, RoundedCornerShape(15.dp))
            .padding(horizontal = 40.dp, vertical = 15.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .width(150.dp)
                    .height(130.dp)
            ) {
                Text(
                    text = "Oversikt",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Column {
                    Text(
                        text = "Bruttopris",
                        color = Color.Black,
                        fontSize = 14.sp,
                    )
                    PriceText(grossPrice)
                }
                Column {
                    Text(
                        text = "Støtte fra staten",
                        color = Color.Black,
                        fontSize = 14.sp,
                    )
                    PriceText(subsidy)
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Totalpris",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Image(
                    painter = painterResource(R.drawable.planet),
                    contentDescription = "Planet med solcellepanel",
                    modifier = Modifier
                        .size(80.dp)
                )
                PriceText(grossPrice - subsidy)
            }
        }
    }
}

@Composable
private fun PriceText(price: Double) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "%,d".format(price.toInt(), Locale.GERMANY).replace(",", " "),
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "NOK",
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}