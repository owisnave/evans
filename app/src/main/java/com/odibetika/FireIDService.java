package com.odibetika;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

//import com.google.firebase.iid.FirebaseInstanceIdService;


public class FireIDService extends FirebaseMessagingService {
    /*@Override
    public void onTokenRefresh() {

    }
}*/
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN", s);
    }
}