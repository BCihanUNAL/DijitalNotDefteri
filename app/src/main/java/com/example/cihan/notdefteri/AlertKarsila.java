package com.example.cihan.notdefteri;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by cihan on 15.05.2019.
 */

public class AlertKarsila extends BroadcastReceiver {
    private static final String TAG = "AlertKarsila";
    private final int NOTIFICATION_ID = 1;
    private GeofencingClient geofencingClient = null;
    public static int kac = 0x7FFFFFFF;
    public static PendingIntent pIntent = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        String isim = intent.getStringExtra("isim");
        Log.d(TAG, "onReceive: ALARM");
        int select = intent.getIntExtra("select",-1),drawable=R.drawable.ic_launcher_background;
        String s = "";
        if(select == 0){
            s = "Saat = " + intent.getStringExtra("saat");
            drawable = R.drawable.ic_takvim;
            kac--;
        }
        if(select == 1){
            s = "Belirlediğiniz noktaya ulaştınız";
            drawable = R.drawable.ic_ev;
            geofencingClient = LocationServices.getGeofencingClient(context);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationChannel.DEFAULT_CHANNEL_ID)
                .setSmallIcon(drawable)
                .setContentTitle(isim + " başlıklı notta kurmuş olduğunuz bildirim")
                .setContentText(s)

                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,builder.build());
        Log.d(TAG, "onReceive: "+kac);
        if(kac == -1 && pIntent != null){
            ((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).cancel(pIntent);
            pIntent = null;
        }
    }
}

