package com.odibetika;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;



public class SureTips extends MultiDexApplication {

    private static SureTips mInstance;
    private static Context context;

    @Override
    public void onCreate() {
        mInstance = this;
        SureTips.context = getApplicationContext();
        super.onCreate();

        //AudienceNetworkAds.initialize(this);

       /* MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });*/
    }

    public static Context getAppContext() {
        return SureTips.context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
