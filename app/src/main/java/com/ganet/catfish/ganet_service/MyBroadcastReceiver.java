package com.ganet.catfish.ganet_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "GaNetService";
    public MyBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d( TAG, "onReceive:" + intent );

        if ( intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) ) {
            Log.d( TAG, "Start Service");
            context.startService(new Intent(context, GaNetService.class));
        }
        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
