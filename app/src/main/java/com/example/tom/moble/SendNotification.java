package com.example.tom.moble;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
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
    String contextTrigger = "";

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    @Override
    protected void onHandleIntent(Intent intent) {
        db = new DatabaseHandler(this);
        db.getEntry(5).getPortuguese();

        contextTrigger = "";

        int contextCue = getNotificationWord(true);
        sendNotification(db.getEntry(contextCue).getPortuguese() + " = " + db.getEntry(contextCue).getEnglish());

        DatabaseEntry updatedEntry = db.getEntry(contextCue);
        updatedEntry.setNotification(contextTrigger);
        db.updateEntry(updatedEntry);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();



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
                        .setContentTitle("MobLe")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        mBuilder.setContentIntent(contentIntent);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        mNotificationManager.notify(m, mBuilder.build());
    }



    public int getNotificationWord(boolean context){
        int notificationWord = 1000;

        if (context) {
            //Get wifi names
            boolean wifiWasDisabled = false;
            wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled() == false) {
                wifiWasDisabled = true;
                wifi.setWifiEnabled(true);
            }
            results = wifi.getScanResults();
            size = results.size();

            if (wifi.isWifiEnabled() == true && wifiWasDisabled == true) {
                wifi.setWifiEnabled(false);
            }
            //Get context cue
            Calendar c = Calendar.getInstance();
            int timeCue = c.get(Calendar.HOUR_OF_DAY);

            String[] cueArray = {"ns internet", "bus internet", "huis internet", "supermarkt", "edurom"};
            int locationCue = 1000;
            for (int i = 0; i < size; i++) {
                for (int z = 0; i < cueArray.length; z++){
                    if (results.get(i).SSID.contains(cueArray[z])) {
                        contextTrigger = results.get(i).SSID;
                        locationCue = z;
                    }
                }
            }

            Random rgen = new Random();
            int lowRange;
            int highRange;
            if (locationCue != 1000){
                switch(locationCue){
                    case 1:
                        //lowRange = rgen.nextInt((max - min) + 1) + min;
                        lowRange = rgen.nextInt((10 - 1) + 1) + 1;
                        highRange = rgen.nextInt((20 - 10) + 1) + 10;
                        break;
                    default:
                        lowRange = 1;
                        highRange = 50;
                }
                notificationWord = findWordWithinRange(lowRange, highRange);
            }

            if (notificationWord == 1000){
                contextTrigger = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
                if (timeCue > 8 && timeCue < 12){
                    notificationWord = findWordWithinRange(10, 50);          // AANVULLEN ALS WOORDEN ER ZIJN..
                }else if(timeCue > 12 && timeCue < 17){

                }else if (timeCue > 17 && timeCue < 20){

                }else if(timeCue > 20 && timeCue < 24){

                }
            }

            if (notificationWord == 1000){
                contextTrigger = "Random";
                notificationWord = findWordWithinRange(3, 300);
            }

        }


        return notificationWord;
    }


    public int findWordWithinRange(int low, int high){
        int returnString = 1000;
            for (int i = low; i <= high; i++){
                if (db.getEntry(i).getNotification() == null){  //KLOPT DIT? -- MOET VERANDEREN NAAR ==!!!!!
                    returnString = i;
                    break;
                }
            }
        return returnString;
    }


}

