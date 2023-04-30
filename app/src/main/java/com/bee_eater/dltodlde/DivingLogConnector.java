package com.bee_eater.dltodlde;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.bee_eater.dltodlde.Constants.*;

public class DivingLogConnector {

    private static final String DB_NAME = "/AppDB.sql";
    private ProgressBar progressBar = null;
    private TextView progressNr = null;
    private TextView progressAbs = null;
    private MainActivity main;
    public List<DivingLogDive> DLDives;
    private static String tmpDBFile;
    private DivingLogFileDoneListener listener;

    /**
     * Construtor for connector instance
     * @param Activity pass main activity in order to be able to access GUI elements etc...
     */
    public DivingLogConnector(MainActivity Activity){
        main = Activity;
        // Get path for temporary db in app specific files
        tmpDBFile = main.getBaseContext().getFilesDir().toString() + DB_NAME;
        // Set callback function :: passed is the class object which implements the interface and
        // therefor has the overriding callback function --> pass "this" in main class, cast here
        this.listener = Activity;
    }

    /**
     * Set progress bar element in order to show progress on GUI
     * @param pg ProgressBar instance
     */
    public void setGuiConnector(ProgressBar pg, TextView nr, TextView progress){
        this.progressBar = pg;
        this.progressAbs = progress;
        this.progressNr = nr;
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
                setGuiVisibility(View.VISIBLE);
                DLDives = DivingLog_LoadDiveLogFile(tmpDBFile);
                setGuiVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.post(DivingLogConnector.this::onLoadDiveLogFileDone);
        }
    }

    private void setGuiVisibility(int vis){
        if(progressBar != null) {
            main.runOnUiThread(() -> {
                LinearLayout lay = (LinearLayout) progressBar.getParent();
                lay.setVisibility(vis);
            });
        }
    }

    private void onLoadDiveLogFileDone (){
        deleteDataBase();
        listener.LoadDivingLogFileDone();
    }

    /**
     * Copy database to app specific files
     * @param file Uri to file from intent
     */
    public void copyDataBase(Uri file) {

        if (VERBOSE) Log.v("DLCONN", "New database is being copied to device!");
        byte[] buffer = new byte[1024];
        OutputStream fsOutput;
        int length;
        try
        {
            InputStream fsInput = main.getBaseContext().getContentResolver().openInputStream(file);
            fsOutput = Files.newOutputStream(Paths.get(tmpDBFile));
            while((length = fsInput.read(buffer)) > 0)
            {
                fsOutput.write(buffer, 0, length);
            }
            fsOutput.close();
            fsOutput.flush();
            fsInput.close();
            if (VERBOSE) Log.v("DLCONN", "New database has been copied to device!");
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
                if (VERBOSE) Log.v("DLCONN", "Database deleted from app storage!");
            } else {
                if (VERBOSE) Log.v("DLCONN", "File not deleted!");
            }
        }
    }

    private List<DivingLogDive> DivingLog_LoadDiveLogFile(String file){

        // Init empty dive list
        List<DivingLogDive> DLDives = new ArrayList<>();

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
                DivingLogDive dive = new DivingLogDive();

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
                            if (VERBOSE) Log.v("DLCONN", "Found column type :: " + Integer.toString(colType));
                            colValue = "ERR";
                    }

                    // Add value to dive by member name
                    try {
                        dive.setMemberByName(colName, colValue);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        if (ERROR) Log.e("DLCONN", "Error adding value to dive object: " + e);
                    }
                    if (VERBOSE) Log.v("DLCONN",colName + " :: " + Integer.toString(colType) + " :: " + String.valueOf(colValue));
                }
                // Convert date and entry time to LocalDateTime variable (for later comparison to DiveLogs.de list)
                try {
                    String tmpDT = dive.Divedate + " " + dive.Entrytime;
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    dive.DiveDateDT = LocalDateTime.parse(tmpDT, dtf);
                    // Only add dive if dt conversion was successful
                    DLDives.add(dive);
                } catch (Exception e) {
                    if (ERROR) Log.e("DLCONN", "Exception getting divetime: " + e);
                    Toast.makeText(main, e.toString(), Toast.LENGTH_LONG).show();
                }
                res.moveToNext();
                currCnt++;
                int progress = (currCnt * 100/ results);
                setGuiProcess(progress, currCnt, results);

            }
        }
        res.close();
        db.close();

        return DLDives;
    }

    private void setGuiProcess(int progress, int done, int total){
        main.runOnUiThread(() -> {
            if(progressBar != null){
                progressBar.setProgress(progress);
            }
            if(progressNr != null){
                progressNr.setText("(" + done + " of " + total + ")");
            }
            if(progressAbs != null){
                progressAbs.setText(progress + " %");
            }
        });
    }

}
