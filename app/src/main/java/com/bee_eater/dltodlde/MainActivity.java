package com.bee_eater.dltodlde;

import static com.bee_eater.dltodlde.Constants.DEBUG;
import static com.bee_eater.dltodlde.Constants.ERROR;
import static com.bee_eater.dltodlde.Constants.VERBOSE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bee_eater.dltodlde.DiveLogsApi.DiveLogsDive;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class MainActivity extends AppCompatActivity implements DivingLogFileDoneListener {

    private DiveLogsApi DLApi = new DiveLogsApi();
    private DivingLogConnector DLC;

    private ArrayList<DiveLogsDive> diveLogsDives;

    private ListView divesList;
    private ArrayAdapter<DivingLogDive> divesListAdapter;


    /**
     * Base onCreate action, called when App is first opened
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Allow network traffic in main thread (don't care about blocking gui...)
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        // Init stuff
        initStuff();
        setupGUI_Main();

        // When App is opened, check if it opened a file by checking intent type (contains the mime file type)
        Intent intent = getIntent();
        checkIntentForSQLFile(intent);

    }


    /**
     * Override onNetIntent to get correct intent when resuming the activity
     * (wasn't closed and reopened by clicking .sql file in file explorer)
     * @param intent The new intent that was started for the activity.
     */
    @Override
    protected void onNewIntent (Intent intent){
        super.onNewIntent(intent);
        initStuff();
        checkIntentForSQLFile(intent);
    }


    /**
     * If content changes (setContentView()) this listener will be executed
     * and the matching function for the corresponding content is called.
     */
    @Override
    public void onContentChanged(){

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        // Load matching GUI data based on loaded content
        if(viewGroup.getId() == R.id.activity_main) {
            setupGUI_Main();
        } else if (viewGroup.getId() == R.id.view_diveSelection) {
            setupGUI_DiveSelect();
        }

    }


    //====================================================================================================
    //====================================================================================================
    // APP FUNCTIONS
    //====================================================================================================
    //====================================================================================================
    /**
     * Function that set's up the GUI (get saved data etc.)
     */
    private void setupGUI_Main(){

        ArrayList<String> up = LoadDLLoginData();
        EditText tinDLUsername = findViewById(R.id.tinDLUsername);
        EditText pwdDLPassword = findViewById(R.id.pwdDLPassword);
        if (up.size() >= 2){
            tinDLUsername.setText(up.get(0));
            pwdDLPassword.setText(up.get(1));
            if(up.size() == 3){
                LoadUserImage(up.get(2));
            }
        }
        pwdDLPassword.setOnKeyListener((view, keyCode, keyEvent) -> {
            //If the keyEvent is a key-down event on the "enter" button
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                on_btnDLLogin_clicked(getCurrentFocus());
                return true;
            }
            return false;
        });

    }


    /**
     * Function that set's up the list showing the dives to select for upload
     */
    private void setupGUI_DiveSelect(){
        divesList = findViewById(R.id.lstDivesList);
        divesListAdapter = new DiveSelectionListAdapter(this, DLC.DLDives);
        divesList.setAdapter(divesListAdapter);
    }


    /**
     * Initialize some things when app is opened with a new intent
     */
    private void initStuff() {
        // Make sure we're on base page
        setContentView(R.layout.activity_main);
        // Create new diving log connector instance
        DLC = new DivingLogConnector(this);
        // Assign progress bar from GUI so progress can be shown by connector instance
        DLC.setGuiConnector(findViewById(R.id.prbDLCFileLoading), findViewById(R.id.txtProgressNr), findViewById(R.id.txtProgressPercentage));
    }


    /**
     * Function that loads login data from shared preferences and puts it into the input fields of the app
     */
    private ArrayList<String> LoadDLLoginData(){
        ArrayList<String> up = new ArrayList<>();
        SharedPreferences sp1 = this.getSharedPreferences("DLLogin", MODE_PRIVATE);
        if(sp1 != null) {
            String user = sp1.getString("user", null);
            if (user != null){
                up.add(user);
            }
            String pass = sp1.getString("pass", null);
            if (pass != null) {
                up.add(pass);
            }
            // Only add image if user / pass was there...
            if (up.size() == 2) {
                String img = sp1.getString("imgurl", null);
                if (img != null) {
                    up.add(img);
                }
            }
        }
        return up;
    }


    /**
     * Function saves login data to shared preferences
     * @param user Username
     * @param pass Password
     */
    private void SaveDLLoginData(String user, String pass, String img){
        SharedPreferences sp=getSharedPreferences("DLLogin", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("user", user);
        Ed.putString("pass", pass);
        Ed.putString("imgurl", img);
        Ed.apply();
    }


    /**
     * Load user image to ImageView
     */
    private void LoadUserImage(String URL){
        try {
            InputStream in = new java.net.URL(URL).openStream();
            Bitmap bmp = BitmapFactory.decodeStream(in);
            ImageView imgUser = findViewById(R.id.imgUser);
            imgUser.setImageBitmap(bmp);
        } catch (Exception e){
            if (ERROR) Log.e("MAIN", e.toString());
        }
    }


    /**
     * Function that checks the open / resume intent for a sql file and calls the loading function
     * @param intent Intent from the calling instance
     */
    private void checkIntentForSQLFile(Intent intent){
        String intentType = intent.getType();
        if (Objects.equals(intentType, "application/x-sql")){

            // Copy file to app specific memory to avoid permission issues
            Uri file = intent.getData();
            try {
                DLC.LoadDiveLogFile(file);
            } catch (Exception e) {
                if (ERROR) Log.e("MAIN", e.toString());
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }

        }
    }


    /**
     * Event listener through interface implementation that is called
     * when the DivingLog sql file is successfully loaded.
     */
    @Override
    public void LoadDivingLogFileDone() {
        if (DLC.DLDives != null) {
            if (DLC.DLDives.size() > 0) {
                Toast.makeText(this, "Opened file: " + DLC.DLDives.size(), Toast.LENGTH_LONG).show();
                for (DivingLogDive d : DLC.DLDives) {
                    try {
                        JSONObject json = new JSONObject(d.toString());
                        if (VERBOSE) Log.v("MAIN", json.toString(4));
                    } catch (JSONException e) {
                        if (ERROR) Log.e("MAIN", e.toString());
                        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                // Load dives from DiveLogs.de
                diveLogsLoadDives();
                // Compare DiveLogs list to DivingLog list
                compareDiveLists();
                // Load list content, contentChanged event sets all required adapters etc...
                setContentView(R.layout.view_diveselection);
                // Jump to end of list
                divesList.setSelection(divesListAdapter.getCount() - 1);
                // TODO: Create conversion from DivingLog to DLD format
            }
        }
    }


    /**
     * Compare DivesLogs.de list to DivingLog list and create diff info
     */
    public void compareDiveLists(){

        for (DivingLogDive log: DLC.DLDives){
            int cnt = 0;

            for (DiveLogsDive web: diveLogsDives){
                if(log.DiveDateDT.isEqual(web.DiveDateDT)){
                    log.DiveLogsIndex = cnt;
                    log.ListInfoText = "Dive already uploaded!";
                    break;
                }
                cnt++;
            }

            // No dive was found
            if(log.DiveLogsIndex == -1){
                // Dive is not older then 7 days...
                if(log.DiveDateDT.isBefore(LocalDateTime.now()) && !log.DiveDateDT.isBefore(LocalDateTime.now().minusDays( 7 ))) {
                    // Preselect
                    log.isSelected = true;
                    log.ListInfoText = "Not uploaded yet!";
                } else {
                    log.ListInfoText = "Not uploaded yet! (> 7 days)";
                }
            }
        }

    }


    /**
     * Load list of dives from DiveLogs.de
     */
    public void diveLogsLoadDives(){

        EditText tinDLUsername = findViewById(R.id.tinDLUsername);
        EditText pwdDLPassword = findViewById(R.id.pwdDLPassword);
        String user = tinDLUsername.getText().toString();
        String pass = pwdDLPassword.getText().toString();
        diveLogsDives = new ArrayList<>();
        String res = DLApi.GetDives(user, pass, diveLogsDives);
        if(Objects.equals(res, "-1")){
            // Login unsuccessful -> Toast
            Toast.makeText(this,"Login error! Please check your credentials!", Toast.LENGTH_LONG).show();
        } else if (Objects.equals(res, "0")) {
            // Everything fine --> Compare DiveLogs to DivingLog dives and show list
            if (DEBUG) Log.d("MAIN", "diveLogsLoadDives(): " + diveLogsDives.size());
        } else {
            // Exception trying to get dives from DiveLogs.de --> Log and toast error
            if (ERROR) Log.e("MAIN", "Exception in DLApi.GetDives(): " + res);
            Toast.makeText(this, "Exception in DLApi.GetDives()", Toast.LENGTH_LONG).show();
        }
    }


    //====================================================================================================
    //====================================================================================================
    // EVENT HANDLERS
    //====================================================================================================
    //====================================================================================================
    /**
     * Event: btnDLLogin was clicked on GUI
     * @param v View calling the event
     */
    public void on_btnDLLogin_clicked(View v){

        EditText tinDLUsername = findViewById(R.id.tinDLUsername);
        EditText pwdDLPassword = findViewById(R.id.pwdDLPassword);
        String user = tinDLUsername.getText().toString();
        String pass = pwdDLPassword.getText().toString();

        if(Objects.equals(user, "")){
            SaveDLLoginData("","", "");
            ImageView imgUser = findViewById(R.id.imgUser);
            imgUser.setImageResource(android.R.color.transparent);
            tinDLUsername.setText("");
            pwdDLPassword.setText("");
            Toast.makeText(this,"Login data cleared!", Toast.LENGTH_LONG).show();
        } else {
            boolean res = DLApi.Login(user, pass);
            if(res) {
                Toast.makeText(this,"Login successfull! Data saved!", Toast.LENGTH_LONG).show();
                SaveDLLoginData(user, pass, DLApi.UserImageURL);
                if (DEBUG) Log.d("MAIN", "Found user image: " + DLApi.UserImageURL);
                try {
                    LoadUserImage(DLApi.UserImageURL);
                } catch (Exception e) {
                    if(ERROR) Log.e("MAIN", "Error loading image: " + e);
                }

            } else {
                Toast.makeText(this,"Login error! Please check your credentials!", Toast.LENGTH_LONG).show();
            }
        }

    }


    /**
     * Event: btnDiveListCancel was clicked on dive list GUI
     * @param v View calling the event
     */
    public void on_btnDiveListCancel_clicked(View v){
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Aborted!", Toast.LENGTH_SHORT).show();
    }


    /**
     * Event: btnDiveListUpload was clicked on dive list GUI
     * @param v View calling the event
     */
    public void on_btnDiveListUpload_clicked(View v){

        // Prepare by initializing tmpDir
        String tmpDir = "currUpload";
        File xmldir = getDir(tmpDir,Context.MODE_PRIVATE);
        Boolean ret = CleanTempDir(xmldir,false);
        if(!ret) {
            if (DEBUG) Log.d("MAIN", "Couldn't make dir: " + xmldir.getPath());
        } else {
            // Write all files to internal store
            Integer cnt = 0;
            for (DivingLogDive d : DLC.DLDives) {
                if (d.isSelected) {
                    String xml = d.toDLD();
                    if (VERBOSE) Log.d("MAIN", xml);

                    // Get file with index and write xml content
                    File xmlf = new File(xmldir, cnt + ".xml");
                    try {
                        try (FileOutputStream stream = new FileOutputStream(xmlf)) {
                            stream.write(xml.getBytes());
                        }
                    } catch (Exception e) {
                        if (ERROR) Log.e("MAIN", "XML write exception: " + e);
                    }
                    cnt++;
                }
            }

            // zip all files in folder
            String xmlfname = xmldir.toPath() + "/upload.zip";
            try {
                FileOutputStream f = new FileOutputStream(xmlfname);
                ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(f));
                byte[] buf = new byte[2048];

                // Loop through xmldir and add all .xml files to zip
                for (File child : Objects.requireNonNull(xmldir.listFiles())) {
                    if (child.getPath().endsWith(".xml")) {
                        // Get file content
                        FileInputStream fi;
                        fi = new FileInputStream(child.getPath());
                        // Remove path from filename
                        String fname = child.getPath().substring(child.getPath().lastIndexOf("/") + 1);
                        // Add new entry to zip
                        zip.putNextEntry(new ZipEntry(fname));
                        // Write data from FileInputStream to zip
                        int len;
                        while ((len = fi.read(buf)) > 0) {
                            zip.write(buf, 0, len);
                        }
                        // Close FileInputStream and current zip entry
                        fi.close();
                        zip.closeEntry();
                    }
                }
                // Write everything and close everything up
                zip.flush();
                zip.close();
            } catch (Exception e) {
                if (ERROR) Log.e("MAIN", "XML write exception: " + e);
            }

            // Upload zip file
            ArrayList<String> up = LoadDLLoginData();
            if (up.size() >= 2) {
                File xmlf = new File(xmldir, "upload.zip");
                String res = DLApi.UploadDives(up.get(0), up.get(1), xmlf);
                Toast.makeText(this, res, Toast.LENGTH_LONG).show();
                setContentView(R.layout.activity_main);
            } else {
                Toast.makeText(this, "Missing credentials?!", Toast.LENGTH_LONG).show();
            }
        }

        // Clean folders up
        ret = CleanTempDir(xmldir, true);
        if(!ret)
            if (DEBUG) Log.d("MAIN", "Couldn't clean up: " + xmldir.getPath());

    }

    private Boolean CleanTempDir(File xmldir, Boolean fin) {

        boolean res = true;
        if(xmldir.exists()){
            for (File child : Objects.requireNonNull(xmldir.listFiles())) {
                res = child.delete();
                if(!res)
                    if (DEBUG) Log.d("MAIN", "Couldn't delete: " + child.getPath());
            }
            res = xmldir.delete();
            if(!res)
                if (DEBUG) Log.d("MAIN", "Couldn't delete: " + xmldir.getPath());
        }
        if(!fin) {
            return xmldir.mkdir();
        } else {
            return res;
        }

    }

}