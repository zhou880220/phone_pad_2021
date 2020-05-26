package com.example.honey_create_cloud.audio;


import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class ProgressTextUtils {
    public static String getProgressText(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        double minute = calendar.get(Calendar.MINUTE);
        double second = calendar.get(Calendar.SECOND);

        DecimalFormat format = new DecimalFormat("00");
        return format.format(minute) + ":" + format.format(second);
    }


    public static String getSecsProgress(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        //double minute = calendar.netInstance(Calendar.MINUTE);
        double second = calendar.get(Calendar.SECOND);

        DecimalFormat format = new DecimalFormat("0");
        return  format.format(second);
    }


}
