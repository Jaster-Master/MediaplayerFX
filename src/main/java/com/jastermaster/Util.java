package com.jastermaster;

import javafx.geometry.*;
import javafx.stage.*;

import java.time.*;
import java.time.format.*;

public class Util {
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("mm:ss");

    public static String getStringFromMillis(double millis) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli((long) millis), ZoneId.systemDefault());
        return date.format(timeFormatter);
    }

    public static String getStringFromDate(LocalDate date) {
        return date.format(dateFormatter);
    }

    public static LocalDateTime getTimeFromString(String date) {
        return LocalDateTime.from(timeFormatter.parse(date));
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
