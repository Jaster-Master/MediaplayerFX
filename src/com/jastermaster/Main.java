package com.jastermaster;

import javafx.application.Platform;

public class Main {

    public static void startApplication(String[] params) {
        Program program = new Program();
        program.startProgram(params);
    }

    public static void closeApplication() {
        Platform.exit();
    }

    public static void main(String[] args) {
        startApplication(args);
    }
}
