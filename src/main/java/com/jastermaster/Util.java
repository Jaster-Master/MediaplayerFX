package com.jastermaster;

import javafx.geometry.*;
import javafx.stage.*;

import java.text.*;
import java.util.*;

public class Util {
    public static String getTimeFromDouble(double millis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return timeFormat.format(new Date((long) millis));
    }

    public static String getTimeFromDate(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return timeFormat.format(date);
    }

    public static long getLongFromDateString(String date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            return timeFormat.parse(date).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static void centerWindow(Window window) {
        window.addEventHandler(WindowEvent.WINDOW_SHOWN, windowEvent -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Window currentWindow = ((Window) windowEvent.getSource());
            currentWindow.setX((screenBounds.getWidth() - currentWindow.getWidth()) / 2);
            currentWindow.setY((screenBounds.getHeight() - currentWindow.getHeight()) / 3);
        });
    }
}
