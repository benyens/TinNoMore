package com.tinnomore.ui.screen.patient

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tinnomore.util.FrequencyPredictor
import com.tinnomore.util.NotchProcessor.NoiseType
import com.tinnomore.viewmodel.AudiometryViewModel
import com.tinnomore.viewmodel.NotchGenState
import com.tinnomore.viewmodel.NotchViewModel
import kotlin.math.roundToInt
import kotlin.math.sqrt

// ── Paleta ────────────────────────────────────────────────────────────────────
private val Teal700  = Color(0xFF00695C)
private val Teal50   = Color(0xFFE0F2F1)
private val Teal100  = Color(0xFFB2DFDB)
private val Amber600 = Color(0xFFFFB300)
private val Red50    = Color(0xFFFFEBEE)
private val Red400   = Color(0xFFEF5350)

// Colores por tipo de ruido
private val noiseColors = mapOf(
    NoiseType.PINK  to Color(0xFFF48FB1),   // rosa pastel
    NoiseType.WHITE to Color(0xFFB0BEC5),   // gris claro
    NoiseType.BROWN to Color(0xFFA1887F)    // marrón suave
)
private val noiseColorsDark = mapOf(
    NoiseType.PINK  to Color(0xFFAD1457),
    NoiseType.WHITE to Color(0xFF37474F),
    NoiseType.BROWN to Color(0xFF4E342E)
)

