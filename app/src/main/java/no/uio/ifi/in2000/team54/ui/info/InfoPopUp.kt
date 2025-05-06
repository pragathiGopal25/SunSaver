package no.uio.ifi.in2000.team54.ui.info

import android.R.attr.contentDescription
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mapbox.maps.extension.style.style
import io.ktor.websocket.Frame
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.theme.Butter
import no.uio.ifi.in2000.team54.ui.theme.CloseBorder
import no.uio.ifi.in2000.team54.ui.theme.LightOrange
import no.uio.ifi.in2000.team54.ui.theme.YellowNav
import no.uio.ifi.in2000.team54.ui.theme.YellowText

@Composable
fun PanelDialog(onDismissRequest: () -> Unit) {
    val scroll = rememberScrollState()
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(
                modifier = Modifier
                    .height(394.dp)
                    .width(325.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Butter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Hva er solcellepaneler?",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Image(
                            painter = painterResource(R.drawable.panel_info),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(193.dp)
                                .width(247.dp)
                        )
                        Spacer(Modifier.padding(3.dp))
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scroll)
                            .padding(top = 8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Solcellepaneler omdanner sollys direkte til " +
                                    "elektrisk energi ved hjelp av halvledermaterialer, " +
                                    "vanligvis silisium.",
                            modifier = Modifier
                                .padding(9.dp)
                                .fillMaxWidth()
                        )

                        Text(
                            text = "Panelene installeres ofte på hustak eller åpne arealer for " +
                                    "å fange mest mulig sollys.",
                            modifier = Modifier
                                .padding(9.dp)
                                .fillMaxWidth()
                        )
                        Text(
                            text = "Solenergien brukes enten direkte hjemmet eller mates inn i" +
                                    " strømnettet.",
                            modifier = Modifier
                                .padding(9.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }

            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(
                        width = 2.dp,
                        color = YellowText,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Text(
                    text = "Lukk",
                    color = YellowText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
        }

    }
}



@Preview(showBackground = true)
@Composable
fun PanelPreview() {
    var showPanelDialog by remember { mutableStateOf(false) }
    PanelDialog(onDismissRequest = { showPanelDialog = false })
}
