package com.example.smartalarm.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smartalarm.adapter.RingtoneAdapter;
import com.example.smartalarm.my_interface.IAlarmManager;
import com.example.smartalarm.my_interface.ItemTouchHelperListener;
import com.example.smartalarm.R;
import com.example.smartalarm.adapter.RecyclerViewItemTouchHelper;
import com.example.smartalarm.adapter.AlarmAdapter;
import com.example.smartalarm.database.AlarmConverter;
import com.example.smartalarm.model.Alarm;
import com.example.smartalarm.receiver.AlarmReceiver;
import com.example.smartalarm.util.RingtoneUtils;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements ItemTouchHelperListener {

    private RecyclerView rcvAlarm;
    private AlarmAdapter alarmAdapter;
    private LinearLayout layoutRoot;
    private AlarmManager alarmManager;
    private Intent intent;
    private RingtoneAdapter ringtoneAdapter;
    private RingtoneUtils ringtoneUtils;

    private IAlarmManager iAlarmManager = new IAlarmManager() {
        @Override
        public void IAddItemAlarmManager(Alarm alarm) {
//            Calendar calendar = AlarmConverter.toCalendar(alarm.getTime());
            Calendar calendar = Calendar.getInstance();
            intent.putExtra("extra", true);
            intent.putExtra("ringtone", alarm.getRingtoneTitle());
            intent.putExtra("idAlarm", alarm.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    MainActivity.this, alarm.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 3000, pendingIntent);
//            // check repeat
//            if (!alarm.getRepeat()) {
//                Log.d("Alarm repeat", alarm.getRepeat() + "");
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//            } else {
//                if (alarm.getTitleRepeat().compareTo("Every day") == 0) {
//                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
//                    return;
//                }
//                String weekTmp[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
//                Map<String, Boolean> map = new HashMap<String, Boolean>();
//                for (int i = 0; i < 7; i++) {
//                    map.put(weekTmp[i], false);
//                }
//
//                if (alarm.getTitleRepeat().compareTo("Every weekday") == 0) {
//                    map.put("Mon", true);
//                    map.put("Tue", true);
//                    map.put("Wed", true);
//                    map.put("Thu", true);
//                    map.put("Fri", true);
//                } else {
//                    String arrOfStr[] = alarm.getTitleRepeat().split(" ");
//                    for (String i : arrOfStr) {
//                        map.put(i, true);
//                    }
//                }
//
//                Set set = map.keySet();
//                for (Object key : set) {
//                    Log.d("Alarm repeat", key + " " + map.get(key));
//                    if (map.get(key) == true) {
//                        Calendar calendarTmp = compareWeek(calendar, (String) key);
//                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarTmp.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pendingIntent);
//                    }
//                }
//            }
        }

        @Override
        public void ICancelItemAlarmManager(Alarm alarm) {
            alarmManager.cancel(PendingIntent.getBroadcast(
                    MainActivity.this, alarm.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
            ));
        }
    };

    private Calendar compareWeek(Calendar calendar, String key) {
        DateFormat formatter = new SimpleDateFormat("EEE", Locale.getDefault());
        String dateCurrent = formatter.format(calendar.getTime());

        while (dateCurrent.compareTo(key) != 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dateCurrent = formatter.format(calendar.getTime());
        }

        return calendar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init
        layoutRoot = (LinearLayout) findViewById(R.id.layout);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        intent = new Intent(MainActivity.this, AlarmReceiver.class);
        ringtoneUtils = new RingtoneUtils(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // recycler view
        rcvAlarm = (RecyclerView) findViewById(R.id.recycleViewAlarm);
        alarmAdapter = new AlarmAdapter(this, iAlarmManager, ringtoneUtils);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvAlarm.setAdapter(alarmAdapter);
        rcvAlarm.setLayoutManager(linearLayoutManager);

        // swipe to delete
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvAlarm.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rcvAlarm);

        // dialog add
        ImageView imgAdd = (ImageView) findViewById(R.id.imageViewAddAlarm);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAdd();
            }
        });
    }

    private Ringtone ringtoneSelected;

    private void showDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_alarm);
        dialog.show();

        Alarm alarmSelected = new Alarm();

        // spinner ringtone
        Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerRingtone);
        ringtoneAdapter = new RingtoneAdapter(getApplicationContext(), R.layout.item_ringtone, ringtoneUtils.getListRingtone());
        spinner.setAdapter(ringtoneAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String notificationTitle = ringtoneAdapter.getItem(i).getTitle();
                String notificationUri = ringtoneUtils.getUriRingtoneFromTitle(notificationTitle);
                if (ringtoneSelected != null && ringtoneSelected.isPlaying()) {
                    ringtoneSelected.stop();
                }
                ringtoneSelected = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(notificationUri));
                ringtoneSelected.play();
                Log.d("Ringtone", notificationTitle + "::: " + notificationUri);
                alarmSelected.setRingtoneTitle(notificationTitle);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // set repeat
        TextView tvTitleRepeat = (TextView) dialog.findViewById(R.id.titleRepeat);
        RelativeLayout layoutRepeat = dialog.findViewById(R.id.layoutRepeat);
        layoutRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddRepeat(tvTitleRepeat, alarmSelected);
            }
        });


        // button save and cancel
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        Button btnSave = dialog.findViewById(R.id.buttonSave);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ringtoneSelected.isPlaying()) {
                    ringtoneSelected.stop();
                }
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
                Calendar calendarSelected = Calendar.getInstance();
                calendarSelected.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendarSelected.set(Calendar.MINUTE, timePicker.getMinute());
                calendarSelected.set(Calendar.SECOND, 0);
                calendarSelected.set(Calendar.MILLISECOND, 0);
                alarmSelected.setTime(AlarmConverter.fromCalendar(calendarSelected));
                alarmSelected.setTimeOfDay(calendarSelected.get(Calendar.HOUR_OF_DAY) * 60 + calendarSelected.get(Calendar.MINUTE));

                alarmAdapter.addItem(alarmSelected);
                alarmAdapter.notifyDataSetChanged();

                if (ringtoneSelected.isPlaying()) {
                    ringtoneSelected.stop();
                }
                dialog.dismiss();
            }
        });
    }

    private void showDialogAddRepeat(TextView tvTitleRepeat, Alarm alarmSelected) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_repeat);
        dialog.show();

        List<Boolean> week = new ArrayList<>(Arrays.asList(new Boolean[7]));
        Collections.fill(week, Boolean.FALSE);

        CheckBox cbMonday = dialog.findViewById(R.id.checkBoxMonday);
        CheckBox cbTuesday = dialog.findViewById(R.id.checkBoxTuesday);
        CheckBox cbWednesday = dialog.findViewById(R.id.checkBoxWednesday);
        CheckBox cbThursday = dialog.findViewById(R.id.checkBoxThursday);
        CheckBox cbFriday = dialog.findViewById(R.id.checkBoxFriday);
        CheckBox cbSaturday = dialog.findViewById(R.id.checkBoxSaturday);
        CheckBox cbSunday = dialog.findViewById(R.id.checkBoxSunday);

        // init checkbox
        String titleTmp = alarmSelected.getTitleRepeat();
        if (titleTmp.compareTo("Never") != 0) {

            if (titleTmp.compareTo("Every day") == 0) {
                cbMonday.setChecked(true);
                cbTuesday.setChecked(true);
                cbWednesday.setChecked(true);
                cbThursday.setChecked(true);
                cbFriday.setChecked(true);
                cbSaturday.setChecked(true);
                cbSunday.setChecked(true);
            } else if (titleTmp.compareTo("Every weekday") == 0) {
                cbMonday.setChecked(true);
                cbTuesday.setChecked(true);
                cbWednesday.setChecked(true);
                cbThursday.setChecked(true);
                cbFriday.setChecked(true);
            } else {
                String arrOfStr[] = titleTmp.split(" ");
                String weekTmp[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                Map<String, Boolean> map = new HashMap<String, Boolean>();
                for (int i = 0; i < 7; i++) {
                    map.put(weekTmp[i], false);
                }
                for (int i = 0; i < arrOfStr.length; i++) {
                    map.put(arrOfStr[i], true);
                }
                cbMonday.setChecked(map.get("Mon"));
                cbTuesday.setChecked(map.get("Tue"));
                cbWednesday.setChecked(map.get("Wed"));
                cbThursday.setChecked(map.get("Thu"));
                cbFriday.setChecked(map.get("Fri"));
                cbSaturday.setChecked(map.get("Sat"));
                cbSunday.setChecked(map.get("Sun"));
            }
        }

        ImageView btnBack = (ImageView) dialog.findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ImageView btnSave = (ImageView) dialog.findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                week.set(0, cbMonday.isChecked());
                week.set(1, cbTuesday.isChecked());
                week.set(2, cbWednesday.isChecked());
                week.set(3, cbThursday.isChecked());
                week.set(4, cbFriday.isChecked());
                week.set(5, cbSaturday.isChecked());
                week.set(6, cbSunday.isChecked());

                String title = AlarmConverter.toTitlefromListWeekRepeat(week);
                tvTitleRepeat.setText(title);
                alarmSelected.setTitleRepeat(title);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof AlarmAdapter.AlarmViewHolder) {
            int indexDelete = viewHolder.getAdapterPosition();
            Alarm alarmDelete = alarmAdapter.getItem(indexDelete);

            alarmAdapter.deleteItem(alarmDelete);
            alarmAdapter.notifyDataSetChanged();

            Snackbar snackbar = Snackbar.make(layoutRoot, "", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alarmAdapter.undoItem(alarmDelete, indexDelete);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}