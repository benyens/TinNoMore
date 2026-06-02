package com.tinnomore.data.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tinnomore.data.db.entity.CrisisRecord;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
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
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CrisisRecordDao_Impl implements CrisisRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CrisisRecord> __insertionAdapterOfCrisisRecord;

  public CrisisRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCrisisRecord = new EntityInsertionAdapter<CrisisRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `crisis_records` (`id`,`patientId`,`timestamp`,`audioFilePath`,`maxDecibels`,`therapyModified`,`modifiedIntensity`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CrisisRecord entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPatientId());
        statement.bindLong(3, entity.getTimestamp());
        if (entity.getAudioFilePath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getAudioFilePath());
        }
        statement.bindDouble(5, entity.getMaxDecibels());
        final int _tmp = entity.getTherapyModified() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getModifiedIntensity() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getModifiedIntensity());
        }
      }
    };
  }

  @Override
  public Object insert(final CrisisRecord record, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCrisisRecord.insertAndReturnId(record);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CrisisRecord>> getCrisisRecordsForPatient(final long patientId) {
    final String _sql = "SELECT * FROM crisis_records WHERE patientId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, patientId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"crisis_records"}, new Callable<List<CrisisRecord>>() {
      @Override
      @NonNull
      public List<CrisisRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfAudioFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "audioFilePath");
          final int _cursorIndexOfMaxDecibels = CursorUtil.getColumnIndexOrThrow(_cursor, "maxDecibels");
          final int _cursorIndexOfTherapyModified = CursorUtil.getColumnIndexOrThrow(_cursor, "therapyModified");
          final int _cursorIndexOfModifiedIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedIntensity");
          final List<CrisisRecord> _result = new ArrayList<CrisisRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CrisisRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPatientId;
            _tmpPatientId = _cursor.getLong(_cursorIndexOfPatientId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpAudioFilePath;
            if (_cursor.isNull(_cursorIndexOfAudioFilePath)) {
              _tmpAudioFilePath = null;
            } else {
              _tmpAudioFilePath = _cursor.getString(_cursorIndexOfAudioFilePath);
            }
            final float _tmpMaxDecibels;
            _tmpMaxDecibels = _cursor.getFloat(_cursorIndexOfMaxDecibels);
            final boolean _tmpTherapyModified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfTherapyModified);
            _tmpTherapyModified = _tmp != 0;
            final Float _tmpModifiedIntensity;
            if (_cursor.isNull(_cursorIndexOfModifiedIntensity)) {
              _tmpModifiedIntensity = null;
            } else {
              _tmpModifiedIntensity = _cursor.getFloat(_cursorIndexOfModifiedIntensity);
            }
            _item = new CrisisRecord(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpAudioFilePath,_tmpMaxDecibels,_tmpTherapyModified,_tmpModifiedIntensity);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
