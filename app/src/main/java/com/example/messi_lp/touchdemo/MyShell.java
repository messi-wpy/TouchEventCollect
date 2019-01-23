package com.example.messi_lp.touchdemo;

import android.support.annotation.NonNull;

public class MyShell {
    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    public void getroot(){
        execCommand(COMMAND_SU);


    }

    public void execCommand(@NonNull String command){
        Process process=null;



    }
}
