package com.tinnomore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tinnomore.data.db.AppDatabase
import com.tinnomore.data.db.entity.AudiometryProfile
import com.tinnomore.data.repository.AudiometryRepository
import com.tinnomore.util.FrequencyPredictor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que implementa HU-04 (ingreso de audiometría y predicción algorítmica del acúfeno).
 *
 * Criterios cubiertos:
 *  HU-04-1: interfaz visual tipo ecualizador con frecuencias 250-8000 Hz (canales L y R)
 *  HU-04-2: muestra dB y frecuencia en tiempo real al mover controles
 *  HU-04-3: al guardar → ejecuta lógica de predicción y deduce la fc más probable
 *  HU-04-4: persiste umbrales localmente (SQLite / Room) en formato estructurado
 *  HU-04-5: configura la Notch Therapy con la fc predicha
 */
class AudiometryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AudiometryRepository(
        AppDatabase.getDatabase(application).audiometryDao()
    )

    val frequencies = FrequencyPredictor.FREQUENCIES   // [250, 500, 1000, 2000, 4000, 8000]

    // Umbral inicial: 20 dB HL (audición normal)
    private val defaultThresholds get() = frequencies.associateWith { 20 }

    private val _left = MutableStateFlow<Map<Int, Int>>(defaultThresholds)
    val left: StateFlow<Map<Int, Int>> = _left.asStateFlow()

    private val _right = MutableStateFlow<Map<Int, Int>>(defaultThresholds)
    val right: StateFlow<Map<Int, Int>> = _right.asStateFlow()

    private val _predictedFc = MutableStateFlow<Int?>(null)
    val predictedFc: StateFlow<Int?> = _predictedFc.asStateFlow()

    private val _savedProfile = MutableStateFlow<AudiometryProfile?>(null)
    val savedProfile: StateFlow<AudiometryProfile?> = _savedProfile.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    // ─── Actualización en tiempo real (HU-04-2) ─────────────────────────────

    fun setLeftThreshold(freq: Int, db: Int) {
        _left.value = _left.value.toMutableMap().also { it[freq] = db }
    }

    fun setRightThreshold(freq: Int, db: Int) {
        _right.value = _right.value.toMutableMap().also { it[freq] = db }
    }

    // ─── Guardar y predecir (HU-04-3 / HU-04-4 / HU-04-5) ──────────────────

    fun saveAndPredict(patientId: Long) {
        val leftMap = _left.value
        val rightMap = _right.value

        // HU-04-3: predicción algorítmica
        val fc = FrequencyPredictor.predictFc(leftMap, rightMap)
        _predictedFc.value = fc

        viewModelScope.launch {
            // HU-04-4: persistir en Room (SQLite)
            val profile = AudiometryProfile(
                patientId = patientId,
                leftChannelData = FrequencyPredictor.serializeChannelData(leftMap),
                rightChannelData = FrequencyPredictor.serializeChannelData(rightMap),
                predictedFc = fc
            )
            repository.saveProfile(profile)
            _savedProfile.value = profile

            // HU-04-5: confirmación de que la terapia fue configurada
            _message.value =
                "Audiometría guardada correctamente.\n" +
                "Frecuencia central predicha: ${FrequencyPredictor.freqLabel(fc)}.\n" +
                "La Notch Therapy ha sido configurada para esta frecuencia."
        }
    }

    // ─── Cargar perfil previo ────────────────────────────────────────────────

    fun loadLatestProfile(patientId: Long) {
        viewModelScope.launch {
            val profile = repository.getLatestForPatient(patientId) ?: return@launch
            _savedProfile.value = profile
            _predictedFc.value = profile.predictedFc
            _left.value = FrequencyPredictor.parseChannelData(profile.leftChannelData)
                .ifEmpty { defaultThresholds }
            _right.value = FrequencyPredictor.parseChannelData(profile.rightChannelData)
                .ifEmpty { defaultThresholds }
        }
    }

    fun clearMessage() { _message.value = null }
}
