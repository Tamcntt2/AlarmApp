package com.example.smartalarm.model;

import android.media.MediaPlayer;
import android.media.Ringtone;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.smartalarm.database.AlarmConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

@Entity(tableName = "alarm")
public class Alarm {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long timeOfDay;
    private long time;
    private boolean isEnabled;
    private String titleRepeat;
    private String ringtoneTitle;

    public boolean getRepeat() {
        if (titleRepeat.compareTo("Never") == 0) return false;
        return true;
    }

    public long getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(long timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getTitleRepeat() {
        return titleRepeat;
    }

    public void setTitleRepeat(String titleRepeat) {
        this.titleRepeat = titleRepeat;
    }

    public Alarm() {
        this.isEnabled = true;
        this.titleRepeat = "Never";
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getRingtoneTitle() {
        return ringtoneTitle;
    }

    public void setRingtoneTitle(String ringtoneTitle) {
        this.ringtoneTitle = ringtoneTitle;
    }
}
