package com.bee_eater.dltodlde;

import static com.bee_eater.dltodlde.Constants.INTENT_EXTRA_FILEPATH;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class FileObserverServiceRestarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("Broadcast Listened", "Service tried to stop");

        try {
            Intent restartIntent = new Intent();
            restartIntent.putExtra(INTENT_EXTRA_FILEPATH, "");
            restartIntent.setClass(context, FileObserverService.class);
            context.startService(restartIntent);
            Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Log.d("Broadcast Listened", e.toString());
        }

    }
}