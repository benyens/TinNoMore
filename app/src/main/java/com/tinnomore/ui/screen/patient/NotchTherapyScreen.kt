package com.tinnomore.ui.screen.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tinnomore.util.FrequencyPredictor
import com.tinnomore.viewmodel.AudiometryViewModel

/**
 * Pantalla de Notch Therapy.
 * Muestra la frecuencia central configurada y permite iniciar/detener la terapia.
 * (En esta versión, la reproducción de audio es simulada — la integración con
 * MediaPlayer/AudioTrack queda pendiente para v0.3.)
 */
@Composable
fun NotchTherapyScreen(
    patientId: Long,
    onBack: () -> Unit = {},
    vm: AudiometryViewModel = viewModel()
) {
    val predictedFc by vm.predictedFc.collectAsState()
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(patientId) { vm.loadLatestProfile(patientId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        Icon(
            Icons.Default.MusicNote,
            contentDescription = null,
            tint     = Color(0xFF00695C),
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Notch Therapy",
            fontSize   = 24.sp,
            fontWeight = FontWeight.Bold,
            color      = Color(0xFF00695C)
        )

        Text(
            "Terapia de enmascaramiento de frecuencia",
            fontSize  = 13.sp,
            color     = Color.Gray,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // ── Frecuencia configurada ────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
        ) {
            Column(
                modifier            = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Frecuencia central configurada", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF00695C))
                Spacer(Modifier.height(8.dp))
                if (predictedFc != null) {
                    Text(
                        FrequencyPredictor.freqLabel(predictedFc!!),
                        fontSize   = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color(0xFF00695C)
                    )
                    Text(
                        "La terapia filtrará esta frecuencia de la música que reproduzcas",
                        fontSize  = 12.sp,
                        color     = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.padding(top = 6.dp)
                    )
                } else {
                    Text(
                        "Sin configurar",
                        fontSize = 22.sp,
                        color    = Color.Gray
                    )
                    Text(
                        "Ve a la pestaña de Audiometría para configurar tu frecuencia",
                        fontSize  = 12.sp,
                        color     = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Botón play / stop ─────────────────────────────────────────────
        Button(
            onClick  = { isPlaying = !isPlaying },
            enabled  = predictedFc != null,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = if (isPlaying) Color(0xFF795548) else Color(0xFF00695C)
            )
        ) {
            Icon(
                if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                if (isPlaying) "Detener terapia" else "Iniciar terapia",
                fontSize = 16.sp, fontWeight = FontWeight.SemiBold
            )
        }

        if (isPlaying) {
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                Row(
                    modifier          = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color    = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Terapia en curso — La frecuencia ${FrequencyPredictor.freqLabel(predictedFc!!)} está siendo filtrada",
                        fontSize = 13.sp,
                        color    = Color(0xFF2E7D32)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Info ──────────────────────────────────────────────────────────
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("¿Cómo funciona?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                InfoRow("🎵", "La Notch Therapy reproduce música con una \"muesca\" (notch) en la frecuencia exacta de tu tinnitus.")
                Spacer(Modifier.height(6.dp))
                InfoRow("🧠", "Con el tiempo, el cerebro aprende a ignorar esa frecuencia, reduciendo la percepción del zumbido.")
                Spacer(Modifier.height(6.dp))
                InfoRow("⏱️", "Se recomiendan sesiones diarias de al menos 30 minutos para mejores resultados.")
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun InfoRow(emoji: String, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(emoji, fontSize = 16.sp, modifier = Modifier.width(28.dp))
        Text(text, fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.weight(1f))
    }
}
