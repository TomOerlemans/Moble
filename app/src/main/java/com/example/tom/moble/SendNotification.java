package com.example.tom.moble;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Tom on 6/2/2016.
 */
public class SendNotification extends IntentService {

    public SendNotification(){
        super("SchedulingService");
    }

    List<ScanResult> results;
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    WifiManager wifi;
    int size = 0;
    String ITEM_KEY = "key";
    DatabaseHandler db;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    @Override
    protected void onHandleIntent(Intent intent) {
        db = new DatabaseHandler(this);
        db.getEntry(5).getPortuguese();

        String text = "No network found";

        if (results != null){
            text = results.get(1).SSID;
        }

        sendNotification(text);
        Log.i("Send notification: ", "done");

       getWiFiNames();

        // Release the wake lock provided by the BroadcastReceiver.
        AlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }

    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.book)
                        .setContentTitle(db.getEntry(5).getPortuguese())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        mNotificationManager.notify(m, mBuilder.build());
    }

    public void getWiFiNames(){
        boolean wifiWasDisabled = false;
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            wifiWasDisabled = true;
            wifi.setWifiEnabled(true);
        }



        results = wifi.getScanResults();
        size = results.size();

        for(int i = 0; i < size; i++){
            Log.v("Wifi names: ", results.get(i).SSID);
        }

        if (wifi.isWifiEnabled() == true && wifiWasDisabled == true) {
            wifi.setWifiEnabled(false);
        }

    }


}

