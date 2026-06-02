package com.tinnomore.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tinnomore.data.db.entity.AudiometryProfile;
import java.lang.Class;
import java.lang.Exception;
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
public final class AudiometryDao_Impl implements AudiometryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AudiometryProfile> __insertionAdapterOfAudiometryProfile;

  public AudiometryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAudiometryProfile = new EntityInsertionAdapter<AudiometryProfile>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `audiometry_profiles` (`id`,`patientId`,`timestamp`,`leftChannelData`,`rightChannelData`,`predictedFc`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AudiometryProfile entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPatientId());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindString(4, entity.getLeftChannelData());
        statement.bindString(5, entity.getRightChannelData());
        statement.bindLong(6, entity.getPredictedFc());
      }
    };
  }

  @Override
  public Object insert(final AudiometryProfile profile,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAudiometryProfile.insertAndReturnId(profile);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLatestForPatient(final long patientId,
      final Continuation<? super AudiometryProfile> $completion) {
    final String _sql = "SELECT * FROM audiometry_profiles WHERE patientId = ? ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, patientId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AudiometryProfile>() {
      @Override
      @Nullable
      public AudiometryProfile call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfLeftChannelData = CursorUtil.getColumnIndexOrThrow(_cursor, "leftChannelData");
          final int _cursorIndexOfRightChannelData = CursorUtil.getColumnIndexOrThrow(_cursor, "rightChannelData");
          final int _cursorIndexOfPredictedFc = CursorUtil.getColumnIndexOrThrow(_cursor, "predictedFc");
          final AudiometryProfile _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPatientId;
            _tmpPatientId = _cursor.getLong(_cursorIndexOfPatientId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpLeftChannelData;
            _tmpLeftChannelData = _cursor.getString(_cursorIndexOfLeftChannelData);
            final String _tmpRightChannelData;
            _tmpRightChannelData = _cursor.getString(_cursorIndexOfRightChannelData);
            final int _tmpPredictedFc;
            _tmpPredictedFc = _cursor.getInt(_cursorIndexOfPredictedFc);
            _result = new AudiometryProfile(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpLeftChannelData,_tmpRightChannelData,_tmpPredictedFc);
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

  @Override
  public Flow<List<AudiometryProfile>> getAllForPatient(final long patientId) {
    final String _sql = "SELECT * FROM audiometry_profiles WHERE patientId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, patientId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"audiometry_profiles"}, new Callable<List<AudiometryProfile>>() {
      @Override
      @NonNull
      public List<AudiometryProfile> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfLeftChannelData = CursorUtil.getColumnIndexOrThrow(_cursor, "leftChannelData");
          final int _cursorIndexOfRightChannelData = CursorUtil.getColumnIndexOrThrow(_cursor, "rightChannelData");
          final int _cursorIndexOfPredictedFc = CursorUtil.getColumnIndexOrThrow(_cursor, "predictedFc");
          final List<AudiometryProfile> _result = new ArrayList<AudiometryProfile>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AudiometryProfile _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPatientId;
            _tmpPatientId = _cursor.getLong(_cursorIndexOfPatientId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpLeftChannelData;
            _tmpLeftChannelData = _cursor.getString(_cursorIndexOfLeftChannelData);
            final String _tmpRightChannelData;
            _tmpRightChannelData = _cursor.getString(_cursorIndexOfRightChannelData);
            final int _tmpPredictedFc;
            _tmpPredictedFc = _cursor.getInt(_cursorIndexOfPredictedFc);
            _item = new AudiometryProfile(_tmpId,_tmpPatientId,_tmpTimestamp,_tmpLeftChannelData,_tmpRightChannelData,_tmpPredictedFc);
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
