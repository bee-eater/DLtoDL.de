package com.bee_eater.dltodlde;

import static com.bee_eater.dltodlde.Constants.*;

import android.util.Log;

import androidx.annotation.NonNull;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class DivingLogDive extends DivingLogTank {

    private static final String[] StrWaterVisibility = {"", "Gut", "Mittel", "Schlecht"};
    private static final List<String> WaterVisibility = Arrays.asList(StrWaterVisibility);

    private static final String[] StrTankType = {"", "Aluminium", "Stahl", "Carbon"};
    private static final List<String> TankType = Arrays.asList(StrTankType);

    private static final String[] StrWaterType = {"", "Salzwasser", "Süßwasser", "Mischwasser", "Chloriert"};
    private static final List<String> WaterType = Arrays.asList(StrWaterType);

    // Other stuff used in app
    public Boolean isSelected = Boolean.FALSE;

    public Integer DiveLogsIndex = -1;
    public String ListInfoText = "";

    // DivingLog parameters read from sql
    public Integer Number;
    public String Divedate;
    public LocalDateTime DiveDateDT;
    public String Entrytime;
    public String Surfint;
    public String Country;
    public Integer CountryID;
    public String City;
    public Integer CityID;
    public String Place;
    public Integer PlaceID;
    public Double Divetime;
    public Double Depth;
    public String Buddy;
    public String BuddyIDs;
    public String Signature;
    public String Comments;
    public Integer Water;
    public Integer Entry;
    public String Divetype;
    public String Gas;
    public String Weather;
    public String UWCurrent;
    public String Surface;
    public Integer Visibility;
    public Double Airtemp;
    public Double Watertemp;
    public Double Weight;
    public Integer Deco;
    public String Decostops;
    public Integer Rep;
    public String Altitude;
    public String Divesuit;
    public String Computer;
    public Integer ProfileInt;
    public String Profile;
    public String UsedEquip;
    public String Profile2;
    public String Profile3;
    public Double DepthAvg;
    public String UUID;
    public String Updated;
    public String Profile4;
    public String Profile5;
    public Integer RepNumber;
    public String VisHor;
    public String VisVer;
    public String CNS;
    public String PGStart;
    public String PGEnd;
    public String Divemaster;
    public String Boat;
    public Integer Rating;
    public Integer ShopID;
    public Integer TripID;
    public Integer UtcOffset;
    public Double DesaturationTime;
    public Double NoFlyTime;
    public ArrayList<DivingLogTank> Tanks = new ArrayList<>();

    public DivingLogPlace PlaceInfo;

    @NonNull
    @Override
    public String toString() {
        return "{ \"ID\": " + String.valueOf(this.ID) + "," +
                " \"Number\": " + String.valueOf(this.Number) + "," +
                " \"Divedate\": " + ((this.Divedate == null) ? "null" : ("\"" + String.valueOf(this.Divedate) + "\",")) +
                " \"Entrytime\": " + ((this.Entrytime == null) ? "null" : ("\"" + String.valueOf(this.Entrytime) + "\"")) + "," +
                " \"Surfint\": " + ((this.Surfint == null) ? "null" : ("\"" + String.valueOf(this.Surfint) + "\"")) + "," +
                " \"Country\": " + ((this.Country == null) ? "null" : ("\"" + String.valueOf(this.Country) + "\"")) + "," +
                " \"CountryID\": " + String.valueOf(this.CountryID) + "," +
                " \"City\": " + ((this.City == null) ? "null" : ("\"" + String.valueOf(this.City) + "\"")) + "," +
                " \"CityID\": " + String.valueOf(this.CityID) + "," +
                " \"Place\": " + ((this.Place == null) ? "null" : ("\"" + String.valueOf(this.Place) + "\"")) + "," +
                " \"PlaceID\": " + String.valueOf(this.PlaceID) + "," +
                " \"Divetime\": " + String.valueOf(this.Divetime) + "," +
                " \"Depth\": " + String.valueOf(this.Depth) + "," +
                " \"Buddy\": " + ((this.Buddy == null) ? "null" : ("\"" + String.valueOf(this.Buddy) + "\"")) + "," +
                " \"BuddyIDs\": " + ((this.BuddyIDs == null) ? "null" : ("\"" + String.valueOf(this.BuddyIDs) + "\"")) + "," +
                " \"Signature\": " + ((this.Signature == null) ? "null" : ("\"" + String.valueOf(this.Signature) + "\"")) + "," +
                " \"Comments\": " + ((this.Comments == null) ? "null" : ("\"" + String.valueOf(this.Comments) + "\"")) + "," +
                " \"Water\": " + String.valueOf(this.Water) + "," +
                " \"Entry\": " + String.valueOf(this.Entry) + "," +
                " \"Divetype\": " + ((this.Divetype == null) ? "null" : ("\"" + String.valueOf(this.Divetype) + "\"")) + "," +
                " \"Tanktype\": " + String.valueOf(this.Tanktype) + "," +
                " \"Tanksize\": " + String.valueOf(this.Tanksize) + "," +
                " \"PresS\": " + String.valueOf(this.PresS) + "," +
                " \"PresE\": " + String.valueOf(this.PresE) + "," +
                " \"Gas\": " + ((this.Gas == null) ? "null" : ("\"" + String.valueOf(this.Gas) + "\"")) + "," +
                " \"Weather\": " + ((this.Weather == null) ? "null" : ("\"" + String.valueOf(this.Weather) + "\"")) + "," +
                " \"UWCurrent\": " + ((this.UWCurrent == null) ? "null" : ("\"" + String.valueOf(this.UWCurrent) + "\"")) + "," +
                " \"Surface\": " + ((this.Surface == null) ? "null" : ("\"" + String.valueOf(this.Surface) + "\"")) + "," +
                " \"Visibility\": " + String.valueOf(this.Visibility) + "," +
                " \"Airtemp\": " + String.valueOf(this.Airtemp) + "," +
                " \"Watertemp\": " + String.valueOf(this.Watertemp) + "," +
                " \"Weight\": " + String.valueOf(this.Weight) + "," +
                " \"Deco\": " + String.valueOf(this.Deco) + "," +
                " \"Decostops\": " + ((this.Decostops == null) ? "null" : ("\"" + String.valueOf(this.Decostops) + "\"")) + "," +
                " \"Rep\": " + String.valueOf(this.Rep) + "," +
                " \"Altitude\": " + ((this.Altitude == null) ? "null" : ("\"" + String.valueOf(this.Altitude) + "\"")) + "," +
                " \"Divesuit\": " + ((this.Divesuit == null) ? "null" : ("\"" + String.valueOf(this.Divesuit) + "\"")) + "," +
                " \"Computer\": " + ((this.Computer == null) ? "null" : ("\"" + String.valueOf(this.Computer) + "\"")) + "," +
                " \"ProfileInt\": " + String.valueOf(this.ProfileInt) + "," +
                " \"Profile\": " + ((this.Profile == null) ? "null" : ("\"" + String.valueOf(this.Profile) + "\"")) + "," +
                " \"UsedEquip\": " + ((this.UsedEquip == null) ? "null" : ("\"" + String.valueOf(this.UsedEquip) + "\"")) + "," +
                " \"PresW\": " + String.valueOf(this.PresW) + "," +
                " \"Profile2\": " + ((this.Profile2 == null) ? "null" : ("\"" + String.valueOf(this.Profile2) + "\"")) + "," +
                " \"Profile3\": " + ((this.Profile3 == null) ? "null" : ("\"" + String.valueOf(this.Profile3) + "\"")) + "," +
                " \"DepthAvg\": " + String.valueOf(this.DepthAvg) + "," +
                " \"UUID\": " + ((this.UUID == null) ? "null" : ("\"" + String.valueOf(this.UUID) + "\"")) + "," +
                " \"Updated\": " + ((this.Updated == null) ? "null" : ("\"" + String.valueOf(this.Updated) + "\"")) + "," +
                " \"Profile4\": " + ((this.Profile4 == null) ? "null" : ("\"" + String.valueOf(this.Profile4) + "\"")) + "," +
                " \"Profile5\": " + ((this.Profile5 == null) ? "null" : ("\"" + String.valueOf(this.Profile5) + "\"")) + "," +
                " \"RepNumber\": " + String.valueOf(this.RepNumber) + "," +
                " \"VisHor\": " + ((this.VisHor == null) ? "null" : ("\"" + String.valueOf(this.VisHor) + "\"")) + "," +
                " \"VisVer\": " + ((this.VisVer == null) ? "null" : ("\"" + String.valueOf(this.VisVer) + "\"")) + "," +
                " \"CNS\": " + ((this.CNS == null) ? "null" : ("\"" + String.valueOf(this.CNS) + "\"")) + "," +
                " \"PGStart\": " + ((this.PGStart == null) ? "null" : ("\"" + String.valueOf(this.PGStart) + "\"")) + "," +
                " \"PGEnd\": " + ((this.PGEnd == null) ? "null" : ("\"" + String.valueOf(this.PGEnd) + "\"")) + "," +
                " \"Divemaster\": " + ((this.Divemaster == null) ? "null" : ("\"" + String.valueOf(this.Divemaster) + "\"")) + "," +
                " \"Boat\": " + ((this.Boat == null) ? "null" : ("\"" + String.valueOf(this.Boat) + "\"")) + "," +
                " \"Rating\": " + String.valueOf(this.Rating) + "," +
                " \"O2\": " + String.valueOf(this.O2) + "," +
                " \"He\": " + String.valueOf(this.He) + "," +
                " \"DblTank\": " + String.valueOf(this.DblTank) + "," +
                " \"SupplyType\": " + String.valueOf(this.SupplyType) + "," +
                " \"MinPPO2\": " + String.valueOf(this.MinPPO2) + "," +
                " \"MaxPPO2\": " + String.valueOf(this.MaxPPO2) + "," +
                " \"ShopID\": " + String.valueOf(this.ShopID) + "," +
                " \"TripID\": " + String.valueOf(this.TripID) + "," +
                " \"utcOffset\": " + String.valueOf(this.UtcOffset) + "," +
                " \"DesaturationTime\": " + String.valueOf(this.DesaturationTime) + "," +
                " \"NoFlyTime\": " + String.valueOf(this.NoFlyTime) + "," +
                " \"ScrubberTime\": " + String.valueOf(this.ScrubberTime) +
                "}";
    }


    public String toDLD(){

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            CDATASection cdata;
            DateTimeFormatter dtf;
            Element el;
            String tmpStr;

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("DIVELOGSDATA");
            doc.appendChild(rootElement);

            // Dive date [dd.MM.yyyy]
            dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            el = doc.createElement("DATE");
            el.setTextContent(this.DiveDateDT.format(dtf));
            rootElement.appendChild(el);

            // Dive time [HH:mm:ss]
            dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            el = doc.createElement("TIME");
            el.setTextContent(this.DiveDateDT.format(dtf));
            rootElement.appendChild(el);

            // Dive time in seconds [s]
            el = doc.createElement("DIVETIMESEC");
            el.setTextContent(String.valueOf((int)(this.Divetime * 60)));
            rootElement.appendChild(el);

            // Surface time [s]
            el = doc.createElement("SURFACETIME");
            int surfint = 0;
            if (this.Surfint != null){
                try {
                    String hours = this.Surfint.split(":")[0];
                    String minutes = this.Surfint.split(":")[1];
                    surfint = Integer.parseInt(hours)*3600 + Integer.parseInt(minutes)*60;
                } catch (Exception e){
                    // nop
                }
            }
            if (surfint != 0) {
                el.setTextContent(String.valueOf(surfint));
            }
            rootElement.appendChild(el);

            // Depth (max) [m]
            el = doc.createElement("MAXDEPTH");
            if (this.Depth != null)
                el.setTextContent(String.format("%.1f", this.Depth));
            rootElement.appendChild(el);

            // Depth (avg) [m]
            el = doc.createElement("MEANDEPTH");
            if (this.DepthAvg != null)
                el.setTextContent(String.format("%.1f", this.DepthAvg));
            rootElement.appendChild(el);

            // Location (CDATA!)
            el = doc.createElement("LOCATION");
            tmpStr = "";
            if (this.Country != null){
                tmpStr += this.Country + ", ";
            }
            if (this.City != null){
                tmpStr += this.City;
            }
            cdata = doc.createCDATASection(tmpStr);
            el.appendChild(cdata);
            rootElement.appendChild(el);

            // Site (CDATA!)
            el = doc.createElement("SITE");
            if (this.Place != null) {
                cdata = doc.createCDATASection(this.Place);
                el.appendChild(cdata);
            }
            rootElement.appendChild(el);

            // Weather (CDATA!)
            el = doc.createElement("WEATHER");
            if (this.Weather != null) {
                cdata = doc.createCDATASection(this.Weather);
                el.appendChild(cdata);
            }
            rootElement.appendChild(el);

            // Visibility (CDATA!)
            el = doc.createElement("WATERVIZIBILITY");
            if (this.Visibility != null) {
                if(this.Visibility < WaterVisibility.size()) {
                    cdata = doc.createCDATASection(WaterVisibility.get(this.Visibility));
                    el.appendChild(cdata);
                }
            }
            rootElement.appendChild(el);

            // Air temperature
            el = doc.createElement("AIRTEMP");
            if (this.Airtemp != null)
                el.setTextContent(String.format("%.1f", this.Airtemp));
            rootElement.appendChild(el);

            // Water temp max depth
            el = doc.createElement("WATERTEMPMAXDEPTH");
            if (this.Watertemp != null)
                el.setTextContent(String.format("%.1f", this.Watertemp));
            rootElement.appendChild(el);

            // Water temp end
            // --> NA

            // Partner (Buddies...) (CDATA!)
            el = doc.createElement("PARTNER");
            if (this.Buddy != null) {
                cdata = doc.createCDATASection(this.Buddy);
                el.appendChild(cdata);
            }
            rootElement.appendChild(el);

            // Boat name (CDATA!)
            el = doc.createElement("BOATNAME");
            if (this.Boat != null) {
                cdata = doc.createCDATASection(this.Boat);
                el.appendChild(cdata);
            }
            rootElement.appendChild(el);

            // Weight [kg]
            el = doc.createElement("WEIGHT");
            if (this.Weight != null)
                el.setTextContent(String.format("%.2f", this.Weight));
            rootElement.appendChild(el);

            addTank(doc,rootElement,this);

            // Additional tanks
            if(this.Tanks.size() > 0) {
                Element addTanks = doc.createElement("ADDITIONALTANKS");
                for(DivingLogTank t: this.Tanks){
                    Element tank = doc.createElement("TANK");
                    addTank(doc,tank,t);
                    addTanks.appendChild(tank);
                }
                rootElement.appendChild(addTanks);
            }

            // Notes (CDATA!)
            el = doc.createElement("LOGNOTES");
            if (this.Comments != null) {
                cdata = doc.createCDATASection(this.Comments);
                el.appendChild(cdata);
            }
            rootElement.appendChild(el);

            // Latitude
            // Longitude
            try {
                GeoConverter geo = new GeoConverter();
                double[] latLon = geo.convert(this.PlaceInfo.Lat + " " + this.PlaceInfo.Lon);
                el = doc.createElement("LAT");
                el.setTextContent(String.valueOf((double)(latLon[0])));
                rootElement.appendChild(el);
                el = doc.createElement("LNG");
                el.setTextContent(String.valueOf((double)(latLon[1])));
                rootElement.appendChild(el);
            } catch (Exception e){
                if (DEBUG) Log.d("DivingLogDive.toDLD(): ", "Error converting to latLon:" + e);
            }

            // Google zoom level
            // --> NA

            // Sample interval [s] (for following depth profile)
            el = doc.createElement("SAMPLEINTERVAL");
            if (this.ProfileInt != null)
                el.setTextContent(String.valueOf(this.ProfileInt));
            rootElement.appendChild(el);

            // <SAMPLE><DEPTH>value</DEPTH></SAMPLE>
            if (this.ProfileInt != null) {
                if (this.Profile != null){
                    List<String> DepthProfile = new ArrayList<>();
                    int index = 0;
                    while (index < this.Profile.length()) {
                        DepthProfile.add(this.Profile.substring(index, Math.min(index + 12,this.Profile.length())));
                        index += 12;
                    }
                    // Get depths from strings [001100000000] = 1.1m
                    List<Double> Depths = new ArrayList<>();
                    for(String s: DepthProfile){
                        Depths.add(Double.valueOf(s.substring(0,3) + "." + s.charAt(3)));
                    }
                    for(Double d: Depths){
                        el = doc.createElement("SAMPLE");
                        Element el_sub = doc.createElement("DEPTH");
                        el_sub.setTextContent(String.format("%.1f", d));
                        el.appendChild(el_sub);
                        rootElement.appendChild(el);
                    }
                }
            }

            // Get xml string
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            return writer.getBuffer().toString();

        } catch (Exception e){
            if (ERROR) Log.e("DivingLogDive.toDLD(): ", e.toString());
            return "";
        }
    }


    public void addTank(Document doc, Element parent, DivingLogTank t){

        Element el;
        CDATASection cdata;

        // Cylinder name (CDATA!)
        // --> NA --> Leave empty
        el = doc.createElement("CYLINDERNAME");
        cdata = doc.createCDATASection("");
        el.appendChild(cdata);
        parent.appendChild(el);

        // Cylinder description (CDATA!)
        el = doc.createElement("CYLINDERDESCRIPTION");
        if (t.Tanktype  != null) {
            if(t.Tanktype < TankType.size()) {
                cdata = doc.createCDATASection(TankType.get(t.Tanktype));
                el.appendChild(cdata);
            }
        }
        parent.appendChild(el);

        // Double tank?
        el = doc.createElement("DBLTANK");
        if (t.DblTank != null)
            el.setTextContent(String.valueOf(t.DblTank));
        parent.appendChild(el);

        // Cylinder size [l]
        el = doc.createElement("CYLINDERSIZE");
        if (t.Tanksize != null)
            el.setTextContent(String.format("%.2f", t.Tanksize));
        parent.appendChild(el);

        // Cylinder start pressure [bar]
        el = doc.createElement("CYLINDERSTARTPRESSURE");
        if (t.PresS != null)
            el.setTextContent(String.format("%.2f", t.PresS));
        parent.appendChild(el);

        // Cylinder end pressure [bar]
        el = doc.createElement("CYLINDERENDPRESSURE");
        if (t.PresE != null)
            el.setTextContent(String.format("%.2f", t.PresE));
        parent.appendChild(el);

        // Working pressure [bar]
        el = doc.createElement("WORKINGPRESSURE");
        if (t.PresW != null)
            el.setTextContent(String.format("%.2f", t.PresW));
        parent.appendChild(el);

        // O2 percentage
        el = doc.createElement("O2PCT");
        if (t.O2 != null)
            el.setTextContent(String.format("%.2f", t.O2));
        parent.appendChild(el);

        // H2 percentage
        el = doc.createElement("HEPCT");
        if (t.He != null)
            el.setTextContent(String.format("%.2f", t.He));
        parent.appendChild(el);
    }

}
