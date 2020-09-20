package com.subra.taskman.utils;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

public class TimeCount {

    /*public void Counter(final ShowCounter myCallback) {
        int count = 0;
        for(;;) {
            try {
                Thread.sleep(1000);
                myCallback.onCallback(count);
                count ++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public interface ShowCounter {
        void onCallback(int value);
    }*/

    private Timer timer;
    private TimerTask timerTask;
    private int count = 0;

    private static TimeCount mInstance;

    public static TimeCount getInstance() {
        if (mInstance == null) {
            mInstance = new TimeCount();
        }
        return mInstance;
    }

    public void getCounter(final ShowCounter myCallback) {
        final Handler handler = new Handler();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            //count = 0;
                            myCallback.onCallback(count);
                            count ++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer = new Timer(); //This is new
        timer.schedule(timerTask, 0, 1000); // execute in every 5sec
    }

    public void stopCounter() {
        if (timer != null) {
            timerTask.cancel();
            timer.cancel();
        }
    }

    public interface ShowCounter {
        void onCallback(int value);
    }

}
