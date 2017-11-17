package com.dzg.gank;

import android.app.Application;

import com.dzg.gank.util.HttpUtil;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by dengzhouguang on 2017/10/11.
 */

public class App extends Application {
    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        instance=this;
        HttpUtil.newInstance();
    }

    public static App getInstance() {
        return instance;
    }
}
