package no.uio.ifi.in2000.team54.ui.managesolararray

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.ui.theme.DarkYellow
import no.uio.ifi.in2000.team54.ui.theme.Light
import no.uio.ifi.in2000.team54.ui.theme.Red

@Composable
fun RoofSectionsList(
    roofSections: SnapshotStateList<RoofSection>,
    onRemove: () -> Unit,
    onEdit: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (roofSections.isEmpty()) {
                NoRoofSectionCard()
            } else {
                roofSections.forEachIndexed { index, section ->
                    RoofSectionCard(
                        section,
                        index,
                        onRemove = {
                            roofSections.remove(section)
                            onRemove()
                        },
                        onEdit = { onEdit(roofSections.indexOf(section)) })
                }
            }
        }
    }
}

@Composable
private fun RoofSectionCard(section: RoofSection, index: Int, onRemove: () -> Unit, onEdit: () -> Unit) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(135.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Light)
            .border(1.dp, DarkYellow, RoundedCornerShape(15.dp))
            .padding(10.dp)
            .clickable { onEdit() }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Takflate ${index + 1}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkYellow
                )
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Slett takflate",
                    tint = Red,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            onRemove()
                        }
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                RoofSectionRow("Areal", "%.1fm²".format(section.area))
                RoofSectionRow("Helning", "%.1f°".format(section.incline))
                RoofSectionRow("Paneler", section.panels.toString())
            }
        }
    }
}

@Composable
private fun NoRoofSectionCard() {
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(135.dp)
            .drawBehind {
                drawRoundRect(
                    color = DarkYellow,
                    cornerRadius = CornerRadius(15.dp.toPx()),
                    style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                )
            }
            .padding(10.dp)
    ) {
        Text(
            "Søk på en adresse og velg en takflate i kartet, eller legg inn en manuelt i boksen under.",
            fontSize = 12.sp,
            color = Color.Gray,
        )
    }
}

@Composable
private fun RoofSectionRow(name: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )
    }
}