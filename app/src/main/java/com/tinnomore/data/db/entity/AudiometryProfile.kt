package com.tinnomore.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * HU-04: Perfil audiométrico del paciente.
 * Almacena umbrales auditivos para ambos canales y la frecuencia central predicha (fc).
 * Los datos de canal se serializan como "250:20,500:25,1000:30,2000:60,4000:80,8000:70"
 * donde cada par es FRECUENCIA_HZ:UMBRAL_DB_HL.
 */
@Entity(tableName = "audiometry_profiles")
data class AudiometryProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val leftChannelData: String,   // "250:20,500:25,..."
    val rightChannelData: String,  // "250:20,500:25,..."
    val predictedFc: Int           // frecuencia central en Hz
)
