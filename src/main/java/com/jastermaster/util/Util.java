package com.jastermaster.util;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Util {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("mm:ss");

    public static String getFileExtensionFromFile(File file) {
        // https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
        String fileName = file.getName();
        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex == -1) return null;
        return fileName.substring(extensionIndex + 1);
    }

    public static boolean isSupportedFormat(String fileFormat) {
        return fileFormat.equalsIgnoreCase("mp3") || fileFormat.equalsIgnoreCase("wav") || fileFormat.equalsIgnoreCase("aac") || fileFormat.equalsIgnoreCase("aiff");
    }

    public static String getStringFromMillis(double millis) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli((long) millis), ZoneId.systemDefault());
        return date.format(TIME_FORMATTER);
    }

    public static String getStringFromDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static LocalDateTime getTimeFromString(String date) {
        return LocalDateTime.from(TIME_FORMATTER.parse(date));
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
