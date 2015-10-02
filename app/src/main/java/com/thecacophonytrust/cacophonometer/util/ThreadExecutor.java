package com.thecacophonytrust.cacophonometer.util;

import java.util.concurrent.Executor;

public class ThreadExecutor implements Executor {
    private static final String LOG_TAG = "ThreadExecutor.java";

    public void execute(Runnable r){
        new Thread(r).start();
    }
}
