package com.tinnomore.`data`.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.tinnomore.`data`.db.dao.AudiometryDao
import com.tinnomore.`data`.db.dao.AudiometryDao_Impl
import com.tinnomore.`data`.db.dao.CrisisRecordDao
import com.tinnomore.`data`.db.dao.CrisisRecordDao_Impl
import com.tinnomore.`data`.db.dao.SymptomDao
import com.tinnomore.`data`.db.dao.SymptomDao_Impl
import com.tinnomore.`data`.db.dao.UserDao
import com.tinnomore.`data`.db.dao.UserDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _userDao: Lazy<UserDao> = lazy {
    UserDao_Impl(this)
  }

  private val _symptomDao: Lazy<SymptomDao> = lazy {
    SymptomDao_Impl(this)
  }

  private val _audiometryDao: Lazy<AudiometryDao> = lazy {
    AudiometryDao_Impl(this)
  }

  private val _crisisRecordDao: Lazy<CrisisRecordDao> = lazy {
    CrisisRecordDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "10f7bd07bae6e0e6b59a0a104a497966", "e43738613c94d7fe75f29763fe883f34") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `rut` TEXT NOT NULL, `email` TEXT NOT NULL, `password` TEXT NOT NULL, `role` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `symptoms` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patientId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `intensity` INTEGER NOT NULL, `durationMinutes` INTEGER, `sleepImpact` INTEGER, `concentrationImpact` INTEGER)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `audiometry_profiles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patientId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `leftChannelData` TEXT NOT NULL, `rightChannelData` TEXT NOT NULL, `predictedFc` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `crisis_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patientId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `audioFilePath` TEXT, `maxDecibels` REAL NOT NULL, `therapyModified` INTEGER NOT NULL, `modifiedIntensity` REAL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `therapy_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patientId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `notchFrequency` INTEGER NOT NULL, `intensityDb` REAL NOT NULL, `durationSeconds` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '10f7bd07bae6e0e6b59a0a104a497966')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `users`")
        connection.execSQL("DROP TABLE IF EXISTS `symptoms`")
        connection.execSQL("DROP TABLE IF EXISTS `audiometry_profiles`")
        connection.execSQL("DROP TABLE IF EXISTS `crisis_records`")
        connection.execSQL("DROP TABLE IF EXISTS `therapy_sessions`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsUsers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsers.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("rut", TableInfo.Column("rut", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("email", TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("password", TableInfo.Column("password", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("role", TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsers: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUsers: TableInfo = TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers)
        val _existingUsers: TableInfo = read(connection, "users")
        if (!_infoUsers.equals(_existingUsers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |users(com.tinnomore.data.db.entity.User).
              | Expected:
              |""".trimMargin() + _infoUsers + """
              |
              | Found:
              |""".trimMargin() + _existingUsers)
        }
        val _columnsSymptoms: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSymptoms.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSymptoms.put("patientId", TableInfo.Column("patientId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSymptoms.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSymptoms.put("intensity", TableInfo.Column("intensity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSymptoms.put("durationMinutes", TableInfo.Column("durationMinutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSymptoms.put("sleepImpact", TableInfo.Column("sleepImpact", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSymptoms.put("concentrationImpact", TableInfo.Column("concentrationImpact", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSymptoms: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSymptoms: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSymptoms: TableInfo = TableInfo("symptoms", _columnsSymptoms, _foreignKeysSymptoms, _indicesSymptoms)
        val _existingSymptoms: TableInfo = read(connection, "symptoms")
        if (!_infoSymptoms.equals(_existingSymptoms)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |symptoms(com.tinnomore.data.db.entity.SymptomEntry).
              | Expected:
              |""".trimMargin() + _infoSymptoms + """
              |
              | Found:
              |""".trimMargin() + _existingSymptoms)
        }
        val _columnsAudiometryProfiles: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAudiometryProfiles.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAudiometryProfiles.put("patientId", TableInfo.Column("patientId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAudiometryProfiles.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAudiometryProfiles.put("leftChannelData", TableInfo.Column("leftChannelData", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAudiometryProfiles.put("rightChannelData", TableInfo.Column("rightChannelData", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAudiometryProfiles.put("predictedFc", TableInfo.Column("predictedFc", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAudiometryProfiles: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAudiometryProfiles: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAudiometryProfiles: TableInfo = TableInfo("audiometry_profiles", _columnsAudiometryProfiles, _foreignKeysAudiometryProfiles, _indicesAudiometryProfiles)
        val _existingAudiometryProfiles: TableInfo = read(connection, "audiometry_profiles")
        if (!_infoAudiometryProfiles.equals(_existingAudiometryProfiles)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |audiometry_profiles(com.tinnomore.data.db.entity.AudiometryProfile).
              | Expected:
              |""".trimMargin() + _infoAudiometryProfiles + """
              |
              | Found:
              |""".trimMargin() + _existingAudiometryProfiles)
        }
        val _columnsCrisisRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCrisisRecords.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCrisisRecords.put("patientId", TableInfo.Column("patientId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCrisisRecords.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCrisisRecords.put("audioFilePath", TableInfo.Column("audioFilePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCrisisRecords.put("maxDecibels", TableInfo.Column("maxDecibels", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCrisisRecords.put("therapyModified", TableInfo.Column("therapyModified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCrisisRecords.put("modifiedIntensity", TableInfo.Column("modifiedIntensity", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCrisisRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCrisisRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCrisisRecords: TableInfo = TableInfo("crisis_records", _columnsCrisisRecords, _foreignKeysCrisisRecords, _indicesCrisisRecords)
        val _existingCrisisRecords: TableInfo = read(connection, "crisis_records")
        if (!_infoCrisisRecords.equals(_existingCrisisRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |crisis_records(com.tinnomore.data.db.entity.CrisisRecord).
              | Expected:
              |""".trimMargin() + _infoCrisisRecords + """
              |
              | Found:
              |""".trimMargin() + _existingCrisisRecords)
        }
        val _columnsTherapySessions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTherapySessions.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTherapySessions.put("patientId", TableInfo.Column("patientId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTherapySessions.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTherapySessions.put("notchFrequency", TableInfo.Column("notchFrequency", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTherapySessions.put("intensityDb", TableInfo.Column("intensityDb", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTherapySessions.put("durationSeconds", TableInfo.Column("durationSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTherapySessions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTherapySessions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTherapySessions: TableInfo = TableInfo("therapy_sessions", _columnsTherapySessions, _foreignKeysTherapySessions, _indicesTherapySessions)
        val _existingTherapySessions: TableInfo = read(connection, "therapy_sessions")
        if (!_infoTherapySessions.equals(_existingTherapySessions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |therapy_sessions(com.tinnomore.data.db.entity.TherapySession).
              | Expected:
              |""".trimMargin() + _infoTherapySessions + """
              |
              | Found:
              |""".trimMargin() + _existingTherapySessions)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "users", "symptoms", "audiometry_profiles", "crisis_records", "therapy_sessions")
  }

  public override fun clearAllTables() {
    super.performClear(false, "users", "symptoms", "audiometry_profiles", "crisis_records", "therapy_sessions")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(UserDao::class, UserDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SymptomDao::class, SymptomDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AudiometryDao::class, AudiometryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CrisisRecordDao::class, CrisisRecordDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun userDao(): UserDao = _userDao.value

  public override fun symptomDao(): SymptomDao = _symptomDao.value

  public override fun audiometryDao(): AudiometryDao = _audiometryDao.value

  public override fun crisisRecordDao(): CrisisRecordDao = _crisisRecordDao.value
}
