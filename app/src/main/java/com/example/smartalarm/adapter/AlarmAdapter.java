package com.example.smartalarm.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartalarm.R;
import com.example.smartalarm.database.AlarmConverter;
import com.example.smartalarm.database.AlarmDatabase;
import com.example.smartalarm.model.Alarm;
import com.example.smartalarm.my_interface.IAlarmManager;
import com.example.smartalarm.util.RingtoneUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private Context context;
    private List<Alarm> listAlarm;
    private IAlarmManager iAlarmManager;
    private RingtoneUtils ringtoneUtils;

    public AlarmAdapter(Context context, IAlarmManager iAlarmManager, RingtoneUtils ringtoneUtils) {
        this.context = context;
        listAlarm = AlarmDatabase.getInstance(context).alarmDAO().getListAlarm();
        this.iAlarmManager = iAlarmManager;
        this.ringtoneUtils = ringtoneUtils;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);

        return new AlarmViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = (Alarm) listAlarm.get(position);
        if (alarm == null) return;

        Calendar calendar = AlarmConverter.toCalendar(alarm.getTime());
        SimpleDateFormat formatHour = new SimpleDateFormat("hh:mm aa");
        String hour = formatHour.format(calendar.getTime());
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            hour = "00" + hour.substring(2, 8);
        }
        holder.tvHour.setText(hour);
        holder.tvRepeat.setText(alarm.getTitleRepeat());

        // update isEnabled
        holder.imgStatus.setImageResource(alarm.isEnabled() ? R.drawable.clock_enable : R.drawable.clock_disable);
        holder.imgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarm.setEnabled(!alarm.isEnabled());
                holder.imgStatus.setImageResource(alarm.isEnabled() ? R.drawable.clock_enable : R.drawable.clock_disable);
                updateIsEnabled(alarm, alarm.isEnabled());
            }
        });

        // update item
        holder.layoutForeground.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                showDialogUpdate(alarm, position);
                Log.d("Alarm id", alarm.getId() + " " + alarm.isEnabled());
            }
        });
    }

    private void updateIsEnabled(Alarm alarm, boolean enabled) {
        int position = findPositionFromIdAlarm(alarm.getId());
        listAlarm.get(position).setEnabled(enabled);

        AlarmDatabase.getInstance(context).alarmDAO().updateAlarm(alarm);

        if (enabled) {
            iAlarmManager.IAddItemAlarmManager(alarm);
        } else {
            iAlarmManager.ICancelItemAlarmManager(alarm);
        }
    }

    private Ringtone ringtoneSelected;
    private RingtoneAdapter ringtoneAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showDialogUpdate(Alarm alarm, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_alarm);
        dialog.show();

        // init timePicker
        TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
        Calendar calendar = AlarmConverter.toCalendar(alarm.getTime());
        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(calendar.get(Calendar.MINUTE));

        // init spinner
        Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerRingtone);
        ringtoneAdapter = new RingtoneAdapter(context, R.layout.item_ringtone, ringtoneUtils.getListRingtone());
        spinner.setAdapter(ringtoneAdapter);

        spinner.setSelection(ringtoneUtils.getPosition(alarm.getRingtoneTitle()));
        Log.d("Ringtone", "Position " + ringtoneUtils.getPosition(alarm.getRingtoneTitle()));
        Log.d("Ringtone", "Title " + alarm.getRingtoneTitle());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String notificationTitle = ringtoneAdapter.getItem(i).getTitle();
                String notificationUri = ringtoneUtils.getUriRingtoneFromTitle(notificationTitle);
                if (ringtoneSelected != null && ringtoneSelected.isPlaying()) {
                    ringtoneSelected.stop();
                }
                ringtoneSelected = RingtoneManager.getRingtone(context, Uri.parse(notificationUri));
                ringtoneSelected.play();
                Log.d("Ringtone", notificationTitle + "::: " + notificationUri);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // init repeat
        TextView tvTitleRepeat = (TextView) dialog.findViewById(R.id.titleRepeat);
        tvTitleRepeat.setText(alarm.getTitleRepeat());
        RelativeLayout layoutRepeat = dialog.findViewById(R.id.layoutRepeat);
        layoutRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddRepeat(tvTitleRepeat, alarm);
            }
        });

        // button save, cancel
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
                Calendar calendarSelected = Calendar.getInstance();
                calendarSelected.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendarSelected.set(Calendar.MINUTE, timePicker.getMinute());
                calendarSelected.set(Calendar.SECOND, 0);
                calendarSelected.set(Calendar.MILLISECOND, 0);

                alarm.setTime(AlarmConverter.fromCalendar(calendarSelected));
                alarm.setTimeOfDay(calendarSelected.get(Calendar.HOUR_OF_DAY) * 60 + calendarSelected.get(Calendar.MINUTE));
                alarm.setEnabled(true);
                updateItem(alarm);
                notifyDataSetChanged();

                if (ringtoneSelected.isPlaying()) {
                    ringtoneSelected.stop();
                }
                dialog.dismiss();
            }
        });
    }

    private void showDialogAddRepeat(TextView tvTitleRepeat, Alarm alarm) {
        Dialog dialog = new Dialog(context);
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
        String titleTmp = alarm.getTitleRepeat();
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
                alarm.setTitleRepeat(title);
                dialog.dismiss();
            }
        });
    }

    public void undoItem(Alarm alarm, int position) {
        addItem(alarm);
        notifyItemInserted(position);
    }

    public Alarm getItem(int position) {
        return listAlarm.get(position);
    }


    @Override
    public int getItemCount() {
        if (listAlarm == null) return 0;
        return listAlarm.size();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView tvHour;
        TextView tvRepeat;
        ImageView imgStatus;
        public RelativeLayout layoutForeground;
        public RelativeLayout layoutBackground;
        Spinner spinner;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);

            tvHour = (TextView) itemView.findViewById(R.id.textViewHour);
            tvRepeat = (TextView) itemView.findViewById(R.id.textViewRepeat);
            imgStatus = (ImageView) itemView.findViewById(R.id.imageViewStatus);
            layoutForeground = (RelativeLayout) itemView.findViewById(R.id.layoutForeground);
            layoutBackground = (RelativeLayout) itemView.findViewById(R.id.layoutBackground);
            spinner = (Spinner) itemView.findViewById(R.id.spinnerRingtone);
        }
    }

    private int findPositionFromIdAlarm(int id) {
        int position = -1;
        for (int i = 0; i < listAlarm.size(); i++) {
            if (listAlarm.get(i).getId() == id) {
                position = i;
            }
        }
        return position;
    }

    public void addItem(Alarm alarmNew) {
        alarmNew = compareTimeCurrent(alarmNew);

        Calendar cal = AlarmConverter.toCalendar(alarmNew.getTime());
        Log.d("Time Alarm Add", cal.get(Calendar.HOUR) + "-" + cal.get(Calendar.MINUTE) + "-" + cal.get(Calendar.SECOND) + "-" + cal.get(Calendar.MILLISECOND));

        // kiem tra time ton tai chua
        List<Alarm> listAlarmCheck = AlarmDatabase.getInstance(context).alarmDAO().checkAlarmFromTimeOfDay(alarmNew.getTimeOfDay());
        if (listAlarmCheck.isEmpty()) {
            // chua ton tai -> add room, list, alarm manager
            AlarmDatabase.getInstance(context).alarmDAO().insertAlarm(alarmNew);

            listAlarmCheck = AlarmDatabase.getInstance(context).alarmDAO().checkAlarmFromTimeOfDay(alarmNew.getTimeOfDay());
            alarmNew.setId(listAlarmCheck.get(0).getId());

            int position = 0;
            for (int i = 0; i < listAlarm.size(); i++) {
                if (listAlarm.get(i).getTimeOfDay() < alarmNew.getTimeOfDay()) {
                    position = i + 1;
                } else {
                    break;
                }
            }
            listAlarm.add(position, alarmNew);

            iAlarmManager.IAddItemAlarmManager(alarmNew);
        } else {
            // da ton tai Time -> cap nhat room + list + alarm manager
            int position = findPositionFromIdAlarm(listAlarmCheck.get(0).getId());
            Alarm alarmSelected = listAlarm.get(position);
            alarmSelected.setRingtoneTitle(alarmNew.getRingtoneTitle());
            alarmSelected.setTitleRepeat(alarmNew.getTitleRepeat());
            alarmSelected.setEnabled(true);
            listAlarm.set(position, alarmSelected);

            AlarmDatabase.getInstance(context).alarmDAO().updateAlarm(alarmSelected);

            iAlarmManager.IAddItemAlarmManager(listAlarm.get(position));
        }
    }

    public void deleteItem(Alarm alarm) {
        AlarmDatabase.getInstance(context).alarmDAO().deleteAlarm(alarm);

        int position = findPositionFromIdAlarm(alarm.getId());
        if (position != -1) {
            listAlarm.remove(position);
            notifyItemRemoved(position);
        }

        iAlarmManager.ICancelItemAlarmManager(alarm);
    }

    private Alarm compareTimeCurrent(Alarm alarmNew) {
        // kiem tra -> tang ngay
        Calendar calendarNew = AlarmConverter.toCalendar(alarmNew.getTime());
        Date dateNew = calendarNew.getTime();
        Calendar calendarCurrent = Calendar.getInstance();
        Date dateCurrent = calendarCurrent.getTime();
        if (dateCurrent.compareTo(dateNew) > 0) {
            calendarNew.add(Calendar.DAY_OF_MONTH, 1);
            alarmNew.setTime(AlarmConverter.fromCalendar(calendarNew));
        }

        return alarmNew;
    }

    public void updateItem(Alarm alarm) {
        alarm = compareTimeCurrent(alarm);

        // Kiem tra time: da ton tai -> xoa alarm cu
        List<Alarm> listAlarmCheck = AlarmDatabase.getInstance(context).alarmDAO().checkAlarmFromTimeOfDay(alarm.getTimeOfDay());
        if (!listAlarmCheck.isEmpty()) {
            deleteItem(listAlarmCheck.get(0));
        }

        // update
        AlarmDatabase.getInstance(context).alarmDAO().updateAlarm(alarm);

        int position = findPositionFromIdAlarm(alarm.getId());
        listAlarm.remove(position);

        int positionNew = 0;
        for (int i = 0; i < listAlarm.size(); i++) {
            if (listAlarm.get(i).getTimeOfDay() <= alarm.getTimeOfDay()) {
                positionNew = i + 1;
            } else {
                break;
            }
        }
        listAlarm.add(positionNew, alarm);
        notifyItemChanged(positionNew);


        iAlarmManager.IAddItemAlarmManager(alarm);
    }
}
