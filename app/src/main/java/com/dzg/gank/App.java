package com.dzg.gank;

import android.app.Application;

import com.dzg.gank.util.HttpUtil;

/**
 * Created by dengzhouguang on 2017/10/11.
 */

public class App extends Application {
    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        HttpUtil.newInstance();
    }

    public static App getInstance() {
        return instance;
    }
}
