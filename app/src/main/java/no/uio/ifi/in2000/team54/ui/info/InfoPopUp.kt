package no.uio.ifi.in2000.team54.ui.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.info.FactDialog
import no.uio.ifi.in2000.team54.ui.theme.Butter
import no.uio.ifi.in2000.team54.ui.theme.YellowText

@Composable
fun FactDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(
                modifier = Modifier
                    .height(391.dp)
                    .width(325.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Butter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(13.dp)
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
                        Spacer(Modifier.height(3.dp))
                        Image(
                            painter = painterResource(R.drawable.panel_info),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(120.dp)
                                .width(250.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Spacer(Modifier.height(11.dp))
                    }

                    Column(
                        modifier = Modifier
                            .width(300.dp)
                            .padding(start = 10.dp, end = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Solcellepaneler omdanner sollys direkte til " +
                                    "elektrisk energi ved hjelp av halvledermaterialer, " +
                                    "vanligvis silisium.",
                            modifier = Modifier
                                .padding(11.dp)
                                .fillMaxWidth()
                        )

                        Text(
                            text = "Panelene installeres ofte på hustak eller åpne arealer for " +
                                    "å fange mest mulig sollys.",
                            modifier = Modifier
                                .padding(11.dp)
                                .fillMaxWidth()
                        )

                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Butter,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = YellowText,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Lukk",
                        color = YellowText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 21.sp
                    )
                }
            }
        }

    }
}

@Composable
fun SolarDialog(onDismissRequest: () -> Unit) {
    val scroll= rememberScrollState()

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(
                modifier = Modifier
                    .height(490.dp)
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
                        modifier = Modifier.fillMaxWidth().verticalScroll(scroll)
                    ) {
                        Text(
                            text = "Hvorfor er solcellepaneler bra for miljøet?",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,

                        )
                        OutlinedCard(
                            modifier = Modifier
                                .padding(5.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .width(395.dp)
                                .height(258.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                contentColor = Color.Black,
                                containerColor = Color.White,
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(10.dp)
                            ) {

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Fornybar energi: ")
                                        }
                                        append("Solenergi er uuttømmelig og forurenser ikke under bruk.")
                                    }
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Redusert CO₂-utslipp: ")
                                        }
                                        append("Mindre behov for strøm fra fossile kilder som kull og gass.")
                                    }
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Lavt vedlikehold: ")
                                        }
                                        append("Har lang levetid (typisk 25–30 år) og krever lite vedlikehold.")
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.padding(3.dp))

                        Text(
                            text = "Hvorfor er solcellepaneler bra for deg?",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        OutlinedCard(
                            modifier = Modifier
                                .padding(5.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .width(395.dp)
                                .height(336.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                contentColor = Color.Black,
                                containerColor = Color.White,
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(10.dp)
                            ) {

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Lavere strømregninger: : ")
                                        }
                                        append("Man produserer egen strøm og kjøper mindre fra strømnettet.")
                                    }
                                )
                                Spacer(Modifier.height(9.dp))

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Økt selvforsyning: ")
                                        }
                                        append("Reduserer avhengigheten av strømleverandører og svingende strømpriser.")
                                    }
                                )
                                Spacer(Modifier.height(9.dp))

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Støtteordninger: ")
                                        }
                                        append("Mange land (inkl. Norge) tilbyr økonomisk støtte for installasjon.")
                                    }
                                )
                                Spacer(Modifier.height(9.dp))

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Potensiell verdistigning på bolig: ")
                                        }
                                        append("Hus med solcelleanlegg kan være mer attraktive for kjøpere.")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Butter,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = YellowText,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Lukk",
                        color = YellowText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 21.sp
                    )
                }
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun DialogPreview() {
    var showDialog by remember { mutableStateOf(false) }
    FactDialog(onDismissRequest = { showDialog = false })
}