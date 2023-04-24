package com.bee_eater.dltodlde;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import android.database.sqlite.SQLiteDatabase;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermission()){
            requestPermission();
        }

        List<DLDive> DLDives = Collections.<DLDive>emptyList();

        Intent intent = getIntent();
        String intentType = intent.getType();
        if (Objects.equals(intentType, "application/x-sql")){
            Uri file = intent.getData();
            String filepath = file.getPath();
            try {
                DLDives = DivingLog_LoadDiveLogFile("/storage/emulated/0/Diving/Logbook.sql");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (DLDives.size() > 0){
                for(DLDive d : DLDives){
                    Log.d("FOUND DIVE", d.toString());
                }
            }

        }

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        setupGUI();

    }

    //====================================================================================================
    //====================================================================================================
    // APP FUNCTIONS
    //====================================================================================================
    //====================================================================================================
    private void setupGUI(){
        LoadDLLoginData();
    }

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

    private void SaveDLLoginData(String user, String pass){
        SharedPreferences sp=getSharedPreferences("DLLogin", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("user", user);
        Ed.putString("pass", pass);
        Ed.commit();
    }


    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int read_permission = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
            int write_permission = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            return read_permission == PackageManager.PERMISSION_GRANTED && write_permission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    //====================================================================================================
    //====================================================================================================
    // EVENT HANDLERS
    //====================================================================================================
    //====================================================================================================
    public void on_btnDLLogin_clicked(View v){

        EditText tinDLUsername = findViewById(R.id.tinDLUsername);
        EditText pwdDLPassword = findViewById(R.id.pwdDLPassword);
        String user = tinDLUsername.getText().toString();
        String pass = pwdDLPassword.getText().toString();

        if(Objects.equals(user, "")){
            SaveDLLoginData("","");
            Toast.makeText(this,"Login data cleared!", Toast.LENGTH_LONG).show();
        } else {
            boolean res = DiveLogsApi_Login(user, pass);
            if(res) {
                Toast.makeText(this,"Login successfull! Data saved!", Toast.LENGTH_LONG).show();
                SaveDLLoginData(user, pass);
            } else {
                Toast.makeText(this,"Login error! Please check your credentials!", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void on_btnTest_clicked(View v){
        Toast.makeText(this,"Dang!", Toast.LENGTH_LONG).show();
    }

    //====================================================================================================
    //====================================================================================================
    // DIVELOGS API FUNCTIONS
    //====================================================================================================
    //====================================================================================================
    private boolean DiveLogsApi_Login(String user, String pass) {

        //https://divelogs.de/xml_authenticate_user.php
        //"user" (mandatory) with your username
        //"pass" (mandatory) with your password
        //<userauthentification version="1.0">
        //  <login>succeeded</login>
        //  <units>metric</units>
        //  <userimage>https://www.divelogs.de/userprofiles/16142Marcel512.jpg</userimage>
        //</userauthentification>

        String httpResponse = DiveLogs_GetXmlFromHttpsPost(user, pass, "https://divelogs.de/xml_authenticate_user.php");
        return DiveLogsXml_CheckLogin(httpResponse);

    }

    private String DiveLogsApi_GetDives(String user, String pass){

        //https://divelogs.de/xml_available_dives.php
        //"user" (mandatory) with your username
        //"pass" (mandatory) with your password
        //<DiveDateReader version="1.0">
        //  <Login>succeeded</Login>
        //  <DiveDates>
        //    <date divelogsId="3536237" lastModified="2023-02-11 23:02:19" diveNumber="401">11.02.2023 12:47</date>
        //  </DiveDates>
        //</DiveDateReader>

        String httpResponse = DiveLogs_GetXmlFromHttpsPost(user, pass, "https://divelogs.de/xml_available_dives.php");
        if(DiveLogsXml_CheckLogin(httpResponse)){
            return httpResponse;
        } else {
            return "1";
        }

    }


    private String DiveLogs_GetXmlFromHttpsPost(String user, String pass, String API_Url){

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("user", user)
                .add("pass", pass)
                .build();

        Request request = new Request.Builder()
                .url(API_Url)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                Log.d("DiveLogs_GetXmlFromHttpsPost", responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }

            String httpResponse = response.body().string();

            return httpResponse;

        } catch (Exception ex){
            Log.e("DiveLogs_GetXmlFromHttpsPost", ex.toString());
            return "";
        }
    }

    private Boolean DiveLogsXml_CheckLogin(String xml){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringBuilder xmlStringBuilder = new StringBuilder();
            xmlStringBuilder.append(xml);
            ByteArrayInputStream input = new ByteArrayInputStream(
                    xmlStringBuilder.toString().getBytes("UTF-8"));
            Document doc = builder.parse(input);
            input.close();

            NodeList nListLower = doc.getElementsByTagName("login");
            NodeList nListUpper = doc.getElementsByTagName("Login");

            Boolean res;
            if(nListLower.getLength() > 0) {
                res = Objects.equals(nListLower.item(0).getTextContent(), "succeeded");
            } else if (nListUpper.getLength() > 0) {
                res = Objects.equals(nListUpper.item(0).getTextContent(), "succeeded");
            } else {
                res = false;
            }
            return res;

        } catch (Exception e) {
            Log.e("DiveLogsXml_CheckLogin", e.toString());
            return false;
        }
    }


    //====================================================================================================
    //====================================================================================================
    // DIVINGLOG FUNCTIONS
    //====================================================================================================
    //====================================================================================================
    private List<DLDive> DivingLog_LoadDiveLogFile(String file){

        // Init empty dive list
        List<DLDive> DLDives = new ArrayList<DLDive>();

        // Load database from file and query all dives from logbook
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file, null, 0);
        String query = "SELECT * FROM Logbook";
        Cursor res = db.rawQuery(query,null);

        // Parse results
        res.moveToFirst();
        if (res.moveToFirst()) {
            while (!res.isAfterLast()) {

                // Create temp dive
                DLDive dive = new DLDive();

                // Loop over all columns, get value and add to dive
                for (Integer i = 0; i < res.getColumnCount(); i++) {
                    // Get column name
                    String colName = res.getColumnName(i);
                    // Get column type in order to use correct function
                    Integer colType = res.getType(i);
                    // Get column value based on type
                    Object colValue;
                    switch(colType){
                        case 0: colValue = null; break;// NULL value, ignore
                        case 1: colValue = res.getInt(i); break;
                        case 2: colValue = res.getDouble(i); break;
                        case 3: colValue = res.getString(i); break;
                        default:
                            Log.d("COLUMN", "Found type :: " + colType.toString());
                            colValue = "ERR";
                    }

                    // Add value to dive by member name
                    try {
                        dive.setMemberByName(colName, colValue);
                    } catch (NoSuchFieldException e) {
                        Log.d("QUERY", e.toString());
                    } catch (IllegalAccessException e) {
                        Log.d("QUERY", e.toString());
                    }
                    Log.d("COLUMN",colName + " :: " + colType.toString() + " :: " + String.valueOf(colValue));
                }
                // add dives to dive list
                DLDives.add(dive);
                res.moveToNext();
            }
        }
        res.close();
        db.close();

        return DLDives;
    }

    //====================================================================================================
    //====================================================================================================
    // TEST FUNCTIONS
    //====================================================================================================
    //====================================================================================================
    public void Test_DownloadDives(View v){
        EditText tinDLUsername = findViewById(R.id.tinDLUsername);
        EditText pwdDLPassword = findViewById(R.id.pwdDLPassword);
        String user = tinDLUsername.getText().toString();
        String pass = pwdDLPassword.getText().toString();
        String res = DiveLogsApi_GetDives(user, pass);
        if(Objects.equals(res, "1")){
            Toast.makeText(this,"Login error! Please check your credentials!", Toast.LENGTH_LONG).show();
        } else {
            //Log.d("on_btnTest_clicked", res);
            Toast.makeText(this,"Got your dives!", Toast.LENGTH_LONG).show();
        }
    }

}