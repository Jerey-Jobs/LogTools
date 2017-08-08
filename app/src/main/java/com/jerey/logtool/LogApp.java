package com.jerey.logtool;

import android.app.Application;
import android.util.Log;

import com.jerey.loglib.LogTools;

/**
 * Created by xiamin on 5/17/17.
 */

public class LogApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogTools.getSettings()
                .setGlobalLogTag(LogApp.class.getSimpleName())
                .setBorderEnable(false);
    }
}
