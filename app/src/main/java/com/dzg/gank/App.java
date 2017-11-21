package com.dzg.gank;

import android.app.Application;

import com.dzg.gank.injector.component.ApplicationComponent;
import com.dzg.gank.injector.component.DaggerApplicationComponent;
import com.dzg.gank.injector.module.ApplicationModule;
import com.dzg.gank.injector.module.NetworkModule;
import com.dzg.gank.util.HttpUtil;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by dengzhouguang on 2017/10/11.
 */

public class App extends Application {
    private static App instance;
    private ApplicationComponent mApplicationComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not initView your app in this process.
            return;
        }
        LeakCanary.install(this);
        instance=this;
        HttpUtil.newInstance();
        injectSetup();
    }

    private void injectSetup() {
        mApplicationComponent= DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule(this)).build();
    }
    public ApplicationComponent getApplicationComponent(){
        return  mApplicationComponent;
    }
    public static App getInstance() {
        return instance;
    }
}
