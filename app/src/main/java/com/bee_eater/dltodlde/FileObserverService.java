package com.bee_eater.dltodlde;

import static com.bee_eater.dltodlde.Constants.DIVINGLOG_FILEPATH;
import static com.bee_eater.dltodlde.Constants.INTENT_EXTRA_FILEPATH;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

import java.io.File;


public class FileObserverService extends Service {
    private FileObserver mFileObserver;
    private Boolean muteEvents = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            //String folder = intent.getStringExtra(INTENT_EXTRA_FILEPATH);
            mFileObserver = new FileObserver(new File(DIVINGLOG_FILEPATH)) {
                @Override
                public void onEvent(int event, String path) {
                    if (event == 0x8) { // CLOSE_WRITE --> New file written and closed
                        if (!muteEvents) { // Event 8 always appears twice --> ignore second!
                            // If an event happens we can do stuff inside here
                            // for example we can send a broadcast message with the event-id
                            Log.d("FILEOBSERVER_EVENT", "Event with id " + Integer.toHexString(event) + " happened"); // event identifies the occured Event in hex
                            muteEvents = true;
                            ShowNotification();
                        } else {
                            muteEvents = false;
                        }
                    }
                }
                };
            mFileObserver.startWatching(); // The FileObserver starts watching
            return Service.START_STICKY;
        } catch (Exception e) {
            stopSelf();
            Log.e("FILEOBSERVER", e.toString());
            return Service.START_STICKY;
        }
    }

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private static final String NOTIFICATION_CHANNEL_STR = "default";
    private void ShowNotification(){

        Intent intent = new Intent(this, MainActivity.class)
                .putExtra(INTENT_EXTRA_FILEPATH, "");

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new
                NotificationChannel(NOTIFICATION_CHANNEL_ID, "Logbook updates" , importance) ;
        notificationChannel.enableLights(true) ;
        notificationChannel.setLightColor(Color.RED) ;
        notificationChannel.enableVibration(true) ;
        notificationChannel.setVibrationPattern(new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;

        Notification n  = new Notification.Builder(this, NOTIFICATION_CHANNEL_STR)
                .setContentTitle("Logbook was updated!")
                .setContentText("(tap here to upload)")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                //.addAction(action)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel) ;

        assert notificationManager != null;
        notificationManager.notify((int)System.currentTimeMillis(), n);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    // We have to create a broadcast that restarts when our app is closed
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}