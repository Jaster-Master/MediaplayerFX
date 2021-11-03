package com.jastermaster;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    public static String getTimeFromDouble(double millis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return timeFormat.format(new Date((long) millis));
    }

    public static String getTimeFromDate(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return timeFormat.format(date);
    }
}
