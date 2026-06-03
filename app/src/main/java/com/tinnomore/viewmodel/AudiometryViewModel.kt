package com.tinnomore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tinnomore.data.api.AudiogramRequest
import com.tinnomore.data.api.TinnitusApi
import com.tinnomore.data.api.TinnitusApiResult
import com.tinnomore.data.db.AppDatabase
import com.tinnomore.data.db.entity.AudiometryProfile
import com.tinnomore.data.repository.AudiometryRepository
import com.tinnomore.util.FrequencyPredictor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado de la llamada al servidor
sealed class ServerAnalysisState {
    object Idle    : ServerAnalysisState()
    object Loading : ServerAnalysisState()
    data class Success(val result: TinnitusApiResult) : ServerAnalysisState()
    data class Error(val message: String)             : ServerAnalysisState()
}

class AudiometryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AudiometryRepository(
        AppDatabase.getDatabase(application).audiometryDao()
    )

    val frequencies = FrequencyPredictor.FREQUENCIES   // [250, 500, 1000, 2000, 4000, 8000]

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

    // ── NUEVO: estado del análisis del servidor ───────────────────────────────
    private val _serverState = MutableStateFlow<ServerAnalysisState>(ServerAnalysisState.Idle)
    val serverState: StateFlow<ServerAnalysisState> = _serverState.asStateFlow()

    // ─── Actualización en tiempo real ────────────────────────────────────────

    fun setLeftThreshold(freq: Int, db: Int) {
        _left.value = _left.value.toMutableMap().also { it[freq] = db }
    }

    fun setRightThreshold(freq: Int, db: Int) {
        _right.value = _right.value.toMutableMap().also { it[freq] = db }
    }

    // ─── Guardar, predecir localmente Y consultar servidor ───────────────────

    fun saveAndPredict(patientId: Long) {
        val leftMap  = _left.value
        val rightMap = _right.value

        // 1. Predicción local (FrequencyPredictor — sin cambios)
        val fc = FrequencyPredictor.predictFc(leftMap, rightMap)
        _predictedFc.value = fc

        viewModelScope.launch {
            // 2. Persistir en Room
            val profile = AudiometryProfile(
                patientId        = patientId,
                leftChannelData  = FrequencyPredictor.serializeChannelData(leftMap),
                rightChannelData = FrequencyPredictor.serializeChannelData(rightMap),
                predictedFc      = fc
            )
            repository.saveProfile(profile)
            _savedProfile.value = profile
            _message.value =
                "Audiometría guardada.\n" +
                        "Frecuencia central predicha: ${FrequencyPredictor.freqLabel(fc)}.\n" +
                        "Consultando servidor para análisis ML..."
        }

        // 3. Análisis ML en el servidor (en paralelo)
        analyzeWithServer(leftMap, rightMap)
    }

    private fun analyzeWithServer(leftMap: Map<Int, Int>, rightMap: Map<Int, Int>) {
        // Promedia ambos oídos para enviar al servidor
        // (el servidor espera un solo canal; usamos el promedio como señal representativa)
        fun avg(freq: Int): Float =
            ((leftMap[freq] ?: 20) + (rightMap[freq] ?: 20)) / 2f

        // El servidor usa 3kHz — interpolamos entre 2kHz y 4kHz si no existe
        val db3000 = ((leftMap[2000] ?: 20) + (leftMap[4000] ?: 20)) / 2f

        val request = AudiogramRequest(
            db_250  = avg(250),
            db_500  = avg(500),
            db_1000 = avg(1000),
            db_2000 = avg(2000),
            db_3000 = db3000,
            db_4000 = avg(4000),
            db_6000 = avg(6000).let {
                // Si 6000 no está en el mapa local (FrequencyPredictor usa [250,500,1000,2000,4000,8000])
                // interpolamos entre 4k y 8k
                if (!leftMap.containsKey(6000))
                    ((leftMap[4000] ?: 20) + (leftMap[8000] ?: 20)) / 2f
                else it
            },
            db_8000 = avg(8000)
        )

        viewModelScope.launch {
            _serverState.value = ServerAnalysisState.Loading
            try {
                val response = TinnitusApi.service.analyze(TinnitusApi.API_KEY, request)
                if (response.isSuccessful && response.body() != null) {
                    _serverState.value = ServerAnalysisState.Success(response.body()!!)
                } else {
                    _serverState.value = ServerAnalysisState.Error(
                        "Error del servidor: ${response.code()}"
                    )
                }
            } catch (e: java.net.ConnectException) {
                _serverState.value = ServerAnalysisState.Error("Sin conexión al servidor")
            } catch (e: java.net.SocketTimeoutException) {
                _serverState.value = ServerAnalysisState.Error("Timeout — reintenta en un momento")
            } catch (e: Exception) {
                _serverState.value = ServerAnalysisState.Error("Error: ${e.message}")
            }
        }
    }

    // ─── Cargar perfil previo ────────────────────────────────────────────────

    fun loadLatestProfile(patientId: Long) {
        viewModelScope.launch {
            val profile = repository.getLatestForPatient(patientId) ?: return@launch
            _savedProfile.value  = profile
            _predictedFc.value   = profile.predictedFc
            _left.value = FrequencyPredictor.parseChannelData(profile.leftChannelData)
                .ifEmpty { defaultThresholds }
            _right.value = FrequencyPredictor.parseChannelData(profile.rightChannelData)
                .ifEmpty { defaultThresholds }
        }
    }

    fun clearMessage()      { _message.value = null }
    fun clearServerState()  { _serverState.value = ServerAnalysisState.Idle }
}