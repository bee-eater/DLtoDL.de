package com.bee_eater.dltodlde;

import static com.bee_eater.dltodlde.Constants.INTENT_EXTRA_FILEPATH;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
            //String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            if ((intent.hasExtra(INTENT_EXTRA_FILEPATH))) {// we store the path of directory inside the intent that starts the service
                File file = (File) intent.getBundleExtra(INTENT_EXTRA_FILEPATH).getSerializable(INTENT_EXTRA_FILEPATH);
                // Creating the FileObserver with the string /storage/emulated/0/Diving/Logbook.sql works
                // Creating it with the file from the opening intent --> not working. Missing permissions??
                mFileObserver = new FileObserver("/storage/emulated/0/Diving/") {
                    @Override
                    public void onEvent(int event, String path) {
                        if (event == 0x8) { // CLOSE_WRITE --> New file written and closed
                            if (!muteEvents) { // Event 8 always appears twice --> ignore second!
                                // If an event happens we can do stuff inside here
                                // for example we can send a broadcast message with the event-id
                                Log.d("FILEOBSERVER_EVENT", "Event with id " + Integer.toHexString(event) + " happened"); // event identifies the occured Event in hex
                                muteEvents = true;
                                ShowNotification(file);
                            } else {
                                muteEvents = false;
                            }
                        }
                    }
                };
            }
            mFileObserver.startWatching(); // The FileObserver starts watching
            return Service.START_STICKY;
        } catch (Exception e) {
            stopSelf();
            return Service.START_STICKY;
        }
    }

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private static final String NOTIFICATION_CHANNEL_STR = "default";
    private void ShowNotification(File file){

        Bundle fbundle = new Bundle();
        fbundle.putSerializable(INTENT_EXTRA_FILEPATH, file);

        Intent intent = new Intent(this, MainActivity.class)
                .putExtra(INTENT_EXTRA_FILEPATH, fbundle);

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new
                NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME" , importance) ;
        notificationChannel.enableLights(true) ;
        notificationChannel.setLightColor(Color.RED) ;
        notificationChannel.enableVibration(true) ;
        notificationChannel.setVibrationPattern(new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;

        // build notification
        // the addAction re-use the same intent to keep the example short
        //Intent uploadIntent = new Intent(this, NotificationButtonReceiver.class);
        //PendingIntent uploadPendingIntent = PendingIntent.getBroadcast(this, 0, uploadIntent, PendingIntent.FLAG_IMMUTABLE);
        //Notification.Action action = new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_stat_accessible_forward), "Upload!", uploadPendingIntent).build();

        Notification n  = new Notification.Builder(this, NOTIFICATION_CHANNEL_STR)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
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