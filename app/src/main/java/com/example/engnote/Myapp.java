package com.example.engnote;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
public class Myapp extends Application {
    private static Myapp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    // 获取ApplicationContext
    public static Context getMyApplication() {
        return instance;
    }
}


