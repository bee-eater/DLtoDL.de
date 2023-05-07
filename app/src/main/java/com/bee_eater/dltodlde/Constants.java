package com.bee_eater.dltodlde;

import android.content.Intent;
import android.net.Uri;

public class Constants {
    public static final int LOGLEVEL = 4;
    public static final boolean ERROR = LOGLEVEL > 0;
    public static final boolean WARN = LOGLEVEL > 1;
    public static final boolean INFO = LOGLEVEL > 2;
    public static final boolean DEBUG = LOGLEVEL > 3;
    public static final boolean VERBOSE = LOGLEVEL > 4;

    public static final String INTENT_EXTRA_FILEPATH = "FPITENT";

}