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

// ── Estado del análisis ML ────────────────────────────────────────────────────
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

    // Frecuencias del audiograma (se usan en la UI)
    val frequencies = FrequencyPredictor.FREQUENCIES   // [250, 500, 1000, 2000, 4000, 8000]

    private val defaultThresholds get() = frequencies.associateWith { 20 }

    // ── Oído afectado ─────────────────────────────────────────────────────────
    // null = no elegido aún (se muestra la pantalla de selección)
    private val _affectedEar = MutableStateFlow<String?>(null)
    val affectedEar: StateFlow<String?> = _affectedEar.asStateFlow()

    // ── Umbrales del oído afectado ────────────────────────────────────────────
    private val _thresholds = MutableStateFlow<Map<Int, Int>>(defaultThresholds)
    val thresholds: StateFlow<Map<Int, Int>> = _thresholds.asStateFlow()

    // ── Frecuencia central ────────────────────────────────────────────────────
    // Fuente de verdad: resultado del servidor ML.
    // NotchTherapyScreen y cualquier otra vista leen ESTE valor.
    private val _mlPredictedFc = MutableStateFlow<Int?>(null)
    val predictedFc: StateFlow<Int?> = _mlPredictedFc.asStateFlow()   // nombre público sin cambio

    // ── Estados auxiliares ────────────────────────────────────────────────────
    private val _savedProfile = MutableStateFlow<AudiometryProfile?>(null)
    val savedProfile: StateFlow<AudiometryProfile?> = _savedProfile.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _serverState = MutableStateFlow<ServerAnalysisState>(ServerAnalysisState.Idle)
    val serverState: StateFlow<ServerAnalysisState> = _serverState.asStateFlow()

    // ── Compatibilidad con NotchViewModel (lee left/right del perfil previo) ──
    // Se mantienen como alias del canal afectado para no romper código existente.
    val left : StateFlow<Map<Int, Int>> get() = _thresholds
    val right: StateFlow<Map<Int, Int>> get() = _thresholds

    // ─── Selección de oído ────────────────────────────────────────────────────

    fun selectEar(ear: String?) {           // "LEFT", "RIGHT", or null to reset
        _affectedEar.value = ear
    }

    // ─── Edición de umbrales ──────────────────────────────────────────────────

    fun setThreshold(freq: Int, db: Int) {
        _thresholds.value = _thresholds.value.toMutableMap().also { it[freq] = db }
    }

    // Para compatibilidad con código que llama setLeftThreshold / setRightThreshold
    fun setLeftThreshold(freq: Int, db: Int)  = setThreshold(freq, db)
    fun setRightThreshold(freq: Int, db: Int) = setThreshold(freq, db)

    // ─── Guardar y analizar con ML ────────────────────────────────────────────

    fun saveAndPredict(patientId: Long) {
        val ear  = _affectedEar.value ?: "LEFT"
        val data = _thresholds.value

        viewModelScope.launch {
            // Guarda provisionalmente sin fc ML (la actualiza cuando el servidor responde)
            val profile = AudiometryProfile(
                patientId        = patientId,
                affectedEar      = ear,
                channelData      = FrequencyPredictor.serializeChannelData(data),
                leftChannelData  = if (ear == "LEFT")  FrequencyPredictor.serializeChannelData(data) else "",
                rightChannelData = if (ear == "RIGHT") FrequencyPredictor.serializeChannelData(data) else "",
                mlPredictedFc    = null,
                predictedFc      = 4000
            )
            val newId = repository.saveProfile(profile)
            _savedProfile.value = profile.copy(id = newId)
            _message.value = "Audiometría guardada. Analizando con IA…"
        }

        // Llama al servidor ML y, si responde, actualiza predictedFc
        analyzeWithServer(data, patientId)
    }

    private fun analyzeWithServer(data: Map<Int, Int>, patientId: Long) {
        // Construye el request a partir del oído afectado
        fun db(freq: Int) = (data[freq] ?: 20).toFloat()
        val db3000 = ((data[2000] ?: 20) + (data[4000] ?: 20)) / 2f
        val db6000 = if (data.containsKey(6000)) db(6000)
                     else ((data[4000] ?: 20) + (data[8000] ?: 20)) / 2f

        val request = AudiogramRequest(
            db_250  = db(250),
            db_500  = db(500),
            db_1000 = db(1000),
            db_2000 = db(2000),
            db_3000 = db3000,
            db_4000 = db(4000),
            db_6000 = db6000,
            db_8000 = db(8000)
        )

        viewModelScope.launch {
            _serverState.value = ServerAnalysisState.Loading
            try {
                val response = TinnitusApi.service.analyze(TinnitusApi.API_KEY, request)
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    _serverState.value = ServerAnalysisState.Success(result)

                    // Frecuencia ML: usa central_freq_hz si hay tinnitus, si no null
                    val mlFc = if (result.tinnitus) result.central_freq_hz else null
                    _mlPredictedFc.value = mlFc

                    // Actualiza el perfil guardado con la fc ML
                    val ear  = _affectedEar.value ?: "LEFT"
                    val data2 = _thresholds.value
                    val updated = AudiometryProfile(
                        patientId        = patientId,
                        affectedEar      = ear,
                        channelData      = FrequencyPredictor.serializeChannelData(data2),
                        leftChannelData  = if (ear == "LEFT")  FrequencyPredictor.serializeChannelData(data2) else "",
                        rightChannelData = if (ear == "RIGHT") FrequencyPredictor.serializeChannelData(data2) else "",
                        mlPredictedFc    = mlFc,
                        predictedFc      = mlFc ?: 4000
                    )
                    repository.saveProfile(updated)
                    _savedProfile.value = updated

                    _message.value = if (mlFc != null)
                        "Análisis ML completo. Frecuencia central: ${FrequencyPredictor.freqLabel(mlFc)}"
                    else
                        "Análisis ML: no se detectó patrón de tinnitus."
                } else {
                    _serverState.value = ServerAnalysisState.Error("Error del servidor: ${response.code()}")
                }
            } catch (e: java.net.ConnectException)      { _serverState.value = ServerAnalysisState.Error("Sin conexión al servidor") }
              catch (e: java.net.SocketTimeoutException) { _serverState.value = ServerAnalysisState.Error("Timeout — reintenta más tarde") }
              catch (e: Exception)                       { _serverState.value = ServerAnalysisState.Error("Error: ${e.message}") }
        }
    }

    // ─── Cargar perfil previo ─────────────────────────────────────────────────

    fun loadLatestProfile(patientId: Long) {
        viewModelScope.launch {
            val profile = repository.getLatestForPatient(patientId) ?: return@launch
            _savedProfile.value = profile
            _affectedEar.value  = profile.affectedEar.ifBlank { "LEFT" }
            _mlPredictedFc.value = profile.mlPredictedFc

            // Carga umbrales: prefiere channelData; si vacío, usa el canal afectado viejo
            val channelStr = profile.channelData.ifBlank {
                if (profile.affectedEar == "RIGHT") profile.rightChannelData
                else profile.leftChannelData
            }
            _thresholds.value = FrequencyPredictor.parseChannelData(channelStr)
                .ifEmpty { defaultThresholds }
        }
    }

    fun clearMessage()     { _message.value = null }
    fun clearServerState() { _serverState.value = ServerAnalysisState.Idle }
}
