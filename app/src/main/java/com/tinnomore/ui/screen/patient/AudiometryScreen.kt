package com.tinnomore.ui.screen.patient

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HearingDisabled
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tinnomore.data.api.TinnitusApiResult
import com.tinnomore.util.FrequencyPredictor
import com.tinnomore.viewmodel.AudiometryViewModel
import com.tinnomore.viewmodel.ServerAnalysisState

private val Teal700 = Color(0xFF00695C)
private val Teal50  = Color(0xFFE0F2F1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudiometryScreen(
    patientId: Long,
    onBack: () -> Unit,
    showBackButton: Boolean = false,
    vm: AudiometryViewModel = viewModel()
) {
    val affectedEar  by vm.affectedEar.collectAsState()
    val thresholds   by vm.thresholds.collectAsState()
    val mlFc         by vm.predictedFc.collectAsState()
    val message      by vm.message.collectAsState()
    val serverState  by vm.serverState.collectAsState()
    val savedProfile by vm.savedProfile.collectAsState()

    LaunchedEffect(patientId) { vm.loadLatestProfile(patientId) }

    message?.let {
        LaunchedEffect(it) {
            kotlinx.coroutines.delay(6000)
            vm.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audiometría") },
                navigationIcon = {
                    when {
                        affectedEar != null -> {
                            // Back button to re-select ear
                            IconButton(onClick = { vm.selectEar(null) }) {
                                Icon(Icons.Default.ArrowBack, "Cambiar oído", tint = Color.White)
                            }
                        }
                        showBackButton -> {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = Teal700,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->

        if (affectedEar == null) {
            EarSelectionScreen(
                modifier = Modifier.padding(padding),
                onSelect = { vm.selectEar(it) }
            )
            return@Scaffold
        }

        val earLabel = if (affectedEar == "LEFT") "Izquierdo" else "Derecho"
        val earColor = if (affectedEar == "LEFT") Color(0xFF1565C0) else Color(0xFFD32F2F)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text("Ingresa tus umbrales auditivos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                "Oído afectado: $earLabel — ajusta cada frecuencia según tu audiograma. " +
                "0 dB = audición normal; mayor valor = mayor pérdida.",
                fontSize = 13.sp, color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            SingleChannelAudiogramChart(
                thresholds  = thresholds,
                frequencies = vm.frequencies,
                color       = earColor,
                label       = "Oído $earLabel"
            )

            Spacer(Modifier.height(20.dp))

            Text("Canal $earLabel", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))

            vm.frequencies.forEach { freq ->
                val threshold = thresholds[freq] ?: 20
                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            FrequencyPredictor.freqLabel(freq),
                            fontWeight = FontWeight.Medium, fontSize = 14.sp,
                            modifier = Modifier.width(72.dp)
                        )
                        Text(
                            "$threshold dB HL",
                            fontSize = 14.sp,
                            color = when {
                                threshold < 25 -> Color(0xFF2E7D32)
                                threshold < 55 -> Color(0xFFE65100)
                                else           -> Color(0xFFD32F2F)
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Slider(
                        value = threshold.toFloat(),
                        onValueChange = { vm.setThreshold(freq, it.toInt()) },
                        valueRange = -10f..120f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "0–25 dB = normal · 26–55 dB = pérdida leve-moderada · >55 dB = pérdida severa",
                fontSize = 11.sp, color = Color.Gray,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick  = { vm.saveAndPredict(patientId) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled  = serverState !is ServerAnalysisState.Loading,
                colors   = ButtonDefaults.buttonColors(containerColor = Teal700)
            ) {
                if (serverState is ServerAnalysisState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Analizando con IA…", fontSize = 15.sp)
                } else {
                    Text("Guardar y analizar audiometría", fontSize = 15.sp)
                }
            }

            when (val state = serverState) {
                is ServerAnalysisState.Success -> {
                    Spacer(Modifier.height(16.dp))
                    ServerResultCard(result = state.result)
                }
                is ServerAnalysisState.Error -> {
                    Spacer(Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape  = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Análisis ML no disponible: ${state.message}", fontSize = 13.sp, color = Color(0xFFD32F2F))
                        }
                    }
                }
                else -> {}
            }

            mlFc?.let { fc ->
                Spacer(Modifier.height(12.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle, null,
                            tint = Color(0xFFFFB300), modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Frecuencia recomendada (ML)",
                                fontWeight = FontWeight.Bold, color = Color(0xFFE65100), fontSize = 13.sp
                            )
                            Text(
                                FrequencyPredictor.freqLabel(fc),
                                fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFFB300)
                            )
                            Text(
                                "Notch Therapy se configurará en esta frecuencia.",
                                fontSize = 12.sp, color = Color.Gray
                            )
                        }
                    }
                }
            }

            message?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Teal700)) {
                    Text(msg, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Pantalla de selección de oído
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EarSelectionScreen(modifier: Modifier = Modifier, onSelect: (String) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.HearingDisabled, null,
            tint = Teal700, modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "¿En qué oído percibes el tinnitus?",
            fontSize = 20.sp, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "Registraremos los umbrales auditivos del oído afectado para configurar tu terapia.",
            fontSize = 14.sp, color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EarButton(
                label   = "Oído\nIzquierdo",
                color   = Color(0xFF1565C0),
                bgColor = Color(0xFFE3F2FD),
                modifier = Modifier.weight(1f),
                onClick = { onSelect("LEFT") }
            )
            EarButton(
                label   = "Oído\nDerecho",
                color   = Color(0xFFD32F2F),
                bgColor = Color(0xFFFFEBEE),
                modifier = Modifier.weight(1f),
                onClick = { onSelect("RIGHT") }
            )
        }

        Spacer(Modifier.height(20.dp))
        Text(
            "Si ambos oídos están afectados, comienza con el que presente mayor molestia.",
            fontSize = 12.sp, color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EarButton(
    label: String,
    color: Color, bgColor: Color,
    modifier: Modifier, onClick: () -> Unit
) {
    Button(
        onClick  = onClick,
        modifier = modifier.height(120.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = bgColor)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Audiograma de un solo canal
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SingleChannelAudiogramChart(
    thresholds: Map<Int, Int>,
    frequencies: List<Int>,
    color: Color,
    label: String
) {
    Column {
        Card(modifier = Modifier.fillMaxWidth().height(180.dp), elevation = CardDefaults.cardElevation(2.dp)) {
            Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                val w     = size.width
                val h     = size.height
                val minDb = -10f; val maxDb = 120f
                val range = maxDb - minDb
                val xStep = if (frequencies.size > 1) w / (frequencies.size - 1) else w

                fun dbToY(db: Int) = h * (db - minDb) / range

                repeat(6) { i -> drawLine(Color.LightGray, Offset(0f, h * i / 5f), Offset(w, h * i / 5f), strokeWidth = 1f) }

                val path = Path()
                frequencies.forEachIndexed { i, freq ->
                    val x = i * xStep
                    val y = dbToY(thresholds[freq] ?: 20)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color, style = Stroke(width = 3f))

                frequencies.forEachIndexed { i, freq ->
                    val x = i * xStep
                    val y = dbToY(thresholds[freq] ?: 20)
                    drawCircle(color, 7f, Offset(x, y))
                    drawCircle(Color.White, 3f, Offset(x, y))
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            frequencies.forEach { f ->
                Text(if (f >= 1000) "${f / 1000}k" else "$f", fontSize = 10.sp, color = Color.Gray)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Surface(color = color, shape = MaterialTheme.shapes.extraSmall, modifier = Modifier.size(12.dp)) {}
            Spacer(Modifier.width(4.dp))
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  ServerResultCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ServerResultCard(result: TinnitusApiResult) {
    val hasTinnitus = result.tinnitus
    val headerColor = if (hasTinnitus) Color(0xFFD32F2F) else Color(0xFF2E7D32)
    val bgColor     = if (hasTinnitus) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val headerText  = if (hasTinnitus) "Patron de tinnitus detectado (ML)" else "Sin patron de tinnitus (ML)"

    Card(colors = CardDefaults.cardColors(containerColor = bgColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(headerText, color = headerColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
                "Score: ${(result.tinnitus_score * 100).toInt()}%  •  Confianza: ${result.confidence}",
                color = Color.Gray, fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )

            if (hasTinnitus) {
                HorizontalDivider(color = headerColor.copy(alpha = 0.2f))
                Spacer(Modifier.height(10.dp))

                result.central_freq_hz?.let { freq ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Frecuencia central ML", color = Color.Gray, fontSize = 13.sp)
                        Text(
                            if (freq >= 1000) "${freq / 1000}k Hz" else "$freq Hz",
                            color = headerColor, fontWeight = FontWeight.Bold, fontSize = 13.sp
                        )
                    }
                }
                result.freq_band?.let { band ->
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Banda", color = Color.Gray, fontSize = 13.sp)
                        Text(
                            when (band) { "low" -> "Baja (≤1kHz)"; "medium" -> "Media (2-3kHz)"; "high" -> "Alta (4-8kHz)"; else -> band },
                            fontWeight = FontWeight.SemiBold, fontSize = 13.sp
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Notch depth", color = Color.Gray, fontSize = 13.sp)
                    Text("${result.notch_depth_db} dB", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                result.freq_band_proba?.let { proba ->
                    Spacer(Modifier.height(10.dp))
                    Text("Probabilidad por banda", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    proba.entries.sortedByDescending { it.value }.forEach { (band, prob) ->
                        val lbl = when (band) { "low" -> "Baja (≤1kHz)"; "medium" -> "Media (2-3kHz)"; "high" -> "Alta (4-8kHz)"; else -> band }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lbl, fontSize = 11.sp, modifier = Modifier.width(100.dp))
                            Box(Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(Color.LightGray.copy(alpha = 0.4f))) {
                                Box(Modifier.fillMaxWidth(prob).fillMaxHeight().background(headerColor.copy(alpha = 0.7f)))
                            }
                            Text("${(prob * 100).toInt()}%", fontSize = 11.sp, modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
                        }
                    }
                }
            }

            if (result.recommendations.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = headerColor.copy(alpha = 0.2f))
                Spacer(Modifier.height(8.dp))
                Text("Recomendaciones", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                result.recommendations.forEach { rec ->
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text("•", color = headerColor, modifier = Modifier.padding(end = 6.dp))
                        Text(rec, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                "Analisis orientativo. No reemplaza evaluacion audiologica profesional.",
                fontSize = 10.sp, color = Color.Gray,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// AudiogramChart kept for backward compatibility
@Composable
fun AudiogramChart(left: Map<Int, Int>, right: Map<Int, Int>, frequencies: List<Int>) {
    SingleChannelAudiogramChart(left, frequencies, Color(0xFF1565C0), "Oído Izquierdo")
}
