package com.tinnomore.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.tinnomore.data.db.dao.AudiometryDao;
import com.tinnomore.data.db.dao.AudiometryDao_Impl;
import com.tinnomore.data.db.dao.CrisisRecordDao;
import com.tinnomore.data.db.dao.CrisisRecordDao_Impl;
import com.tinnomore.data.db.dao.SymptomDao;
import com.tinnomore.data.db.dao.SymptomDao_Impl;
import com.tinnomore.data.db.dao.UserDao;
import com.tinnomore.data.db.dao.UserDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserDao _userDao;

  private volatile SymptomDao _symptomDao;

  private volatile AudiometryDao _audiometryDao;

  private volatile CrisisRecordDao _crisisRecordDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `rut` TEXT NOT NULL, `email` TEXT NOT NULL, `password` TEXT NOT NULL, `role` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `symptoms` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patientId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `intensity` INTEGER NOT NULL, `durationMinutes` INTEGER, `sleepImpact` INTEGER, `concentrationImpact` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `audiometry_profiles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patientId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `leftChannelData` TEXT NOT NULL, `rightChannelData` TEXT NOT NULL, `predictedFc` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `crisis_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patientId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `audioFilePath` TEXT, `maxDecibels` REAL NOT NULL, `therapyModified` INTEGER NOT NULL, `modifiedIntensity` REAL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `therapy_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patientId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `notchFrequency` INTEGER NOT NULL, `intensityDb` REAL NOT NULL, `durationSeconds` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '10f7bd07bae6e0e6b59a0a104a497966')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `symptoms`");
        db.execSQL("DROP TABLE IF EXISTS `audiometry_profiles`");
        db.execSQL("DROP TABLE IF EXISTS `crisis_records`");
        db.execSQL("DROP TABLE IF EXISTS `therapy_sessions`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(6);
        _columnsUsers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("rut", new TableInfo.Column("rut", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("password", new TableInfo.Column("password", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.tinnomore.data.db.entity.User).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsSymptoms = new HashMap<String, TableInfo.Column>(7);
        _columnsSymptoms.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSymptoms.put("patientId", new TableInfo.Column("patientId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSymptoms.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSymptoms.put("intensity", new TableInfo.Column("intensity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSymptoms.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSymptoms.put("sleepImpact", new TableInfo.Column("sleepImpact", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSymptoms.put("concentrationImpact", new TableInfo.Column("concentrationImpact", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSymptoms = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSymptoms = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSymptoms = new TableInfo("symptoms", _columnsSymptoms, _foreignKeysSymptoms, _indicesSymptoms);
        final TableInfo _existingSymptoms = TableInfo.read(db, "symptoms");
        if (!_infoSymptoms.equals(_existingSymptoms)) {
          return new RoomOpenHelper.ValidationResult(false, "symptoms(com.tinnomore.data.db.entity.SymptomEntry).\n"
                  + " Expected:\n" + _infoSymptoms + "\n"
                  + " Found:\n" + _existingSymptoms);
        }
        final HashMap<String, TableInfo.Column> _columnsAudiometryProfiles = new HashMap<String, TableInfo.Column>(6);
        _columnsAudiometryProfiles.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAudiometryProfiles.put("patientId", new TableInfo.Column("patientId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAudiometryProfiles.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAudiometryProfiles.put("leftChannelData", new TableInfo.Column("leftChannelData", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAudiometryProfiles.put("rightChannelData", new TableInfo.Column("rightChannelData", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAudiometryProfiles.put("predictedFc", new TableInfo.Column("predictedFc", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAudiometryProfiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAudiometryProfiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAudiometryProfiles = new TableInfo("audiometry_profiles", _columnsAudiometryProfiles, _foreignKeysAudiometryProfiles, _indicesAudiometryProfiles);
        final TableInfo _existingAudiometryProfiles = TableInfo.read(db, "audiometry_profiles");
        if (!_infoAudiometryProfiles.equals(_existingAudiometryProfiles)) {
          return new RoomOpenHelper.ValidationResult(false, "audiometry_profiles(com.tinnomore.data.db.entity.AudiometryProfile).\n"
                  + " Expected:\n" + _infoAudiometryProfiles + "\n"
                  + " Found:\n" + _existingAudiometryProfiles);
        }
        final HashMap<String, TableInfo.Column> _columnsCrisisRecords = new HashMap<String, TableInfo.Column>(7);
        _columnsCrisisRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrisisRecords.put("patientId", new TableInfo.Column("patientId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrisisRecords.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrisisRecords.put("audioFilePath", new TableInfo.Column("audioFilePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrisisRecords.put("maxDecibels", new TableInfo.Column("maxDecibels", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrisisRecords.put("therapyModified", new TableInfo.Column("therapyModified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrisisRecords.put("modifiedIntensity", new TableInfo.Column("modifiedIntensity", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCrisisRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCrisisRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCrisisRecords = new TableInfo("crisis_records", _columnsCrisisRecords, _foreignKeysCrisisRecords, _indicesCrisisRecords);
        final TableInfo _existingCrisisRecords = TableInfo.read(db, "crisis_records");
        if (!_infoCrisisRecords.equals(_existingCrisisRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "crisis_records(com.tinnomore.data.db.entity.CrisisRecord).\n"
                  + " Expected:\n" + _infoCrisisRecords + "\n"
                  + " Found:\n" + _existingCrisisRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsTherapySessions = new HashMap<String, TableInfo.Column>(6);
        _columnsTherapySessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTherapySessions.put("patientId", new TableInfo.Column("patientId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTherapySessions.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTherapySessions.put("notchFrequency", new TableInfo.Column("notchFrequency", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTherapySessions.put("intensityDb", new TableInfo.Column("intensityDb", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTherapySessions.put("durationSeconds", new TableInfo.Column("durationSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTherapySessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTherapySessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTherapySessions = new TableInfo("therapy_sessions", _columnsTherapySessions, _foreignKeysTherapySessions, _indicesTherapySessions);
        final TableInfo _existingTherapySessions = TableInfo.read(db, "therapy_sessions");
        if (!_infoTherapySessions.equals(_existingTherapySessions)) {
          return new RoomOpenHelper.ValidationResult(false, "therapy_sessions(com.tinnomore.data.db.entity.TherapySession).\n"
                  + " Expected:\n" + _infoTherapySessions + "\n"
                  + " Found:\n" + _existingTherapySessions);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "10f7bd07bae6e0e6b59a0a104a497966", "e43738613c94d7fe75f29763fe883f34");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users","symptoms","audiometry_profiles","crisis_records","therapy_sessions");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `symptoms`");
      _db.execSQL("DELETE FROM `audiometry_profiles`");
      _db.execSQL("DELETE FROM `crisis_records`");
      _db.execSQL("DELETE FROM `therapy_sessions`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SymptomDao.class, SymptomDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AudiometryDao.class, AudiometryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CrisisRecordDao.class, CrisisRecordDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public SymptomDao symptomDao() {
    if (_symptomDao != null) {
      return _symptomDao;
    } else {
      synchronized(this) {
        if(_symptomDao == null) {
          _symptomDao = new SymptomDao_Impl(this);
        }
        return _symptomDao;
      }
    }
  }

  @Override
  public AudiometryDao audiometryDao() {
    if (_audiometryDao != null) {
      return _audiometryDao;
    } else {
      synchronized(this) {
        if(_audiometryDao == null) {
          _audiometryDao = new AudiometryDao_Impl(this);
        }
        return _audiometryDao;
      }
    }
  }

  @Override
  public CrisisRecordDao crisisRecordDao() {
    if (_crisisRecordDao != null) {
      return _crisisRecordDao;
    } else {
      synchronized(this) {
        if(_crisisRecordDao == null) {
          _crisisRecordDao = new CrisisRecordDao_Impl(this);
        }
        return _crisisRecordDao;
      }
    }
  }
}
