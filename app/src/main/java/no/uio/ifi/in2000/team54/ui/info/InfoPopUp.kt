package no.uio.ifi.in2000.team54.ui.info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.ui.theme.Astra
import no.uio.ifi.in2000.team54.ui.theme.RipeLemon
import no.uio.ifi.in2000.team54.ui.theme.VistaWhite

@Composable
fun FactDialog(onDismissRequest: () -> Unit) {
    val scroll = rememberScrollState()

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(
                modifier = Modifier
                    .height(391.dp)
                    .width(325.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, RipeLemon),
                colors = CardDefaults.outlinedCardColors(containerColor = Astra)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                        Text(
                            text = "Hva er solcellepaneler?",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(3.dp))
                    }

                    Column(
                        modifier = Modifier
                            .width(300.dp)
                            .padding(start = 10.dp, end = 7.dp)
                            .verticalScroll(scroll),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Solcellepaneler absorberer sollys og " +
                                    "genererer energi ved hjelp av halvledermaterialer, " +
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
        }
    }
}

@Composable
fun SolarDialog(onDismissRequest: () -> Unit) {
    val scroll = rememberScrollState()

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(
                modifier = Modifier
                    .height(490.dp)
                    .width(325.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, RipeLemon),
                colors = CardDefaults.outlinedCardColors(containerColor = Astra)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scroll)
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
                                        append("Solenergi er uuttømmelig og slipper ingen klimagasser i luften.")
                                    }
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Redusert CO₂-utslipp: ")
                                        }
                                        append("Solenergi har mindre påvirkning på miljøet enn andre metoder for kraftproduksjon.")
                                    }
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Levetid: ")
                                        }
                                        append("De fleste solceller har levetid på 25–30 år.")
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
                                .width(395.dp),
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
                                            append("Lavere strømregninger: ")
                                        }
                                        append("Spar penger ved å produsere din egen strøm og redusere behovet for å kjøpe fra strømleverandører.")
                                    }
                                )
                                Spacer(Modifier.height(9.dp))

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Økt selvforsyning: ")
                                        }
                                        append("Reduserer avhengigheten av strømleverandører og gir rom for investering i eget batterilagringssystem")
                                    }
                                )
                                Spacer(Modifier.height(9.dp))

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Støtteordninger: ")
                                        }
                                        append("Norge tilbyr økonomisk støtte for installasjon.")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TutorialDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(
                modifier = Modifier
                    .height(400.dp)
                    .width(350.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, RipeLemon),
                colors = CardDefaults.outlinedCardColors(containerColor = Astra)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Slik bruker du SunSaver!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp)
                                .padding(start = 1.dp, end = 1.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(containerColor = VistaWhite)
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.add_button),
                                    contentDescription = "Knapp for å legge til eiendom",
                                    modifier = Modifier.size(45.dp)
                                )
                                Spacer(Modifier.height(7.dp))
                                Text(
                                    text = "Legg til et anlegg med pluss-knappen nederst på hjemskjermen.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp)
                                .padding(start = 1.dp, end = 1.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(containerColor = VistaWhite)
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.sok),
                                    contentDescription = "Knapp for å søke opp en adresse.",
                                    modifier = Modifier.size(45.dp)
                                )
                                Spacer(Modifier.height(7.dp))
                                Text(
                                    text = "Søk opp en adresse.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(containerColor = VistaWhite)
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = VistaWhite),
                                    border = BorderStroke(1.dp, RipeLemon),
                                    modifier = Modifier
                                        .height(25.dp)
                                        .width(70.dp)
                                ) {
                                    Text(
                                        text = "Lagre",
                                        color = Color.Black,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 1.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(Modifier.height(7.dp))

                                Text(
                                    text = "Trykk på denne knappen for å lagre solcelleanlegget.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(130.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(containerColor = VistaWhite)
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.edit_icon),
                                    contentDescription = "Knapp for å redigere valgte takflater.",
                                    modifier = Modifier.size(45.dp)
                                )
                                Spacer(Modifier.height(7.dp))
                                Text(
                                    text = "Du kan også redigere og endre de valgte takflatene.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}
