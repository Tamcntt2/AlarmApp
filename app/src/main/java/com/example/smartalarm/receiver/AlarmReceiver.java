package com.example.smartalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.smartalarm.service.AlarmService;
import com.example.smartalarm.util.RingtoneUtils;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isStatus = intent.getBooleanExtra("extra", false);
        String ringtoneDefault = new RingtoneUtils(context).getRingtoneDefault();
        String ringtoneTitle = intent.getExtras().getString("ringtone", ringtoneDefault);
        int idAlarm = intent.getIntExtra("idAlarm", -1);

        Calendar calendar = Calendar.getInstance();
        Log.d("Time Alarm Receiver", calendar.get(Calendar.HOUR) + "-" + calendar.get(Calendar.MINUTE) + "-" +
                calendar.get(Calendar.SECOND) + "-" + calendar.get(Calendar.MILLISECOND));

        Intent myIntent = new Intent(context, AlarmService.class);
        Bundle data = new Bundle();
        data.putBoolean("extra", isStatus);
        data.putString("ringtone", ringtoneTitle);
        data.putInt("idAlarm", idAlarm);
        myIntent.putExtras(data);
        context.startService(myIntent);
    }
}
