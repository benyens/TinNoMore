package com.tinnomore.`data`.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.tinnomore.`data`.db.entity.AudiometryProfile
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AudiometryDao_Impl(
  __db: RoomDatabase,
) : AudiometryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAudiometryProfile: EntityInsertAdapter<AudiometryProfile>
  init {
    this.__db = __db
    this.__insertAdapterOfAudiometryProfile = object : EntityInsertAdapter<AudiometryProfile>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `audiometry_profiles` (`id`,`patientId`,`timestamp`,`leftChannelData`,`rightChannelData`,`predictedFc`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AudiometryProfile) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.patientId)
        statement.bindLong(3, entity.timestamp)
        statement.bindText(4, entity.leftChannelData)
        statement.bindText(5, entity.rightChannelData)
        statement.bindLong(6, entity.predictedFc.toLong())
      }
    }
  }

  public override suspend fun insert(profile: AudiometryProfile): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfAudiometryProfile.insertAndReturnId(_connection, profile)
    _result
  }

  public override suspend fun getLatestForPatient(patientId: Long): AudiometryProfile? {
    val _sql: String = "SELECT * FROM audiometry_profiles WHERE patientId = ? ORDER BY timestamp DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, patientId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patientId")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfLeftChannelData: Int = getColumnIndexOrThrow(_stmt, "leftChannelData")
        val _columnIndexOfRightChannelData: Int = getColumnIndexOrThrow(_stmt, "rightChannelData")
        val _columnIndexOfPredictedFc: Int = getColumnIndexOrThrow(_stmt, "predictedFc")
        val _result: AudiometryProfile?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpLeftChannelData: String
          _tmpLeftChannelData = _stmt.getText(_columnIndexOfLeftChannelData)
          val _tmpRightChannelData: String
          _tmpRightChannelData = _stmt.getText(_columnIndexOfRightChannelData)
          val _tmpPredictedFc: Int
          _tmpPredictedFc = _stmt.getLong(_columnIndexOfPredictedFc).toInt()
          _result = AudiometryProfile(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpLeftChannelData,_tmpRightChannelData,_tmpPredictedFc)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllForPatient(patientId: Long): Flow<List<AudiometryProfile>> {
    val _sql: String = "SELECT * FROM audiometry_profiles WHERE patientId = ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("audiometry_profiles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, patientId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patientId")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfLeftChannelData: Int = getColumnIndexOrThrow(_stmt, "leftChannelData")
        val _columnIndexOfRightChannelData: Int = getColumnIndexOrThrow(_stmt, "rightChannelData")
        val _columnIndexOfPredictedFc: Int = getColumnIndexOrThrow(_stmt, "predictedFc")
        val _result: MutableList<AudiometryProfile> = mutableListOf()
        while (_stmt.step()) {
          val _item: AudiometryProfile
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpLeftChannelData: String
          _tmpLeftChannelData = _stmt.getText(_columnIndexOfLeftChannelData)
          val _tmpRightChannelData: String
          _tmpRightChannelData = _stmt.getText(_columnIndexOfRightChannelData)
          val _tmpPredictedFc: Int
          _tmpPredictedFc = _stmt.getLong(_columnIndexOfPredictedFc).toInt()
          _item = AudiometryProfile(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpLeftChannelData,_tmpRightChannelData,_tmpPredictedFc)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
