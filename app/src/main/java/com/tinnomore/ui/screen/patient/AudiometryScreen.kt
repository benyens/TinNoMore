package com.tinnomore.ui.screen.patient

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudiometryScreen(
    patientId: Long,
    onBack: () -> Unit,
    vm: AudiometryViewModel = viewModel()
) {
    val left         by vm.left.collectAsState()
    val right        by vm.right.collectAsState()
    val predictedFc  by vm.predictedFc.collectAsState()
    val message      by vm.message.collectAsState()
    val serverState  by vm.serverState.collectAsState()   // ← NUEVO

    var selectedTab by remember { mutableIntStateOf(0) }

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
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = Color(0xFF00695C),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                "Ingresa tus umbrales auditivos",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Ajusta cada frecuencia según tu audiograma previo. " +
                        "Los valores en dB HL representan tu umbral auditivo " +
                        "(0 = audición normal, mayor valor = mayor pérdida).",
                fontSize = 13.sp,
                color    = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            // ── Selector de canal ────────────────────────────────────────
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = MaterialTheme.colorScheme.primaryContainer
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("🔵  Oído Izquierdo", modifier = Modifier.padding(12.dp), fontSize = 14.sp)
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("🔴  Oído Derecho", modifier = Modifier.padding(12.dp), fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Audiograma visual ────────────────────────────────────────
            AudiogramChart(left = left, right = right, frequencies = vm.frequencies)

            Spacer(Modifier.height(20.dp))

            // ── Sliders ──────────────────────────────────────────────────
            val currentChannel = if (selectedTab == 0) left else right
            val channelLabel   = if (selectedTab == 0) "Izquierdo" else "Derecho"

            Text("Canal $channelLabel", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))

            vm.frequencies.forEach { freq ->
                val threshold = currentChannel[freq] ?: 20
                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            FrequencyPredictor.freqLabel(freq),
                            fontWeight = FontWeight.Medium,
                            fontSize   = 14.sp,
                            modifier   = Modifier.width(72.dp)
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
                        value         = threshold.toFloat(),
                        onValueChange = { v ->
                            if (selectedTab == 0) vm.setLeftThreshold(freq, v.toInt())
                            else                  vm.setRightThreshold(freq, v.toInt())
                        },
                        valueRange = -10f..120f,
                        modifier   = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Referencia: 0-25 dB = normal · 26-55 dB = pérdida leve-moderada · >55 dB = pérdida severa",
                fontSize  = 11.sp,
                color     = Color.Gray,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // ── Botón guardar ────────────────────────────────────────────
            Button(
                onClick  = { vm.saveAndPredict(patientId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = serverState !is ServerAnalysisState.Loading,
                colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C))
            ) {
                if (serverState is ServerAnalysisState.Loading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Analizando con IA...", fontSize = 15.sp)
                } else {
                    Text("Guardar y analizar audiometría", fontSize = 15.sp)
                }
            }

            // ── Resultado predicción local ───────────────────────────────
            predictedFc?.let { fc ->
                Spacer(Modifier.height(16.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))) {
                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint     = Color(0xFF00695C),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Predicción local (algoritmo notch)",
                                fontWeight = FontWeight.Bold,
                                color      = Color(0xFF00695C),
                                fontSize   = 13.sp
                            )
                            Text(
                                FrequencyPredictor.freqLabel(fc),
                                fontSize   = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = Color(0xFF00695C)
                            )
                            Text(
                                "Notch Therapy configurada en esta frecuencia.",
                                fontSize = 12.sp,
                                color    = Color.Gray
                            )
                        }
                    }
                }
            }

            // ── Resultado del servidor (ML) ──────────────────────────────
            when (val state = serverState) {
                is ServerAnalysisState.Success -> {
                    Spacer(Modifier.height(12.dp))
                    ServerResultCard(result = state.result)
                }
                is ServerAnalysisState.Error -> {
                    Spacer(Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape  = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier          = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint     = Color(0xFFD32F2F),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Análisis ML no disponible: ${state.message}",
                                fontSize = 13.sp,
                                color    = Color(0xFFD32F2F)
                            )
                        }
                    }
                }
                else -> {}
            }

            // ── Mensaje de confirmación ──────────────────────────────────
            message?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32))) {
                    Text(
                        msg,
                        color    = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─── Card con resultado del servidor ─────────────────────────────────────────

@Composable
fun ServerResultCard(result: TinnitusApiResult) {
    val hasTinnitus  = result.tinnitus
    val headerColor  = if (hasTinnitus) Color(0xFFD32F2F) else Color(0xFF2E7D32)
    val bgColor      = if (hasTinnitus) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val headerText   = if (hasTinnitus) "⚠️ Patrón de tinnitus detectado (ML)" else "✅ Sin patrón de tinnitus (ML)"

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape  = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(headerText, color = headerColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
                "Score: ${(result.tinnitus_score * 100).toInt()}%  •  Confianza: ${result.confidence}",
                color    = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )

            if (hasTinnitus) {
                Divider(color = headerColor.copy(alpha = 0.2f))
                Spacer(Modifier.height(10.dp))

                // Frecuencia central
                result.central_freq_hz?.let { freq ->
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Frecuencia central ML", color = Color.Gray, fontSize = 13.sp)
                        Text(
                            if (freq >= 1000) "${freq/1000}k Hz" else "$freq Hz",
                            color      = headerColor,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 13.sp
                        )
                    }
                }

                // Banda
                result.freq_band?.let { band ->
                    Row(
                        modifier              = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Banda", color = Color.Gray, fontSize = 13.sp)
                        Text(
                            when(band) {
                                "low"    -> "Baja (≤1kHz)"
                                "medium" -> "Media (2-3kHz)"
                                "high"   -> "Alta (4-8kHz)"
                                else     -> band
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 13.sp
                        )
                    }
                }

                // Profundidad del notch
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Notch depth", color = Color.Gray, fontSize = 13.sp)
                    Text("${result.notch_depth_db} dB", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                // Barras de probabilidad por banda
                result.freq_band_proba?.let { proba ->
                    Spacer(Modifier.height(10.dp))
                    Text("Probabilidad por banda", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    proba.entries.sortedByDescending { it.value }.forEach { (band, prob) ->
                        val label = when(band) {
                            "low"    -> "Baja (≤1kHz)"
                            "medium" -> "Media (2-3kHz)"
                            "high"   -> "Alta (4-8kHz)"
                            else     -> band
                        }
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(label, fontSize = 11.sp, modifier = Modifier.width(100.dp))
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.LightGray.copy(alpha = 0.4f))
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxWidth(prob)
                                        .fillMaxHeight()
                                        .background(headerColor.copy(alpha = 0.7f))
                                )
                            }
                            Text(
                                "${(prob * 100).toInt()}%",
                                fontSize = 11.sp,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }

            // Recomendaciones
            if (result.recommendations.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Divider(color = headerColor.copy(alpha = 0.2f))
                Spacer(Modifier.height(8.dp))
                Text("Recomendaciones", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                result.recommendations.forEach { rec ->
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text("•", color = headerColor, modifier = Modifier.padding(end = 6.dp))
                        Text(rec, fontSize = 12.sp)
                    }
                }
            }

            // Disclaimer
            Spacer(Modifier.height(10.dp))
            Text(
                "⚕️ Análisis orientativo. No reemplaza evaluación audiológica profesional.",
                fontSize = 10.sp,
                color    = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─── Audiograma visual (sin cambios respecto al original) ─────────────────────

@Composable
fun AudiogramChart(
    left: Map<Int, Int>,
    right: Map<Int, Int>,
    frequencies: List<Int>
) {
    val blueLeft = Color(0xFF1565C0)
    val redRight = Color(0xFFD32F2F)

    Column {
        Card(
            modifier  = Modifier.fillMaxWidth().height(180.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                val w     = size.width
                val h     = size.height
                val minDb = -10f
                val maxDb = 120f
                val range = maxDb - minDb
                val xStep = if (frequencies.size > 1) w / (frequencies.size - 1) else w

                fun dbToY(db: Int) = h * (db - minDb) / range

                repeat(6) { i ->
                    val y = h * i / 5f
                    drawLine(Color.LightGray, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                }

                fun drawChannel(channel: Map<Int, Int>, color: Color) {
                    val path = Path()
                    frequencies.forEachIndexed { i, freq ->
                        val x = i * xStep
                        val y = dbToY(channel[freq] ?: 20)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(path, color, style = Stroke(width = 3f))
                    frequencies.forEachIndexed { i, freq ->
                        val x = i * xStep
                        val y = dbToY(channel[freq] ?: 20)
                        drawCircle(color, 7f, Offset(x, y))
                        drawCircle(Color.White, 3f, Offset(x, y))
                    }
                }

                drawChannel(left, blueLeft)
                drawChannel(right, redRight)
            }
        }

        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            frequencies.forEach { f ->
                Text(
                    if (f >= 1000) "${f / 1000}k" else "$f",
                    fontSize = 10.sp,
                    color    = Color.Gray
                )
            }
        }

        Row(
            modifier              = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            LegendItem(blueLeft, "Oído Izquierdo")
            Spacer(Modifier.width(16.dp))
            LegendItem(redRight, "Oído Derecho")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color    = color,
            shape    = MaterialTheme.shapes.extraSmall,
            modifier = Modifier.size(12.dp)
        ) {}
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}