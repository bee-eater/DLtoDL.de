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
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();

        try {
            File file = (File) intent.getBundleExtra(INTENT_EXTRA_FILEPATH).getSerializable(INTENT_EXTRA_FILEPATH);
            Intent restartIntent = new Intent();
            Bundle fbundle = new Bundle();
            fbundle.putSerializable(INTENT_EXTRA_FILEPATH, file);
            restartIntent.putExtra(INTENT_EXTRA_FILEPATH, fbundle);
            restartIntent.setClass(context, FileObserverService.class);
            context.startService(restartIntent);
        } catch (Exception e){

        }

    }
}