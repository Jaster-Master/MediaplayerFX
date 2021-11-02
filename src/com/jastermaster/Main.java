package com.jastermaster;

public class Main {

    public static void startApplication(String[] params) {
        Program program = new Program();
        program.startProgram(params);
    }

    public static void main(String[] args) {
        startApplication(args);
    }
}
