package com.example.smartalarm.util;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;

import com.example.smartalarm.adapter.RingtoneAdapter;
import com.example.smartalarm.model.RingtoneAlarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RingtoneUtils {

    private Map<String, String> mapRingtone;
    private List<RingtoneAlarm> listRingtone;
    private Context context;

    public RingtoneUtils(Context context) {
        this.context = context;
        mapRingtone = new HashMap<>();
        listRingtone = new ArrayList<>();
        initData();
    }

    private void initData() {
        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = manager.getCursor();

        while (cursor.moveToNext()) {
            cursor.getPosition();
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            listRingtone.add(new RingtoneAlarm(notificationTitle));
            mapRingtone.put(notificationTitle, notificationUri);
        }
    }

    public int getPosition(String title) {
        int position = -1;
        for(int i=0; i<listRingtone.size(); i++) {
            if(listRingtone.get(i).getTitle().equals(title)) {
                position = i;
            }
        }
        return position;
    }

    public List<RingtoneAlarm> getListRingtone() {
        return listRingtone;
    }

    public String getUriRingtoneFromTitle(String title) {
        return mapRingtone.get(title);
    }

    public String getRingtoneDefault() {
        return listRingtone.get(0).getTitle();
    }
}
