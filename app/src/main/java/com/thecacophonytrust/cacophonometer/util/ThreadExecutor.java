package com.thecacophonytrust.cacophonometer.util;

import android.util.Log;

import com.thecacophonytrust.cacophonometer.http.UploadRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class ThreadExecutor implements Executor {
    private static final String LOG_TAG = "ThreadExecutor.java";

    private Map<Thread, Runnable> threads = new HashMap<>();

    public void execute(Runnable r){
        Thread thread = new Thread(r);
        thread.start();
        threads.put(thread, r);
    }

    public int activeThreads(){
        int result = 0;
        ArrayList<Thread> finishedThreads = new ArrayList<>();
        for (Thread thread : threads.keySet()){
            if (thread.isAlive()){
                result+=1;
            } else {
                finishedThreads.add(thread);
            }
        }
        for (Thread thread : finishedThreads) {
            threads.remove(thread);
        }
        return result;
    }
}
