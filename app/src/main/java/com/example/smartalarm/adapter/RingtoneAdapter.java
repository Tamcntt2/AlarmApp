package com.example.smartalarm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smartalarm.R;
import com.example.smartalarm.model.RingtoneAlarm;

import java.util.List;

public class RingtoneAdapter extends ArrayAdapter<RingtoneAlarm> {
    public RingtoneAdapter(@NonNull Context context, int resource, @NonNull List<RingtoneAlarm> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ringtone, parent, false);
        TextView tvSelected = convertView.findViewById(R.id.textViewRingtone);

        RingtoneAlarm ringtone = this.getItem(position);
        if(ringtone != null) {
            tvSelected.setText(ringtone.getTitle());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ringtone, parent, false);
        TextView tvRingtone = convertView.findViewById(R.id.textViewRingtone);

        RingtoneAlarm ringtone = this.getItem(position);
        if(ringtone != null) {
            tvRingtone.setText(ringtone.getTitle());
        }

        return convertView;
    }


}
