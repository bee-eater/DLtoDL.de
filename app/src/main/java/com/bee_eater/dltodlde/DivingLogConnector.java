package com.bee_eater.dltodlde;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DivingLogConnector {

    private static final String DB_NAME = "/AppDB.sql";
    private ProgressBar progressBar = null;
    private MainActivity main;
    public List<DLDive> DLDives;
    private static String tmpDBFile;
    private DiveLogFileDoneListener listener;

    /**
     * Construtor for connector instance
     * @param Activity pass main activity in order to be able to access GUI elements etc...
     */
    public DivingLogConnector(MainActivity Activity){
        main = Activity;
        // Get path for temporary db in app specific files
        tmpDBFile = main.getBaseContext().getFilesDir().toString() + DB_NAME;
        // Set callback function :: passed is the class object which implements the interface and
        // therefor has the overriding callback function --> "this" in main class
        this.listener = (DiveLogFileDoneListener)Activity;
    }

    /**
     * Set progress bar element in order to show progress on GUI
     * @param pg ProgressBar instance
     */
    public void setProgressBar(ProgressBar pg){
        this.progressBar = pg;
    }

    public void LoadDiveLogFile(Uri file){
        copyDataBase(file);
        LoadDiveLogFile ld = new LoadDiveLogFile();
        ld.start();
    }

    private class LoadDiveLogFile extends Thread {
        Handler handler = new Handler();

        @Override
        public void run() {
            try {
                if(progressBar != null)
                    main.runOnUiThread(new Runnable() {@Override public void run() {progressBar.setVisibility(View.VISIBLE);}});

                DLDives = DivingLog_LoadDiveLogFile(tmpDBFile);

                if(progressBar != null)
                    main.runOnUiThread(new Runnable() {@Override public void run() {progressBar.setVisibility(View.INVISIBLE);}});
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                public void run() {
                    onLoadDiveLogFileDone();
                }
            });
        }
    }

    private void onLoadDiveLogFileDone (){
        deleteDataBase();
        listener.LoadDiveLogFileDone();
    }

    /**
     * Copy database to app specific files
     * @param file Uri to file from intent
     */
    public void copyDataBase(Uri file) {

        Log.i("Database", "New database is being copied to device!");
        byte[] buffer = new byte[1024];
        OutputStream fsOutput;
        int length;
        try
        {
            InputStream fsInput = main.getBaseContext().getContentResolver().openInputStream(file);
            fsOutput = new FileOutputStream(tmpDBFile);
            while((length = fsInput.read(buffer)) > 0)
            {
                fsOutput.write(buffer, 0, length);
            }
            fsOutput.close();
            fsOutput.flush();
            fsInput.close();
            Log.i("Database", "New database has been copied to device!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Delete temporary database file from app storage
     */
    private void deleteDataBase(){
        File fDelete = new File(tmpDBFile);
        if (fDelete.exists()) {
            if (fDelete.delete()) {
                Log.d("Database", "Database deleted from app storage!");
            } else {
                Log.d("Database", "File not deleted!");
            }
        }
    }

    private List<DLDive> DivingLog_LoadDiveLogFile(String file){

        // Init empty dive list
        List<DLDive> DLDives = new ArrayList<DLDive>();

        // Load database from file and query all dives from logbook
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file, null, 0);
        String query = "SELECT * FROM Logbook";
        Cursor res = db.rawQuery(query,null);

        int results = res.getCount();
        int currCnt = 0;

        // Parse results
        res.moveToFirst();
        if (res.moveToFirst()) {
            while (!res.isAfterLast()) {

                // Create temp dive
                DLDive dive = new DLDive();

                // Loop over all columns, get value and add to dive
                for (int i = 0; i < res.getColumnCount(); i++) {
                    // Get column name
                    String colName = res.getColumnName(i);
                    // Get column type in order to use correct function
                    int colType = res.getType(i);
                    // Get column value based on type
                    Object colValue;
                    switch(colType){
                        case Cursor.FIELD_TYPE_NULL: colValue = null; break;// NULL value, ignore
                        case Cursor.FIELD_TYPE_INTEGER: colValue = res.getInt(i); break;
                        case Cursor.FIELD_TYPE_FLOAT: colValue = res.getDouble(i); break;
                        case Cursor.FIELD_TYPE_STRING: colValue = res.getString(i); break;
                        default:
                            Log.d("COLUMN", "Found type :: " + Integer.toString(colType));
                            colValue = "ERR";
                    }

                    // Add value to dive by member name
                    try {
                        dive.setMemberByName(colName, colValue);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        Log.d("QUERY", e.toString());
                    }
                    Log.d("COLUMN",colName + " :: " + Integer.toString(colType) + " :: " + String.valueOf(colValue));
                }
                // add dives to dive list
                DLDives.add(dive);
                res.moveToNext();
                currCnt++;
                if(progressBar != null){
                    progressBar.setProgress((currCnt * 100/ results));
                }
            }
        }
        res.close();
        db.close();

        return DLDives;
    }

}
