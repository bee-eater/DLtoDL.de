package com.bee_eater.dltodlde;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.bee_eater.dltodlde.Constants.*;

//====================================================================================================
//====================================================================================================
// DIVELOGS API FUNCTIONS
//====================================================================================================
//====================================================================================================
public class DiveLogsApi {

    public String UserImageURL;

    /**
     * Constructor, don't do shit...
     */
    public DiveLogsApi(){

    }

    /**
     * DiveLogs.de API call: Login user
     * @param user Username
     * @param pass Password
     * @return true if the login was successfull
     */
    public boolean Login(String user, String pass) {

        //https://divelogs.de/xml_authenticate_user.php
        //"user" (mandatory) with your username
        //"pass" (mandatory) with your password
        //<userauthentification version="1.0">
        //  <login>succeeded</login>
        //  <units>metric</units>
        //  <userimage>https://www.divelogs.de/userprofiles/16142Marcel512.jpg</userimage>
        //</userauthentification>

        String httpResponse = GetXmlFromHttpsPost(user, pass, "https://divelogs.de/xml_authenticate_user.php");
        Boolean loginSuccessful = CheckLogin(httpResponse);
        if (loginSuccessful){
            UserImageURL = GetUserImageURL(httpResponse);
        }
        return loginSuccessful;

    }

    /**
     * DiveLogs.de API call: Download a list of available dives
     * @param user Username
     * @param pass Password
     * @return a list of available dives
     */
    public String GetDives(String user, String pass){

        //https://divelogs.de/xml_available_dives.php
        //"user" (mandatory) with your username
        //"pass" (mandatory) with your password
        //<DiveDateReader version="1.0">
        //  <Login>succeeded</Login>
        //  <DiveDates>
        //    <date divelogsId="3536237" lastModified="2023-02-11 23:02:19" diveNumber="401">11.02.2023 12:47</date>
        //  </DiveDates>
        //</DiveDateReader>

        String httpResponse = GetXmlFromHttpsPost(user, pass, "https://divelogs.de/xml_available_dives.php");
        if(CheckLogin(httpResponse)){
            return httpResponse;
        } else {
            return "1";
        }

    }

    /**
     * DiveLogs.de API helper function: Make a https API call to DiveLogs.de URL and return xml body
     * @param user Username
     * @param pass Password
     * @param API_Url API URL to make the call to
     * @return xml response from DiveLogs.de
     */
    private String GetXmlFromHttpsPost(String user, String pass, String API_Url){

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
                if (VERBOSE) Log.v("DLAPI", responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }

            assert response.body() != null;
            String rsp = response.body().string();
            if (DEBUG) Log.d("DLAPI", rsp);
            return rsp;

        } catch (Exception e){
             if (ERROR) Log.e("DLAPI", "Error getting response from OkHttpClient: " + e);
            return "";
        }
    }

    /**
     * Evaluates if xml answer from DiveLogs.de contains a successful login info
     * @param xml xml response from the api call
     * @return true if login was successful
     */
    private Boolean CheckLogin(String xml){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(
                    xml.getBytes(StandardCharsets.UTF_8));
            Document doc = builder.parse(input);
            input.close();

            NodeList nListLower = doc.getElementsByTagName("login");
            NodeList nListUpper = doc.getElementsByTagName("Login");

            boolean res;
            if(nListLower.getLength() > 0) {
                res = Objects.equals(nListLower.item(0).getTextContent(), "succeeded");
            } else if (nListUpper.getLength() > 0) {
                res = Objects.equals(nListUpper.item(0).getTextContent(), "succeeded");
            } else {
                res = false;
            }
            return res;

        } catch (Exception e) {
            if (ERROR) Log.e("DLAPI", "Error getting login info from xml: " + e);
            return false;
        }
    }

    /**
     * Retrieve url from login response
     * @param xml response from login api call
     * @return path to user image contained in xml data
     */
    private String GetUserImageURL(String xml) {
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(
                    xml.getBytes(StandardCharsets.UTF_8));
            Document doc = builder.parse(input);
            input.close();

            NodeList nUserImage = doc.getElementsByTagName("userimage");
            if (nUserImage.getLength() > 0) {
                return nUserImage.item(0).getTextContent();
            } else {
                return "";
            }

        } catch (Exception e){
            if (ERROR) Log.e("DLAPI", "Exception getting user image from xml answer: " + e);
            return "";
        }
    }
}
