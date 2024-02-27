package com.example.smartalarm.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.smartalarm.R;
import com.example.smartalarm.activity.QuestionActivity;
import com.example.smartalarm.database.AlarmDatabase;
import com.example.smartalarm.model.Alarm;
import com.example.smartalarm.util.RingtoneUtils;

import java.util.Calendar;
import java.util.List;

public class AlarmService extends Service {

    private boolean isStatus;
    Ringtone ringtone;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle data = intent.getExtras();
        String ringtoneTitle;
        int idAlarm;
        if (data != null) {
            isStatus = data.getBoolean("extra");
            ringtoneTitle = data.getString("ringtone");
            idAlarm = data.getInt("idAlarm");
        } else {
            String ringtoneDefault = new RingtoneUtils(this).getRingtoneDefault();
            isStatus = false;
            ringtoneTitle = ringtoneDefault;
            idAlarm = -1;
        }

        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }

        if (isStatus) {
            Intent intent1 = new Intent(this, QuestionActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("idAlarm", idAlarm);
            startActivity(intent1);

            RingtoneUtils ringtoneUtils = new RingtoneUtils(getApplicationContext());
            String notificationUri = ringtoneUtils.getUriRingtoneFromTitle(ringtoneTitle);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(notificationUri));
//            ringtone.setLooping(true);
            ringtone.play();

            Calendar cal = Calendar.getInstance();
            Log.d("Time Alarm Service", cal.get(Calendar.HOUR) + "-" + cal.get(Calendar.MINUTE) + "-" + cal.get(Calendar.SECOND) + "-" + cal.get(Calendar.MILLISECOND));
        }

        return START_NOT_STICKY;
    }
}
