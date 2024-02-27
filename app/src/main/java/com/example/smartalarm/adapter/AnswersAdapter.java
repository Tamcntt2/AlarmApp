package com.example.smartalarm.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartalarm.R;
import com.example.smartalarm.activity.MainActivity;
import com.example.smartalarm.database.AlarmConverter;
import com.example.smartalarm.database.AlarmDatabase;
import com.example.smartalarm.model.Alarm;
import com.example.smartalarm.my_interface.IAlarmManager;
import com.example.smartalarm.service.AlarmService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswersViewHolder> {

    private Context context;
    private List<String> listAnswers;
    private String rightAnswer;
    private int idAlarm;

    public AnswersAdapter(Context context, List<String> listAnswers, String rightAnswer, int idAlarm) {
        this.context = context;
        this.listAnswers = listAnswers;
        this.rightAnswer = rightAnswer;
        this.idAlarm = idAlarm;
    }

    @NonNull
    @Override
    public AnswersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer_question, parent, false);

        return new AnswersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswersViewHolder holder, int position) {
        String answer = listAnswers.get(position);
        if (answer == null) return;

        holder.btnAnswer.setText(answer);

        holder.btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answer.equals(rightAnswer)) {
                    // turn off
                    Intent myIntent = new Intent(context, AlarmService.class);
                    context.startService(myIntent);
                    myIntent.putExtra("extra", false);
                    Toast.makeText(context, "Tắt báo thức thành công!", Toast.LENGTH_SHORT).show();

                    if (idAlarm != -1) {
                        Alarm alarm = AlarmDatabase.getInstance(context).alarmDAO().checkAlarmFromId(idAlarm).get(0);
                        if (!alarm.getRepeat()) {
                            alarm.setEnabled(false);
                            AlarmDatabase.getInstance(context).alarmDAO().updateAlarm(alarm);
                        } else {    // set repeating alarm
                            setRepeatingAlarm(alarm);
                        }
                    }
                    System.exit(0);
                } else {
                    Toast.makeText(context, "Đáp án sai!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setRepeatingAlarm(Alarm alarm) {
        if (alarm.getTitleRepeat().compareTo("Every day") == 0) {
            Calendar cal = AlarmConverter.toCalendar(alarm.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
            // ... add next alarm manager
            return;
        }
        String weekTmp[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        for (int i = 0; i < 7; i++) {
            map.put(weekTmp[i], false);
        }

        if (alarm.getTitleRepeat().compareTo("Every weekday") == 0) {
            map.put("Mon", true);
            map.put("Tue", true);
            map.put("Wed", true);
            map.put("Thu", true);
            map.put("Fri", true);
        } else {
            String arrOfStr[] = alarm.getTitleRepeat().split(" ");
            for (String i : arrOfStr) {
                map.put(i, true);
            }
        }

        Set set = map.keySet();
        for (Object key : set) {
            Log.d("Alarm repeat", key + " " + map.get(key));
            if (map.get(key) == true) {

            }
        }
    }

    @Override
    public int getItemCount() {
        return listAnswers.size();
    }

    public class AnswersViewHolder extends RecyclerView.ViewHolder {
        Button btnAnswer;

        public AnswersViewHolder(@NonNull View itemView) {
            super(itemView);
            btnAnswer = (Button) itemView.findViewById(R.id.buttonAnswer);
        }
    }
}
