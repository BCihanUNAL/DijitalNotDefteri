package com.example.cihan.notdefteri;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Calendar;
import java.util.List;

/**
 * Created by cihan on 26.05.2019.
 */

public class GeofenceTransition extends IntentService {
    private static final String TAG = "GeofenceTransition";

    public GeofenceTransition(){
        super("MyTransition");
        Log.d(TAG, "GeofenceTransition: created");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Event Error");
            return;
        }


        int geofenceTransition = geofencingEvent.getGeofenceTransition();


        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            Intent myIntent = new Intent(this,AlertKarsila.class);
            myIntent.putExtra("isim",intent.getStringExtra("isim"));
            myIntent.putExtra("select",1);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,234324235,myIntent,0);
            ((AlarmManager)getSystemService(ALARM_SERVICE)).setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),pendingIntent);
        }
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            GoogleMapActivity.remove();
        }
    }
}
