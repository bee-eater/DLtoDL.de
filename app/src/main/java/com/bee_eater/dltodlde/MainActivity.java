package com.bee_eater.dltodlde;

import com.bee_eater.dltodlde.DiveLogsApi.*;
import static com.bee_eater.dltodlde.Constants.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


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
        LoadDLLoginData();
    }

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
        DLC.setGuiConnector((ProgressBar)findViewById(R.id.prbDLCFileLoading), (TextView)findViewById(R.id.txtProgressNr), (TextView)findViewById(R.id.txtProgressPercentage));
    }


    /**
     * Function that loads login data from shared preferences and puts it into the input fields of the app
     */
    private void LoadDLLoginData(){
        SharedPreferences sp1 = this.getSharedPreferences("DLLogin", MODE_PRIVATE);
        if(sp1 != null) {
            String user = sp1.getString("user", null);
            if (user != null) {
                EditText tinDLUsername = findViewById(R.id.tinDLUsername);
                tinDLUsername.setText(user);
            }
            String pass = sp1.getString("pass", null);
            if (pass != null) {
                EditText pwdDLPassword = findViewById(R.id.pwdDLPassword);
                pwdDLPassword.setText(pass);
            }
        }
    }

    /**
     * Function saves login data to shared preferences
     * @param user Username
     * @param pass Password
     */
    private void SaveDLLoginData(String user, String pass){
        SharedPreferences sp=getSharedPreferences("DLLogin", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("user", user);
        Ed.putString("pass", pass);
        Ed.apply();
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
                // TODO: Get dives from divelogs.de
                diveLogsLoadDives();
                // Load list content, contentChanged event sets all required adapters etc...
                setContentView(R.layout.view_diveselection);
                // TODO: Create conversion from DivingLog to DLD format
            }
        }
    }


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
            SaveDLLoginData("","");
            Toast.makeText(this,"Login data cleared!", Toast.LENGTH_LONG).show();
        } else {
            boolean res = DLApi.Login(user, pass);
            if(res) {
                Toast.makeText(this,"Login successfull! Data saved!", Toast.LENGTH_LONG).show();
                SaveDLLoginData(user, pass);
                if (DEBUG) Log.d("MAIN", "Found user image: " + DLApi.UserImageURL);
            } else {
                Toast.makeText(this,"Login error! Please check your credentials!", Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * Event: btnTest was clicked on GUI
     * @param v View calling the event
     */
    public void on_btnTest_clicked(View v){
        setContentView(R.layout.view_diveselection);
    }

    public void on_btnBackToHome_clicked(View v){
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Selected: " + DLC.DLDives.get(0).isSelected.toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * ActivityResultLauncher for DivesSelection-Dialog
     */
    ActivityResultLauncher<Intent> DivesSelectionLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == 2) {
                    // There are no request codes
                    Intent data = result.getData();
                    String message = data.getStringExtra("MESSAGE");
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });

}