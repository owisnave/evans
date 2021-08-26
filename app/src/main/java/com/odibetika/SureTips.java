package com.odibetika;

import android.app.Application;

import com.facebook.ads.AudienceNetworkAds;

public class SureTips extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AudienceNetworkAds.initialize(this);
    }
}
