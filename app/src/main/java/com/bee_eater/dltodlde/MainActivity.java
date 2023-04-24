package com.bee_eater.dltodlde;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import android.database.sqlite.SQLiteDatabase;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent req = new Intent();
        req.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        //startActivity(req);

        Intent intent = getIntent();
        String intentType = intent.getType();

        SQLiteDatabase db = SQLiteDatabase.openDatabase("/storage/emulated/0/Diving/Logbook.sql", null, 0);
        String query = "SELECT * FROM Logbook";
        Cursor res = db.rawQuery(query,null);
        res.moveToFirst();
        if (res.moveToFirst()) {
            while (!res.isAfterLast()) {
                String Number = res.getString(1);
                String Country = res.getString(5);
                String Depth = res.getString(12);
                res.moveToNext();
            }
        }
        String query_result = res.toString();
        res.close();

        if (Objects.equals(intentType, "application/x-sql")){
            //InputStream istream = getContentResolver().openInputStream(Uri.parse(intent.getDataString()));
            Uri file = intent.getData();
            String filepath = file.getPath();
            try {

            } catch (Exception e) {
                throw new RuntimeException(e);
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