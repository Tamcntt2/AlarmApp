package com.example.smartalarm.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.smartalarm.model.Alarm;


@Database(entities = {Alarm.class}, version = 1)
@TypeConverters({AlarmConverter.class})
public abstract class AlarmDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "listAlarm10.db";
    private static AlarmDatabase instance;

    public static synchronized AlarmDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AlarmDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries().build();
        }
        return instance;

    }

    public abstract AlarmDAO alarmDAO();
}
