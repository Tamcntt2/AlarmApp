package com.example.smartalarm.database;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmConverter {
    @TypeConverter
    public static Calendar toCalendar(long longDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(longDate));
        return calendar;
    }

    @TypeConverter
    public static long fromCalendar(Calendar calendar) {
        return calendar.getTime().getTime();
    }

    @TypeConverter
    public static String toTitlefromListWeekRepeat(List<Boolean> list) {
        if (list.get(0) && list.get(1) && list.get(2) && list.get(3) && list.get(4)
                && list.get(5) && list.get(6)) {
            return "Every day";
        }

        if (list.get(0) && list.get(1) && list.get(2) && list.get(3) && list.get(4)) {
            return "Every weekday";
        }

        String week[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String title = "";
        for (int i = 0; i < 7; i++) {
            if (list.get(i)) {
                title += week[i] + " ";
            }
        }

        if (title.length() == 0) {
            return "Never";
        }
        return title;
    }

    @TypeConverter
    public static List<Boolean> toListWeekRepeatFromTitle(String title) {
        List<Boolean> list = new ArrayList<>(Arrays.asList(new Boolean[7]));
        Collections.fill(list, Boolean.FALSE);

        if (title.compareTo("Never") == 0) {
            return list;
        }

        if (title.compareTo("Every day") == 0) {
            for (int i = 0; i < 7; i++) {
                list.set(i, true);
            }
            return list;
        }

        if (title.compareTo("Every weekday") == 0) {
            for (int i = 0; i < 5; i++) {
                list.set(i, true);
            }
            return list;
        }

        String week[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < 7; i++) {
            map.put(week[i], i);
        }

        String arrOfStr[] = title.split(" ");
        for(int i=0; i<arrOfStr.length; i++) {
            list.set(map.get(arrOfStr[i]), true);
        }

        return list;
    }

}
