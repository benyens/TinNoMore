package com.tinnomore.`data`.db.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.tinnomore.`data`.db.entity.SymptomEntry
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
public class SymptomDao_Impl(
  __db: RoomDatabase,
) : SymptomDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSymptomEntry: EntityInsertAdapter<SymptomEntry>

  private val __deleteAdapterOfSymptomEntry: EntityDeleteOrUpdateAdapter<SymptomEntry>

  private val __updateAdapterOfSymptomEntry: EntityDeleteOrUpdateAdapter<SymptomEntry>
  init {
    this.__db = __db
    this.__insertAdapterOfSymptomEntry = object : EntityInsertAdapter<SymptomEntry>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `symptoms` (`id`,`patientId`,`timestamp`,`intensity`,`durationMinutes`,`sleepImpact`,`concentrationImpact`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SymptomEntry) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.patientId)
        statement.bindLong(3, entity.timestamp)
        statement.bindLong(4, entity.intensity.toLong())
        val _tmpDurationMinutes: Int? = entity.durationMinutes
        if (_tmpDurationMinutes == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpDurationMinutes.toLong())
        }
        val _tmpSleepImpact: Int? = entity.sleepImpact
        if (_tmpSleepImpact == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpSleepImpact.toLong())
        }
        val _tmpConcentrationImpact: Int? = entity.concentrationImpact
        if (_tmpConcentrationImpact == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpConcentrationImpact.toLong())
        }
      }
    }
    this.__deleteAdapterOfSymptomEntry = object : EntityDeleteOrUpdateAdapter<SymptomEntry>() {
      protected override fun createQuery(): String = "DELETE FROM `symptoms` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SymptomEntry) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfSymptomEntry = object : EntityDeleteOrUpdateAdapter<SymptomEntry>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `symptoms` SET `id` = ?,`patientId` = ?,`timestamp` = ?,`intensity` = ?,`durationMinutes` = ?,`sleepImpact` = ?,`concentrationImpact` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SymptomEntry) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.patientId)
        statement.bindLong(3, entity.timestamp)
        statement.bindLong(4, entity.intensity.toLong())
        val _tmpDurationMinutes: Int? = entity.durationMinutes
        if (_tmpDurationMinutes == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpDurationMinutes.toLong())
        }
        val _tmpSleepImpact: Int? = entity.sleepImpact
        if (_tmpSleepImpact == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpSleepImpact.toLong())
        }
        val _tmpConcentrationImpact: Int? = entity.concentrationImpact
        if (_tmpConcentrationImpact == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpConcentrationImpact.toLong())
        }
        statement.bindLong(8, entity.id)
      }
    }
  }

  public override suspend fun insert(symptom: SymptomEntry): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfSymptomEntry.insertAndReturnId(_connection, symptom)
    _result
  }

  public override suspend fun delete(symptom: SymptomEntry): Int = performSuspending(__db, false, true) { _connection ->
    var _result: Int = 0
    _result += __deleteAdapterOfSymptomEntry.handle(_connection, symptom)
    _result
  }

  public override suspend fun update(symptom: SymptomEntry): Int = performSuspending(__db, false, true) { _connection ->
    var _result: Int = 0
    _result += __updateAdapterOfSymptomEntry.handle(_connection, symptom)
    _result
  }

  public override fun getSymptomsForPatient(patientId: Long): Flow<List<SymptomEntry>> {
    val _sql: String = "SELECT * FROM symptoms WHERE patientId = ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("symptoms")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, patientId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patientId")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfIntensity: Int = getColumnIndexOrThrow(_stmt, "intensity")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "durationMinutes")
        val _columnIndexOfSleepImpact: Int = getColumnIndexOrThrow(_stmt, "sleepImpact")
        val _columnIndexOfConcentrationImpact: Int = getColumnIndexOrThrow(_stmt, "concentrationImpact")
        val _result: MutableList<SymptomEntry> = mutableListOf()
        while (_stmt.step()) {
          val _item: SymptomEntry
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpIntensity: Int
          _tmpIntensity = _stmt.getLong(_columnIndexOfIntensity).toInt()
          val _tmpDurationMinutes: Int?
          if (_stmt.isNull(_columnIndexOfDurationMinutes)) {
            _tmpDurationMinutes = null
          } else {
            _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          }
          val _tmpSleepImpact: Int?
          if (_stmt.isNull(_columnIndexOfSleepImpact)) {
            _tmpSleepImpact = null
          } else {
            _tmpSleepImpact = _stmt.getLong(_columnIndexOfSleepImpact).toInt()
          }
          val _tmpConcentrationImpact: Int?
          if (_stmt.isNull(_columnIndexOfConcentrationImpact)) {
            _tmpConcentrationImpact = null
          } else {
            _tmpConcentrationImpact = _stmt.getLong(_columnIndexOfConcentrationImpact).toInt()
          }
          _item = SymptomEntry(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpIntensity,_tmpDurationMinutes,_tmpSleepImpact,_tmpConcentrationImpact)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSymptomsForPatientBetween(
    patientId: Long,
    from: Long,
    to: Long,
  ): Flow<List<SymptomEntry>> {
    val _sql: String = """
        |
        |        SELECT * FROM symptoms
        |        WHERE patientId = ? AND timestamp BETWEEN ? AND ?
        |        ORDER BY timestamp DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("symptoms")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, patientId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, from)
        _argIndex = 3
        _stmt.bindLong(_argIndex, to)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patientId")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfIntensity: Int = getColumnIndexOrThrow(_stmt, "intensity")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "durationMinutes")
        val _columnIndexOfSleepImpact: Int = getColumnIndexOrThrow(_stmt, "sleepImpact")
        val _columnIndexOfConcentrationImpact: Int = getColumnIndexOrThrow(_stmt, "concentrationImpact")
        val _result: MutableList<SymptomEntry> = mutableListOf()
        while (_stmt.step()) {
          val _item: SymptomEntry
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpIntensity: Int
          _tmpIntensity = _stmt.getLong(_columnIndexOfIntensity).toInt()
          val _tmpDurationMinutes: Int?
          if (_stmt.isNull(_columnIndexOfDurationMinutes)) {
            _tmpDurationMinutes = null
          } else {
            _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          }
          val _tmpSleepImpact: Int?
          if (_stmt.isNull(_columnIndexOfSleepImpact)) {
            _tmpSleepImpact = null
          } else {
            _tmpSleepImpact = _stmt.getLong(_columnIndexOfSleepImpact).toInt()
          }
          val _tmpConcentrationImpact: Int?
          if (_stmt.isNull(_columnIndexOfConcentrationImpact)) {
            _tmpConcentrationImpact = null
          } else {
            _tmpConcentrationImpact = _stmt.getLong(_columnIndexOfConcentrationImpact).toInt()
          }
          _item = SymptomEntry(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpIntensity,_tmpDurationMinutes,_tmpSleepImpact,_tmpConcentrationImpact)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: Long): SymptomEntry? {
    val _sql: String = "SELECT * FROM symptoms WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patientId")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfIntensity: Int = getColumnIndexOrThrow(_stmt, "intensity")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "durationMinutes")
        val _columnIndexOfSleepImpact: Int = getColumnIndexOrThrow(_stmt, "sleepImpact")
        val _columnIndexOfConcentrationImpact: Int = getColumnIndexOrThrow(_stmt, "concentrationImpact")
        val _result: SymptomEntry?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpIntensity: Int
          _tmpIntensity = _stmt.getLong(_columnIndexOfIntensity).toInt()
          val _tmpDurationMinutes: Int?
          if (_stmt.isNull(_columnIndexOfDurationMinutes)) {
            _tmpDurationMinutes = null
          } else {
            _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          }
          val _tmpSleepImpact: Int?
          if (_stmt.isNull(_columnIndexOfSleepImpact)) {
            _tmpSleepImpact = null
          } else {
            _tmpSleepImpact = _stmt.getLong(_columnIndexOfSleepImpact).toInt()
          }
          val _tmpConcentrationImpact: Int?
          if (_stmt.isNull(_columnIndexOfConcentrationImpact)) {
            _tmpConcentrationImpact = null
          } else {
            _tmpConcentrationImpact = _stmt.getLong(_columnIndexOfConcentrationImpact).toInt()
          }
          _result = SymptomEntry(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpIntensity,_tmpDurationMinutes,_tmpSleepImpact,_tmpConcentrationImpact)
        } else {
          _result = null
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
