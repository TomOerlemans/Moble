package com.example.tom.moble;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    Calendar calender;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    int beginRange;
    int endRange;
    int notificationCounter;
    String contextCue;
    int dbWordLocation;

    @Override
    protected void onHandleIntent(Intent intent) {
        calender = Calendar.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int currentHour = calender.get(Calendar.HOUR_OF_DAY);
        int startTime = preferences.getInt("startTimeNotifications",  9);
        if(currentHour >= startTime &&  currentHour <= (startTime + 12)) {
            notificationCounter = preferences.getInt("NotificationCounter", 0);
            String changeFrequency = preferences.getString("Change Frequency Date", null);
            int maxNotifications = 12;
            int amountOfWords = 2;
            boolean notificationWord = true;
            int day = 1;

            if (changeFrequency != null) {
                if (tryParse(changeFrequency) != null) {
                    if (new Date().after(tryParse(changeFrequency)) == true) {
                        maxNotifications = 4;
                        amountOfWords = 3;
                        notificationWord = false;
                        day = 2;
                    }
                }
            }

            if (notificationCounter < (maxNotifications * day)) {

                int currentMinute = calender.get(Calendar.MINUTE);
                int previousScan = (preferences.getInt("hourNotification", 0) * 60) + preferences.getInt("minuteNotification", 0);
                int currentTime = (currentHour * 60) + currentMinute;

                if (currentTime >= previousScan + 60) {
                    db = new DatabaseHandler(this);
                    getNotificationWord(notificationWord);
                    if (contextCue.equals("Unintentional random")) {
                        if (currentTime >= previousScan + 120) {
                            for (int i = 0; i < amountOfWords; i ++) {
                                if(i > 0){
                                    getNotificationWord(notificationWord);
                                }
                                sendNotification(db.getEntry(dbWordLocation).getPortuguese() + " = " + db.getEntry(dbWordLocation).getEnglish());
                                DatabaseEntry updatedEntry = db.getEntry(dbWordLocation);
                                updatedEntry.setNotification(contextCue);
                                db.updateEntry(updatedEntry);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("hourNotification", currentHour);
                                editor.putInt("minuteNotification", currentMinute);
                                editor.commit();
                            }
                        }
                    } else {
                        for(int i = 0; i < amountOfWords; i++) {
                            if(i > 0){
                                getNotificationWord(notificationWord);
                            }
                            sendNotification(db.getEntry(dbWordLocation).getPortuguese() + " = " + db.getEntry(dbWordLocation).getEnglish());
                            DatabaseEntry updatedEntry = db.getEntry(dbWordLocation);
                            updatedEntry.setNotification(contextCue);
                            db.updateEntry(updatedEntry);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("hourNotification", currentHour);
                            editor.putInt("minuteNotification", currentMinute);
                            editor.commit();
                        }
                    }
                }
            }
        }
        // Release the wake lock provided by the BroadcastReceiver.
        AlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }

    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MenuActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.book)
                        .setContentTitle("MobLe")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if(am.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(2000);
        }

        mBuilder.setContentIntent(contentIntent);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        mNotificationManager.notify(m, mBuilder.build());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int counter = preferences.getInt("NotificationCounter",  0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("NotificationCounter", (counter + 1));
        editor.commit();

    }



    public void getNotificationWord(boolean context){

        if (context) {
            Log.v("one", "one");
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
            //Try to find a location cue and a word that matches it
            boolean foundLocationCue = getLocationCue();
            boolean foundLocationWord = false;
            boolean foundTimeWord = false;
            if(foundLocationCue == true){
                Log.v("two", "two");
                foundLocationWord = findWordWithinRange(beginRange, endRange);
            }

            if(foundLocationWord == false){
                foundTimeWord = getTimeCue();
            }

            if(foundTimeWord == false && foundLocationWord == false){
                contextCue = "Unintentional random";
                findWordWithinRange(1,205);
            }

        }else{
            contextCue = "Intentional random";
            findWordWithinRange(1,205);
        }


    }

    public boolean findWordWithinRange(int low, int high){
        Log.v("highandlow", Integer.toString(low) + "" + Integer.toString(high));
        for (int i = low; i <= high; i++){
            if (db.getEntry(i).getNotification() == null){
                dbWordLocation = i;
                Log.v("selected location", Integer.toString(i));
                return true;
            }
        }
        return false;
    }



    public boolean getTimeCue(){
        int timeCue = calender.get(Calendar.HOUR_OF_DAY);
        if (timeCue > 8 && timeCue < 12){
            if(findWordWithinRange(194, 195)){
                contextCue = "morning";
                return true;
            }
        }else if(timeCue > 12 && timeCue < 17){
            if(findWordWithinRange(196, 197)){
                contextCue = "afternoon";
                return true;
            }
        }else if (timeCue > 17 && timeCue < 20){
            if(findWordWithinRange(204, 205)){
                contextCue = "dinner time";
                return true;
            }
        }else if(timeCue > 20 && timeCue < 24){
            if(findWordWithinRange(198, 203)){
                contextCue = "evening";
                return true;
            }
        }
        return false;
    }


    public boolean getLocationCue(){
        //Check whether we're at home
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String homeWifi = preferences.getString("Home Wifi", "Not found");
        if(!homeWifi.equals("Not found")){
            homeWifi = homeWifi.toLowerCase();
            for (int i = 0; i < size; i++) {
                if (results.get(i).SSID.toLowerCase().contains(homeWifi)) {
                    contextCue = "home";
                    beginRange = 6;
                    endRange = 32;
                    return true;
                }

            }
        }
        //Check whether we're using public transport
        String[] publictransportArray = new String[]{"trein", "ret"};
        for (int i = 0; i < size; i++) {
            for (int z = 0; z < publictransportArray.length; z++) {
                if (results.get(i).SSID.toLowerCase().contains(publictransportArray[z])) {
                    contextCue = "public transport";
                    beginRange = 33;
                    endRange = 80;
                    return true;
                }
            }
        }
        //Check whether we're at a supermarket
        String[] supermarktArray = new String[]{"albert heijn", "jumbo", "lidl", "aldi", "coop", "spar", "plus", "hoogvliet", "vomar", "dirk"};
        for (int i = 0; i < size; i++) {
            for (int z = 0; z < supermarktArray.length; z++){
                if (results.get(i).SSID.toLowerCase().contains(supermarktArray[z])) {
                    contextCue = "supermarkt";
                    beginRange = 81;
                    endRange = 156;
                    return true;
                }
            }
        }
        //Check whether we're at the university/library
        for (int i = 0; i < size; i++) {
                if (results.get(i).SSID.toLowerCase().contains("eduroam")) {
                    contextCue = "library";
                    Log.v("four", "four");
                    beginRange = 157;
                    endRange = 193;
                    return true;
                }
        }

        //Nothing is found
        return false;
    }

    public static Date tryParse(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
        try {
            return sdf.parse(text);
        } catch (ParseException e) {
            return null;
        }
    }


}

