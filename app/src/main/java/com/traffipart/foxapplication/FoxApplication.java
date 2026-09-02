package com.traffipart.foxapplication;

import android.app.Application;
import com.traffipart.foxapplication.di.AppContainer;
import com.traffipart.foxapplication.di.DefaultAppContainer;

public final class FoxApplication extends Application {

    private AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();

        appContainer = new DefaultAppContainer(this);
    }

    public AppContainer appContainer() {
        return appContainer;
    }
}
