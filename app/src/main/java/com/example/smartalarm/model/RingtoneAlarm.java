package com.example.smartalarm.model;

import android.database.Cursor;
import android.media.RingtoneManager;

import java.util.HashMap;
import java.util.Map;

public class RingtoneAlarm {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RingtoneAlarm(String title) {
        this.title = title;
    }
}
