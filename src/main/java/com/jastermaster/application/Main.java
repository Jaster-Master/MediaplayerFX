package com.jastermaster.application;

import javafx.application.Platform;

import java.net.URL;

public class Main {

    public static volatile Program runningProgram;

    public static void startApplication(String[] params) {
        Program program = new Program();
        program.startProgram(params);
    }

    public static String getResourceString(String fileName) {
        URL urlPath = getResourceURL(fileName);
        if (urlPath == null) return "";
        return urlPath.toString();
    }

    public static URL getResourceURL(String fileName) {
        return Main.class.getResource(fileName);
    }

    public static void closeApplication() {
        Platform.exit();
    }

    public static void main(String[] args) {
        startApplication(args);
    }
}
