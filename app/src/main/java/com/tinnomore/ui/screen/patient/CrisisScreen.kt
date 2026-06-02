package com.tinnomore.ui.screen.patient

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tinnomore.viewmodel.CrisisState
import com.tinnomore.viewmodel.CrisisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrisisScreen(
    patientId: Long,
    onBack: () -> Unit,
    vm: CrisisViewModel = viewModel()
) {
    val context   = LocalContext.current
    val state     by vm.state.collectAsState()
    val result    by vm.result.collectAsState()
    val progress  by vm.progress.collectAsState()
    val elapsed   by vm.elapsedSeconds.collectAsState()

    // Launcher para solicitar permiso RECORD_AUDIO
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) vm.startRecording(patientId, context.filesDir)
        else         vm.onPermissionDenied()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crisis de Tinnitus") },
                navigationIcon = {
                    IconButton(onClick = { vm.reset(); onBack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = Color(0xFFD32F2F),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier          = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            contentAlignment  = Alignment.Center
        ) {
            when (state) {

                // ── IDLE: pantalla inicial ────────────────────────────────
                CrisisState.IDLE -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎙️", fontSize = 72.sp)
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "¿Estás sufriendo una crisis?",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Al pulsar el botón la app grabará el ambiente acústico " +
                        "durante 10 segundos y ajustará tu terapia automáticamente.",
                        textAlign = TextAlign.Center,
                        color     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        modifier  = Modifier.padding(bottom = 32.dp)
                    )
                    Button(
                        onClick = {
                            val hasPerm = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasPerm) vm.startRecording(patientId, context.filesDir)
                            else permLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("ANALIZAR AMBIENTE", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // ── SIN PERMISO (HU-01-2) ─────────────────────────────────
                CrisisState.NO_PERMISSION -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚠️", fontSize = 56.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Sin acceso al micrófono",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "La aplicación no tiene acceso al micrófono. " +
                        "Por favor concede el permiso para poder analizar el ambiente y agilizar tu terapia.",
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.padding(bottom = 20.dp)
                    )
                    Button(onClick = { permLauncher.launch(Manifest.permission.RECORD_AUDIO) }) {
                        Text("Conceder permiso")
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { vm.reset() }) { Text("Volver") }
                }

                // ── GRABANDO (HU-01-1) ────────────────────────────────────
                CrisisState.RECORDING -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎙️", fontSize = 72.sp)
                    Spacer(Modifier.height(20.dp))
                    Text("Grabando ambiente...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("$elapsed / 10 segundos", color = Color.Gray)
                    Spacer(Modifier.height(20.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        color    = Color(0xFFD32F2F)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No muevas el teléfono para obtener una lectura precisa",
                        fontSize  = 12.sp,
                        color     = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                // ── ANALIZANDO (HU-02-1) ──────────────────────────────────
                CrisisState.ANALYZING -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFFD32F2F), modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Analizando el audio...", fontSize = 18.sp)
                }

                // ── AMBIENTE SEGURO (HU-02-3) ─────────────────────────────
                CrisisState.SAFE -> result?.let { r ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✅", fontSize = 64.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Ambiente seguro",
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFF2E7D32)
                        )
                        Text(
                            "${r.decibels.toInt()} dB",
                            fontSize   = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color(0xFF2E7D32)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(r.message, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 16.dp))

                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Terapias disponibles", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                TherapyRow("🎵", "Notch Therapy — Estándar")
                                TherapyRow("🌊", "Ruido blanco")
                                TherapyRow("🌿", "Sonidos de naturaleza")
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        OutlinedButton(onClick = { vm.reset() }) { Text("Nuevo análisis") }
                    }
                }

                // ── AMBIENTE PELIGROSO (HU-02-2) ──────────────────────────
                CrisisState.DANGEROUS -> result?.let { r ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔴", fontSize = 64.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "¡Ruido peligroso!",
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFFD32F2F),
                            textAlign  = TextAlign.Center
                        )
                        Text(
                            "${r.decibels.toInt()} dB",
                            fontSize   = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color(0xFFD32F2F)
                        )
                        Spacer(Modifier.height(12.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                            Text(
                                r.message,
                                modifier  = Modifier.padding(14.dp),
                                textAlign = TextAlign.Center,
                                color     = Color(0xFFB71C1C),
                                fontSize  = 14.sp
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        r.therapyIntensity?.let { intensity ->
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Terapia ajustada automáticamente", fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(6.dp))
                                    TherapyRow("🎵", "Notch Therapy — Intensidad: ${(intensity * 100).toInt()}%")
                                    Text(
                                        "La intensidad fue calculada en base a los ${r.decibels.toInt()} dB detectados.",
                                        fontSize = 12.sp,
                                        color    = Color.Gray,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        OutlinedButton(onClick = { vm.reset() }) { Text("Nuevo análisis") }
                    }
                }
            }
        }
    }
}

@Composable
private fun TherapyRow(emoji: String, label: String) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 14.sp)
    }
}
