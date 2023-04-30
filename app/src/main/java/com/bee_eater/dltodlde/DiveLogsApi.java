package com.bee_eater.dltodlde;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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

    public class DiveLogsDive{
        public String divelogsId;
        public String lastModified;
        public LocalDateTime lastModifiedDT;
        public String diveNumber;
        public String date;
        public LocalDateTime DiveDateDT;
    }

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
        String httpResponse = GetXmlFromHttpsPost(user, pass, "https://divelogs.de/xml_authenticate_user.php",null);
        Boolean loginSuccessful = Boolean.FALSE;
        if(Objects.equals(httpResponse, "")){

        } else {
            loginSuccessful = CheckLogin(httpResponse);
            if (loginSuccessful) {
                UserImageURL = GetUserImageURL(httpResponse);
            }
        }
        return loginSuccessful;

    }

    /**
     * DiveLogs.de API call: Download a list of available dives
     * @param user Username
     * @param pass Password
     * @return a list of available dives
     */
    public String GetDives(String user, String pass, ArrayList<DiveLogsDive> dlogs){

        //https://divelogs.de/xml_available_dives.php
        //"user" (mandatory) with your username
        //"pass" (mandatory) with your password
        //<DiveDateReader version="1.0">
        //  <Login>succeeded</Login>
        //  <DiveDates>
        //    <date divelogsId="3536237" lastModified="2023-02-11 23:02:19" diveNumber="401">11.02.2023 12:47</date>
        //  </DiveDates>
        //</DiveDateReader>
        String httpResponse = GetXmlFromHttpsPost(user, pass, "https://divelogs.de/xml_available_dives.php", null);
        if(CheckLogin(httpResponse)){
            ArrayList<DiveLogsDive> dld = new ArrayList<>();
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                ByteArrayInputStream input = new ByteArrayInputStream(httpResponse.getBytes(StandardCharsets.UTF_8));
                Document doc = builder.parse(input);
                input.close();

                NodeList nDates = doc.getElementsByTagName("date");
                for (Node n : iterable(nDates)) {
                    DiveLogsDive dive = new DiveLogsDive();
                    // Get main node text
                    DateTimeFormatter fDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                    dive.date = n.getTextContent();
                    dive.DiveDateDT = LocalDateTime.parse(dive.date, fDate);
                    // Get attributes and read them
                    NamedNodeMap nm = n.getAttributes();
                    dive.divelogsId = nm.getNamedItem("divelogsId").getTextContent();
                    dive.diveNumber = nm.getNamedItem("diveNumber").getTextContent();
                    DateTimeFormatter fMod = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    dive.lastModified = nm.getNamedItem("lastModified").getTextContent();
                    dive.lastModifiedDT = LocalDateTime.parse(dive.lastModified, fMod);
                    // Add dive to external array
                    dlogs.add(dive);
                }
                return "0";
            } catch (Exception e){
                return e.toString();
            }
        } else {
            return "-1";
        }

    }

    /**
     * DiveLogs.de API helper function: Make a https API call to DiveLogs.de URL and return xml body
     * @param user Username
     * @param pass Password
     * @param API_Url API URL to make the call to
     * @param zip Zip file containing dives to upload
     * @return xml response from DiveLogs.de
     */
    private String GetXmlFromHttpsPost(String user, String pass, String API_Url, File zip){

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody;
        if (zip == null) {
            formBody = new FormBody.Builder()
                    .add("user", user)
                    .add("pass", pass)
                    .build();
        } else {
            formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("userfile", zip.getName(),
                            RequestBody.create(zip,MediaType.parse("application/xml")))
                    .addFormDataPart("user", user)
                    .addFormDataPart("pass", pass)
                    .build();
        }

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


    /**
     * Upload zip file with POST to DiveLogs.de API
     * @param xmlfname Path to the zip file to upload
     */
    public String UploadDives(String user, String pass, File xmlzip) {

        //https://divelogs.de/DivelogsDirectImport.php
        //"user" (mandatory) with your username
        //"pass" (mandatory) with your password
        //"userfile" (mandatory) with your DLD file
        //"dldfile" (mandatory) zip file containing xml dives
        //<divelogsDataImport version="1.0">
        //  <Login>succeeded</Login>
        //  <FileCopy>succeeded</FileCopy>
        //  <entered>0</entered>
        //  <skipped>0</skipped>
        //  <updated>0</updated>
        //  <invalid>0</invalid>
        //  <profiles>0</profiles>
        //</divelogsDataImport>

        String httpResponse = GetXmlFromHttpsPost(user, pass, "https://divelogs.de/DivelogsDirectImport.php",xmlzip);
        if(Objects.equals(httpResponse, "")){
            return "";
        } else {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                ByteArrayInputStream input = new ByteArrayInputStream(
                        httpResponse.getBytes(StandardCharsets.UTF_8));
                Document doc = builder.parse(input);
                input.close();

                NodeList nLogin = doc.getElementsByTagName("Login");
                Boolean Login = false;
                if(nLogin.getLength() > 0) {
                    Login = Objects.equals(nLogin.item(0).getTextContent(), "succeeded");
                }

                NodeList nFileCopy = doc.getElementsByTagName("FileCopy");
                Boolean FileCopy = false;
                if(nFileCopy.getLength() > 0) {
                    FileCopy = Objects.equals(nFileCopy.item(0).getTextContent(), "succeeded");
                }

                NodeList nEntered = doc.getElementsByTagName("entered");
                Integer entered = 0;
                if(nEntered.getLength() > 0) {
                    entered = Integer.valueOf(nEntered.item(0).getTextContent());
                }

                NodeList nSkipped = doc.getElementsByTagName("skipped");
                Integer skipped = 0;
                if(nSkipped.getLength() > 0) {
                    skipped = Integer.valueOf(nSkipped.item(0).getTextContent());
                }

                NodeList nUpdated = doc.getElementsByTagName("updated");
                Integer updated = 0;
                if(nUpdated.getLength() > 0) {
                    updated = Integer.valueOf(nUpdated.item(0).getTextContent());
                }

                NodeList nInvalid = doc.getElementsByTagName("invalid");
                Integer invalid = 0;
                if(nInvalid.getLength() > 0) {
                    invalid = Integer.valueOf(nInvalid.item(0).getTextContent());
                }

                return "Login: " + Login + " | Upload: " + FileCopy + " | Entered: " + entered + " | Skipped: " + skipped + " | Updated: " + updated + " | Invalid: " + invalid;
            } catch (Exception e){
                return "";
            }
        }

    }


    /**
     * Helper for iterating through NodeList (org.w3c.com)
     * @param nodeList
     * @return
     */
    public static Iterable<Node> iterable(final NodeList nodeList) {
        return () -> new Iterator<Node>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < nodeList.getLength();
            }

            @Override
            public Node next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return nodeList.item(index++);
            }
        };
    }
}
