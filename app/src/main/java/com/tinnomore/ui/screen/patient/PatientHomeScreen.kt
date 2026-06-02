package com.tinnomore.ui.screen.patient

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tinnomore.data.db.entity.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHomeScreen(
    user: User?,
    onCrisisClick: () -> Unit,
    onSymptomsClick: () -> Unit,
    onAudiometryClick: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TinNoMore") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, "Cerrar sesión", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Saludo
            Text(
                text       = "Hola, ${user?.name?.split(" ")?.firstOrNull() ?: "Paciente"} 👋",
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text     = "¿Cómo te encuentras hoy?",
                fontSize = 15.sp,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 28.dp)
            )

            // ── Botón de crisis (HU-01) ─────────────────────────────────────
            Card(
                onClick = onCrisisClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Row(
                    modifier              = Modifier.fillMaxSize().padding(20.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            "¡CRISIS!",
                            color      = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 24.sp
                        )
                        Text(
                            "Presiona si tienes una crisis ahora",
                            color    = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Acciones secundarias ────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    modifier  = Modifier.weight(1f),
                    icon      = Icons.Default.EditNote,
                    title     = "Síntomas",
                    subtitle  = "Registra tu estado diario",
                    color     = MaterialTheme.colorScheme.primary,
                    onClick   = onSymptomsClick
                )
                FeatureCard(
                    modifier  = Modifier.weight(1f),
                    icon      = Icons.Default.HearingDisabled,
                    title     = "Audiometría",
                    subtitle  = "Configura tu terapia",
                    color     = Color(0xFF00695C),
                    onClick   = onAudiometryClick
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Info de terapia configurada ────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Notch Therapy activa",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 14.sp
                        )
                        Text(
                            "Ingresa tu audiometría para configurar la frecuencia",
                            fontSize = 12.sp,
                            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick   = onClick,
        modifier  = modifier.height(120.dp),
        colors    = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier              = Modifier.fillMaxSize().padding(14.dp),
            verticalArrangement   = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title,   color = Color.White, fontWeight = FontWeight.Bold,  fontSize = 14.sp)
            Text(subtitle, color = Color.White.copy(alpha = 0.8f),             fontSize = 11.sp)
        }
    }
}
