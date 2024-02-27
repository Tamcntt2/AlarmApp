package com.example.smartalarm.util;

import java.text.SimpleDateFormat;

public class TimeUtil {
//    String[] DAY_OF_WEEKS = {"Sunday", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy"};
    public static String convertTime(long time) {
        SimpleDateFormat formatDay = new SimpleDateFormat("dd-MM");
        String dateStr = formatDay.format(time);

        return dateStr;
    }
}
