package com.tinnomore.`data`.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.tinnomore.`data`.db.entity.CrisisRecord
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Float
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
public class CrisisRecordDao_Impl(
  __db: RoomDatabase,
) : CrisisRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCrisisRecord: EntityInsertAdapter<CrisisRecord>
  init {
    this.__db = __db
    this.__insertAdapterOfCrisisRecord = object : EntityInsertAdapter<CrisisRecord>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `crisis_records` (`id`,`patientId`,`timestamp`,`audioFilePath`,`maxDecibels`,`therapyModified`,`modifiedIntensity`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CrisisRecord) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.patientId)
        statement.bindLong(3, entity.timestamp)
        val _tmpAudioFilePath: String? = entity.audioFilePath
        if (_tmpAudioFilePath == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpAudioFilePath)
        }
        statement.bindDouble(5, entity.maxDecibels.toDouble())
        val _tmp: Int = if (entity.therapyModified) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpModifiedIntensity: Float? = entity.modifiedIntensity
        if (_tmpModifiedIntensity == null) {
          statement.bindNull(7)
        } else {
          statement.bindDouble(7, _tmpModifiedIntensity.toDouble())
        }
      }
    }
  }

  public override suspend fun insert(record: CrisisRecord): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfCrisisRecord.insertAndReturnId(_connection, record)
    _result
  }

  public override fun getCrisisRecordsForPatient(patientId: Long): Flow<List<CrisisRecord>> {
    val _sql: String = "SELECT * FROM crisis_records WHERE patientId = ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("crisis_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, patientId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patientId")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfAudioFilePath: Int = getColumnIndexOrThrow(_stmt, "audioFilePath")
        val _columnIndexOfMaxDecibels: Int = getColumnIndexOrThrow(_stmt, "maxDecibels")
        val _columnIndexOfTherapyModified: Int = getColumnIndexOrThrow(_stmt, "therapyModified")
        val _columnIndexOfModifiedIntensity: Int = getColumnIndexOrThrow(_stmt, "modifiedIntensity")
        val _result: MutableList<CrisisRecord> = mutableListOf()
        while (_stmt.step()) {
          val _item: CrisisRecord
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpAudioFilePath: String?
          if (_stmt.isNull(_columnIndexOfAudioFilePath)) {
            _tmpAudioFilePath = null
          } else {
            _tmpAudioFilePath = _stmt.getText(_columnIndexOfAudioFilePath)
          }
          val _tmpMaxDecibels: Float
          _tmpMaxDecibels = _stmt.getDouble(_columnIndexOfMaxDecibels).toFloat()
          val _tmpTherapyModified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTherapyModified).toInt()
          _tmpTherapyModified = _tmp != 0
          val _tmpModifiedIntensity: Float?
          if (_stmt.isNull(_columnIndexOfModifiedIntensity)) {
            _tmpModifiedIntensity = null
          } else {
            _tmpModifiedIntensity = _stmt.getDouble(_columnIndexOfModifiedIntensity).toFloat()
          }
          _item = CrisisRecord(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpAudioFilePath,_tmpMaxDecibels,_tmpTherapyModified,_tmpModifiedIntensity)
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