@Composable
fun NotchTherapyScreen(
    patientId: Long,
    onBack: () -> Unit = {},
    audiometryVm: AudiometryViewModel = viewModel(),
    notchVm: NotchViewModel = viewModel()
) {
    val predictedFc  by audiometryVm.predictedFc.collectAsState()
    val selectedFreq by notchVm.selectedFreq.collectAsState()
    val noiseType    by notchVm.noiseType.collectAsState()
    val volumeDb     by notchVm.volumeDb.collectAsState()
    val isPlaying    by notchVm.isPlaying.collectAsState()
    val genState     by notchVm.genState.collectAsState()

    LaunchedEffect(patientId) { audiometryVm.loadLatestProfile(patientId) }
    LaunchedEffect(predictedFc) {
        predictedFc?.let { fc ->
            if (notchVm.availableFrequencies.contains(fc)) notchVm.setFrequency(fc)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.GraphicEq, null, tint = Teal700, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(8.dp))
            Text("Notch Therapy", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Teal700)
        }
        Text(
            "Filtra la frecuencia de tu tinnitus del ruido de fondo",
            fontSize = 13.sp, color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        // ── 1. Tipo de ruido ──────────────────────────────────────────────────
        NoiseSelector(
            selected  = noiseType,
            isPlaying = isPlaying,
            onSelect  = { notchVm.setNoiseType(it) }
        )

        Spacer(Modifier.height(14.dp))

        // ── 2. Selector de frecuencia ─────────────────────────────────────────
        FrequencySelector(
            frequencies = notchVm.availableFrequencies,
            selected    = selectedFreq,
            predictedFc = predictedFc,
            onSelect    = { notchVm.setFrequency(it) }
        )

        Spacer(Modifier.height(14.dp))

        // ── 3. Rango del notch ────────────────────────────────────────────────
        NotchRangeCard(fcHz = selectedFreq)

        Spacer(Modifier.height(14.dp))

        // ── 4. Volumen ────────────────────────────────────────────────────────
        VolumeCard(volumeDb = volumeDb, onVolumeChange = { notchVm.setVolume(it) })

        Spacer(Modifier.height(20.dp))

        // ── Estado generación ─────────────────────────────────────────────────
        AnimatedVisibility(genState is NotchGenState.Generating) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Teal50)
                    .padding(14.dp)
            ) {
                CircularProgressIndicator(color = Teal700, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
                Text("Procesando audio con notch…", fontSize = 13.sp, color = Teal700)
            }
            Spacer(Modifier.height(8.dp))
        }
        AnimatedVisibility(genState is NotchGenState.Error) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Red50)
                    .padding(14.dp)
            ) {
                Text("⚠ ${(genState as? NotchGenState.Error)?.msg}", fontSize = 13.sp, color = Red400)
            }
            Spacer(Modifier.height(8.dp))
        }

        // ── 5. Play / Pause ───────────────────────────────────────────────────
        PlayButton(
            isPlaying = isPlaying,
            isLoading = genState is NotchGenState.Generating,
            noiseType = noiseType,
            onToggle  = { if (isPlaying) notchVm.stop() else notchVm.play() }
        )

        AnimatedVisibility(
            visible = isPlaying,
            enter   = fadeIn() + expandVertically(),
            exit    = fadeOut() + shrinkVertically()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(14.dp))
                PlayingBadge(fcHz = selectedFreq, noiseType = noiseType, volumeDb = volumeDb)
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── 6. Info ───────────────────────────────────────────────────────────
        InfoSection()

        Spacer(Modifier.height(32.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  NoiseSelector
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NoiseSelector(
    selected: NoiseType,
    isPlaying: Boolean,
    onSelect: (NoiseType) -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tipo de ruido base", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Teal700)
                if (isPlaying) {
                    Surface(color = Teal100, shape = RoundedCornerShape(4.dp)) {
                        Text(
                            "Detener para cambiar",
                            fontSize = 10.sp, color = Teal700,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NoiseType.entries.forEach { type ->
                    NoiseCard(
                        type      = type,
                        isSelected = type == selected,
                        enabled   = !isPlaying,
                        modifier  = Modifier.weight(1f),
                        onSelect  = { onSelect(type) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NoiseCard(
    type: NoiseType,
    isSelected: Boolean,
    enabled: Boolean,
    modifier: Modifier,
    onSelect: () -> Unit
) {
    val bgColor     = if (isSelected) (noiseColors[type] ?: Teal100) else MaterialTheme.colorScheme.surfaceVariant
    val borderColor = if (isSelected) (noiseColorsDark[type] ?: Teal700) else Color.Transparent
    val textColor   = if (isSelected) (noiseColorsDark[type] ?: Teal700) else Color.Gray
    val alpha       = if (enabled) 1f else 0.5f

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(if (isSelected) 2.dp else 0.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onSelect() },
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier            = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(type.emoji, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                type.label,
                fontSize   = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color      = textColor
            )
            if (isSelected) {
                Spacer(Modifier.height(2.dp))
                Text("✓ Activo", fontSize = 10.sp, color = textColor)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  FrequencySelector
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FrequencySelector(
    frequencies: List<Int>,
    selected: Int,
    predictedFc: Int?,
    onSelect: (Int) -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Frecuencia a filtrar", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Teal700)
                if (predictedFc != null) {
                    Spacer(Modifier.width(8.dp))
                    Surface(color = Color(0xFFFFF8E1), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            "Sugerida: ${FrequencyPredictor.freqLabel(predictedFc)}",
                            fontSize = 11.sp, color = Amber600,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                FrequencyPredictor.freqLabel(selected),
                fontSize   = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Teal700,
                modifier   = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                frequencies.forEach { freq ->
                    val isSelected  = freq == selected
                    val isPredicted = freq == predictedFc
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSelect(freq) }
                            .then(
                                if (isPredicted && !isSelected)
                                    Modifier.border(1.5.dp, Amber600, RoundedCornerShape(20.dp))
                                else Modifier
                            ),
                        color = if (isSelected) Teal700 else Teal50,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            FrequencyPredictor.freqLabel(freq),
                            fontSize   = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color      = if (isSelected) Color.White else Teal700,
                            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  NotchRangeCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NotchRangeCard(fcHz: Int) {
    val lower = (fcHz / sqrt(2.0)).roundToInt()
    val upper = (fcHz * sqrt(2.0)).roundToInt()

    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = Teal50),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Rango del filtro notch", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Teal700)
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                FreqPill("−1 octava", lower, "límite inferior")
                Text("↔", fontSize = 20.sp, color = Teal700)
                FreqPill("fc", fcHz, "centro", bold = true)
                Text("↔", fontSize = 20.sp, color = Teal700)
                FreqPill("+1 octava", upper, "límite superior")
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "Ancho de banda: 1 octava (fc/√2 … fc×√2)",
                fontSize = 11.sp, color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FreqPill(topLabel: String, hz: Int, bottomLabel: String, bold: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(topLabel, fontSize = 10.sp, color = Color.Gray)
        Text(
            FrequencyPredictor.freqLabel(hz),
            fontSize   = if (bold) 18.sp else 14.sp,
            fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.SemiBold,
            color      = Teal700
        )
        Text(bottomLabel, fontSize = 10.sp, color = Color.Gray)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  VolumeCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VolumeCard(volumeDb: Float, onVolumeChange: (Float) -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Intensidad", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Teal700)
                Surface(color = Teal100, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        "${volumeDb.roundToInt()} dB",
                        fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Teal700,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Slider(
                value         = volumeDb,
                onValueChange = onVolumeChange,
                valueRange    = -40f..0f,
                steps         = 39,
                colors        = SliderDefaults.colors(
                    thumbColor       = Teal700,
                    activeTrackColor = Teal700
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Silencio (−40 dB)", fontSize = 11.sp, color = Color.Gray)
                Text("Máximo (0 dB)", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  PlayButton
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PlayButton(
    isPlaying: Boolean,
    isLoading: Boolean,
    noiseType: NoiseType,
    onToggle: () -> Unit
) {
    val containerColor = when {
        isLoading -> Color(0xFF607D8B)
        isPlaying -> Color(0xFF795548)
        else      -> Teal700
    }
    Button(
        onClick  = onToggle,
        enabled  = !isLoading,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape    = RoundedCornerShape(14.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(10.dp))
            Text("Procesando ruido ${noiseType.label.lowercase()}…", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        } else {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                if (isPlaying) "Pausar" else "Reproducir en loop",
                fontSize = 15.sp, fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  PlayingBadge
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PlayingBadge(fcHz: Int, noiseType: NoiseType, volumeDb: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f, label = "alpha",
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse)
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFE8F5E9))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF2E7D32).copy(alpha = alpha))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "${noiseType.emoji} Ruido ${noiseType.label} • Notch en ${FrequencyPredictor.freqLabel(fcHz)} • ${volumeDb.roundToInt()} dB",
            fontSize   = 13.sp,
            color      = Color(0xFF2E7D32),
            fontWeight = FontWeight.Medium
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  InfoSection
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun InfoSection() {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("¿Cómo funciona?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))
            InfoRow("🌸", "Rosa: espectro equilibrado, el más recomendado para terapia de tinnitus.")
            Spacer(Modifier.height(6.dp))
            InfoRow("🤍", "Blanco: energía uniforme en todas las frecuencias — más agudo e intenso.")
            Spacer(Modifier.height(6.dp))
            InfoRow("🟫", "Marrón: más graves, grave y profundo, parecido al sonido del viento.")
            Spacer(Modifier.height(10.dp))
            Divider()
            Spacer(Modifier.height(10.dp))
            InfoRow("🔇", "El notch suprime la frecuencia de tu tinnitus ±1 octava del ruido elegido.")
            Spacer(Modifier.height(6.dp))
            InfoRow("🔁", "El audio se reproduce en loop continuo. El volumen es ajustable en tiempo real.")
            Spacer(Modifier.height(6.dp))
            InfoRow("⏱️", "Se recomiendan sesiones diarias de al menos 30 minutos.")
        }
    }
}

@Composable
private fun InfoRow(emoji: String, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(emoji, fontSize = 16.sp, modifier = Modifier.width(28.dp))
        Text(text, fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.weight(1f))
    }
}
