package com.bee_eater.dltodlde;

import static com.bee_eater.dltodlde.Constants.*;

import android.content.Intent;
import android.net.Uri;

public class DLtoDLdeHelper {

    public static Uri getUriFromIntent(Intent intent){
        String file = intent.getStringExtra(INTENT_EXTRA_FILEPATH);
        return Uri.parse(file);
    }

}
