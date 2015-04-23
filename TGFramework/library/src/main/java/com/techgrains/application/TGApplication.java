package com.techgrains.application;

import android.app.Application;

import com.techgrains.session.TGSession;

/**
 * Application class for TGFramework library, initiate application level objects.
 *
 * Created on 22/04/15.
 */
public class TGApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TGSession.instance().setApplicationContext(this.getApplicationContext());
    }
}
