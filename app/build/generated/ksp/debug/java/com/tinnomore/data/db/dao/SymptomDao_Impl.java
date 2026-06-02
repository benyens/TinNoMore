package com.tinnomore.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tinnomore.data.db.entity.SymptomEntry;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SymptomDao_Impl implements SymptomDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SymptomEntry> __insertionAdapterOfSymptomEntry;

  private final EntityDeletionOrUpdateAdapter<SymptomEntry> __deletionAdapterOfSymptomEntry;

  private final EntityDeletionOrUpdateAdapter<SymptomEntry> __updateAdapterOfSymptomEntry;

  public SymptomDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSymptomEntry = new EntityInsertionAdapter<SymptomEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `symptoms` (`id`,`patientId`,`timestamp`,`intensity`,`durationMinutes`,`sleepImpact`,`concentrationImpact`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SymptomEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPatientId());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindLong(4, entity.getIntensity());
        if (entity.getDurationMinutes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getDurationMinutes());
        }
        if (entity.getSleepImpact() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getSleepImpact());
        }
        if (entity.getConcentrationImpact() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getConcentrationImpact());
        }
      }
    };
    this.__deletionAdapterOfSymptomEntry = new EntityDeletionOrUpdateAdapter<SymptomEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `symptoms` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SymptomEntry entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSymptomEntry = new EntityDeletionOrUpdateAdapter<SymptomEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `symptoms` SET `id` = ?,`patientId` = ?,`timestamp` = ?,`intensity` = ?,`durationMinutes` = ?,`sleepImpact` = ?,`concentrationImpact` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SymptomEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPatientId());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindLong(4, entity.getIntensity());
        if (entity.getDurationMinutes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getDurationMinutes());
        }
        if (entity.getSleepImpact() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getSleepImpact());
        }
        if (entity.getConcentrationImpact() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getConcentrationImpact());
        }
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final SymptomEntry symptom, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSymptomEntry.insertAndReturnId(symptom);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final SymptomEntry symptom, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSymptomEntry.handle(symptom);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final SymptomEntry symptom, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSymptomEntry.handle(symptom);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SymptomEntry>> getSymptomsForPatient(final long patientId) {
    final String _sql = "SELECT * FROM symptoms WHERE patientId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, patientId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"symptoms"}, new Callable<List<SymptomEntry>>() {
      @Override
      @NonNull
      public List<SymptomEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfSleepImpact = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepImpact");
          final int _cursorIndexOfConcentrationImpact = CursorUtil.getColumnIndexOrThrow(_cursor, "concentrationImpact");
          final List<SymptomEntry> _result = new ArrayList<SymptomEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SymptomEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPatientId;
            _tmpPatientId = _cursor.getLong(_cursorIndexOfPatientId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpIntensity;
            _tmpIntensity = _cursor.getInt(_cursorIndexOfIntensity);
            final Integer _tmpDurationMinutes;
            if (_cursor.isNull(_cursorIndexOfDurationMinutes)) {
              _tmpDurationMinutes = null;
            } else {
              _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            }
            final Integer _tmpSleepImpact;
            if (_cursor.isNull(_cursorIndexOfSleepImpact)) {
              _tmpSleepImpact = null;
            } else {
              _tmpSleepImpact = _cursor.getInt(_cursorIndexOfSleepImpact);
            }
            final Integer _tmpConcentrationImpact;
            if (_cursor.isNull(_cursorIndexOfConcentrationImpact)) {
              _tmpConcentrationImpact = null;
            } else {
              _tmpConcentrationImpact = _cursor.getInt(_cursorIndexOfConcentrationImpact);
            }
            _item = new SymptomEntry(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpIntensity,_tmpDurationMinutes,_tmpSleepImpact,_tmpConcentrationImpact);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<SymptomEntry>> getSymptomsForPatientBetween(final long patientId,
      final long from, final long to) {
    final String _sql = "\n"
            + "        SELECT * FROM symptoms\n"
            + "        WHERE patientId = ? AND timestamp BETWEEN ? AND ?\n"
            + "        ORDER BY timestamp DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, patientId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, from);
    _argIndex = 3;
    _statement.bindLong(_argIndex, to);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"symptoms"}, new Callable<List<SymptomEntry>>() {
      @Override
      @NonNull
      public List<SymptomEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfSleepImpact = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepImpact");
          final int _cursorIndexOfConcentrationImpact = CursorUtil.getColumnIndexOrThrow(_cursor, "concentrationImpact");
          final List<SymptomEntry> _result = new ArrayList<SymptomEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SymptomEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPatientId;
            _tmpPatientId = _cursor.getLong(_cursorIndexOfPatientId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpIntensity;
            _tmpIntensity = _cursor.getInt(_cursorIndexOfIntensity);
            final Integer _tmpDurationMinutes;
            if (_cursor.isNull(_cursorIndexOfDurationMinutes)) {
              _tmpDurationMinutes = null;
            } else {
              _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            }
            final Integer _tmpSleepImpact;
            if (_cursor.isNull(_cursorIndexOfSleepImpact)) {
              _tmpSleepImpact = null;
            } else {
              _tmpSleepImpact = _cursor.getInt(_cursorIndexOfSleepImpact);
            }
            final Integer _tmpConcentrationImpact;
            if (_cursor.isNull(_cursorIndexOfConcentrationImpact)) {
              _tmpConcentrationImpact = null;
            } else {
              _tmpConcentrationImpact = _cursor.getInt(_cursorIndexOfConcentrationImpact);
            }
            _item = new SymptomEntry(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpIntensity,_tmpDurationMinutes,_tmpSleepImpact,_tmpConcentrationImpact);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id, final Continuation<? super SymptomEntry> $completion) {
    final String _sql = "SELECT * FROM symptoms WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SymptomEntry>() {
      @Override
      @Nullable
      public SymptomEntry call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfSleepImpact = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepImpact");
          final int _cursorIndexOfConcentrationImpact = CursorUtil.getColumnIndexOrThrow(_cursor, "concentrationImpact");
          final SymptomEntry _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPatientId;
            _tmpPatientId = _cursor.getLong(_cursorIndexOfPatientId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpIntensity;
            _tmpIntensity = _cursor.getInt(_cursorIndexOfIntensity);
            final Integer _tmpDurationMinutes;
            if (_cursor.isNull(_cursorIndexOfDurationMinutes)) {
              _tmpDurationMinutes = null;
            } else {
              _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            }
            final Integer _tmpSleepImpact;
            if (_cursor.isNull(_cursorIndexOfSleepImpact)) {
              _tmpSleepImpact = null;
            } else {
              _tmpSleepImpact = _cursor.getInt(_cursorIndexOfSleepImpact);
            }
            final Integer _tmpConcentrationImpact;
            if (_cursor.isNull(_cursorIndexOfConcentrationImpact)) {
              _tmpConcentrationImpact = null;
            } else {
              _tmpConcentrationImpact = _cursor.getInt(_cursorIndexOfConcentrationImpact);
            }
            _result = new SymptomEntry(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpIntensity,_tmpDurationMinutes,_tmpSleepImpact,_tmpConcentrationImpact);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
