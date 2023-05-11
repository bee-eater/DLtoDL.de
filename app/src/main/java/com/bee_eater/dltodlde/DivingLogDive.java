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
    public LocalDateTime DiveDateDT;

    // DivingLog parameters read from sql
    public Integer number;
    public String divedate;
    public String entrytime;
    public String surfint;
    public String country;
    public Integer countryid;
    public String city;
    public Integer cityid;
    public String place;
    public Integer placeid;
    public Double divetime;
    public Double depth;
    public String buddy;
    public String buddyids;
    public String signature;
    public String comments;
    public Integer water;
    public Integer entry;
    public String divetype;
    public String gas;
    public String weather;
    public String uwcurrent;
    public String surface;
    public Integer visibility;
    public Double airtemp;
    public Double watertemp;
    public Double weight;
    public Integer deco;
    public String decostops;
    public Integer rep;
    public String altitude;
    public String divesuit;
    public String computer;
    public Integer profileint;
    public String profile;
    public String usedequip;
    public String profile2;
    public String profile3;
    public Double depthavg;
    public String uuid;
    public String updated;
    public String profile4;
    public String profile5;
    public Integer repnumber;
    public String vishor;
    public String visver;
    public String cns;
    public String pgstart;
    public String pgend;
    public String divemaster;
    public String boat;
    public Integer rating;
    public Integer shopid;
    public Integer tripid;
    public Integer utcoffset;
    public Double desaturationtime;
    public Double noflytime;
    public ArrayList<DivingLogTank> Tanks = new ArrayList<>();

    public DivingLogPlace PlaceInfo;

    @NonNull
    @Override
    public String toString() {
        return "{ \"ID\": " + String.valueOf(this.id) + "," +
                " \"Number\": " + String.valueOf(this.number) + "," +
                " \"Divedate\": " + ((this.divedate == null) ? "null" : ("\"" + String.valueOf(this.divedate) + "\",")) +
                " \"Entrytime\": " + ((this.entrytime == null) ? "null" : ("\"" + String.valueOf(this.entrytime) + "\"")) + "," +
                " \"Surfint\": " + ((this.surfint == null) ? "null" : ("\"" + String.valueOf(this.surfint) + "\"")) + "," +
                " \"Country\": " + ((this.country == null) ? "null" : ("\"" + String.valueOf(this.country) + "\"")) + "," +
                " \"CountryID\": " + String.valueOf(this.countryid) + "," +
                " \"City\": " + ((this.city == null) ? "null" : ("\"" + String.valueOf(this.city) + "\"")) + "," +
                " \"CityID\": " + String.valueOf(this.cityid) + "," +
                " \"Place\": " + ((this.place == null) ? "null" : ("\"" + String.valueOf(this.place) + "\"")) + "," +
                " \"PlaceID\": " + String.valueOf(this.placeid) + "," +
                " \"Divetime\": " + String.valueOf(this.divetime) + "," +
                " \"Depth\": " + String.valueOf(this.depth) + "," +
                " \"Buddy\": " + ((this.buddy == null) ? "null" : ("\"" + String.valueOf(this.buddy) + "\"")) + "," +
                " \"BuddyIDs\": " + ((this.buddyids == null) ? "null" : ("\"" + String.valueOf(this.buddyids) + "\"")) + "," +
                " \"Signature\": " + ((this.signature == null) ? "null" : ("\"" + String.valueOf(this.signature) + "\"")) + "," +
                " \"Comments\": " + ((this.comments == null) ? "null" : ("\"" + String.valueOf(this.comments) + "\"")) + "," +
                " \"Water\": " + String.valueOf(this.water) + "," +
                " \"Entry\": " + String.valueOf(this.entry) + "," +
                " \"Divetype\": " + ((this.divetype == null) ? "null" : ("\"" + String.valueOf(this.divetype) + "\"")) + "," +
                " \"Tanktype\": " + String.valueOf(this.tanktype) + "," +
                " \"Tanksize\": " + String.valueOf(this.tanksize) + "," +
                " \"PresS\": " + String.valueOf(this.press) + "," +
                " \"PresE\": " + String.valueOf(this.prese) + "," +
                " \"Gas\": " + ((this.gas == null) ? "null" : ("\"" + String.valueOf(this.gas) + "\"")) + "," +
                " \"Weather\": " + ((this.weather == null) ? "null" : ("\"" + String.valueOf(this.weather) + "\"")) + "," +
                " \"UWCurrent\": " + ((this.uwcurrent == null) ? "null" : ("\"" + String.valueOf(this.uwcurrent) + "\"")) + "," +
                " \"Surface\": " + ((this.surface == null) ? "null" : ("\"" + String.valueOf(this.surface) + "\"")) + "," +
                " \"Visibility\": " + String.valueOf(this.visibility) + "," +
                " \"Airtemp\": " + String.valueOf(this.airtemp) + "," +
                " \"Watertemp\": " + String.valueOf(this.watertemp) + "," +
                " \"Weight\": " + String.valueOf(this.weight) + "," +
                " \"Deco\": " + String.valueOf(this.deco) + "," +
                " \"Decostops\": " + ((this.decostops == null) ? "null" : ("\"" + String.valueOf(this.decostops) + "\"")) + "," +
                " \"Rep\": " + String.valueOf(this.rep) + "," +
                " \"Altitude\": " + ((this.altitude == null) ? "null" : ("\"" + String.valueOf(this.altitude) + "\"")) + "," +
                " \"Divesuit\": " + ((this.divesuit == null) ? "null" : ("\"" + String.valueOf(this.divesuit) + "\"")) + "," +
                " \"Computer\": " + ((this.computer == null) ? "null" : ("\"" + String.valueOf(this.computer) + "\"")) + "," +
                " \"ProfileInt\": " + String.valueOf(this.profileint) + "," +
                " \"Profile\": " + ((this.profile == null) ? "null" : ("\"" + String.valueOf(this.profile) + "\"")) + "," +
                " \"UsedEquip\": " + ((this.usedequip == null) ? "null" : ("\"" + String.valueOf(this.usedequip) + "\"")) + "," +
                " \"PresW\": " + String.valueOf(this.presw) + "," +
                " \"Profile2\": " + ((this.profile2 == null) ? "null" : ("\"" + String.valueOf(this.profile2) + "\"")) + "," +
                " \"Profile3\": " + ((this.profile3 == null) ? "null" : ("\"" + String.valueOf(this.profile3) + "\"")) + "," +
                " \"DepthAvg\": " + String.valueOf(this.depthavg) + "," +
                " \"UUID\": " + ((this.uuid == null) ? "null" : ("\"" + String.valueOf(this.uuid) + "\"")) + "," +
                " \"Updated\": " + ((this.updated == null) ? "null" : ("\"" + String.valueOf(this.updated) + "\"")) + "," +
                " \"Profile4\": " + ((this.profile4 == null) ? "null" : ("\"" + String.valueOf(this.profile4) + "\"")) + "," +
                " \"Profile5\": " + ((this.profile5 == null) ? "null" : ("\"" + String.valueOf(this.profile5) + "\"")) + "," +
                " \"RepNumber\": " + String.valueOf(this.repnumber) + "," +
                " \"VisHor\": " + ((this.vishor == null) ? "null" : ("\"" + String.valueOf(this.vishor) + "\"")) + "," +
                " \"VisVer\": " + ((this.visver == null) ? "null" : ("\"" + String.valueOf(this.visver) + "\"")) + "," +
                " \"CNS\": " + ((this.cns == null) ? "null" : ("\"" + String.valueOf(this.cns) + "\"")) + "," +
                " \"PGStart\": " + ((this.pgstart == null) ? "null" : ("\"" + String.valueOf(this.pgstart) + "\"")) + "," +
                " \"PGEnd\": " + ((this.pgend == null) ? "null" : ("\"" + String.valueOf(this.pgend) + "\"")) + "," +
                " \"Divemaster\": " + ((this.divemaster == null) ? "null" : ("\"" + String.valueOf(this.divemaster) + "\"")) + "," +
                " \"Boat\": " + ((this.boat == null) ? "null" : ("\"" + String.valueOf(this.boat) + "\"")) + "," +
                " \"Rating\": " + String.valueOf(this.rating) + "," +
                " \"O2\": " + String.valueOf(this.o2) + "," +
                " \"He\": " + String.valueOf(this.he) + "," +
                " \"DblTank\": " + String.valueOf(this.dbltank) + "," +
                " \"SupplyType\": " + String.valueOf(this.supplytype) + "," +
                " \"MinPPO2\": " + String.valueOf(this.minppo2) + "," +
                " \"MaxPPO2\": " + String.valueOf(this.maxppo2) + "," +
                " \"ShopID\": " + String.valueOf(this.shopid) + "," +
                " \"TripID\": " + String.valueOf(this.tripid) + "," +
                " \"utcOffset\": " + String.valueOf(this.utcoffset) + "," +
                " \"DesaturationTime\": " + String.valueOf(this.desaturationtime) + "," +
                " \"NoFlyTime\": " + String.valueOf(this.noflytime) + "," +
                " \"ScrubberTime\": " + String.valueOf(this.scrubbertime) +
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
            el.setTextContent(String.valueOf((int)(this.divetime * 60)));
            rootElement.appendChild(el);

            // Surface time [s]
            el = doc.createElement("SURFACETIME");
            int surfint = 0;
            if (this.surfint != null){
                try {
                    String hours = this.surfint.split(":")[0];
                    String minutes = this.surfint.split(":")[1];
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
            if (this.depth != null)
                el.setTextContent(String.format("%.1f", this.depth));
            rootElement.appendChild(el);

            // Depth (avg) [m]
            el = doc.createElement("MEANDEPTH");
            if (this.depthavg != null)
                el.setTextContent(String.format("%.1f", this.depthavg));
            rootElement.appendChild(el);

            // Location (CDATA!)
            el = doc.createElement("LOCATION");
            tmpStr = "";
            if (this.country != null){
                tmpStr += this.country + ", ";
            }
            if (this.city != null){
                tmpStr += this.city;
            }
            cdata = doc.createCDATASection(tmpStr);
            el.appendChild(cdata);
            rootElement.appendChild(el);

            // Site (CDATA!)
            el = doc.createElement("SITE");
            if (this.place != null) {
                cdata = doc.createCDATASection(this.place);
                el.appendChild(cdata);
            }
            rootElement.appendChild(el);

            // Weather (CDATA!)
            el = doc.createElement("WEATHER");
            if (this.weather != null) {
                cdata = doc.createCDATASection(this.weather);
                el.appendChild(cdata);
            }
            rootElement.appendChild(el);

            // Visibility (CDATA!)
            el = doc.createElement("WATERVIZIBILITY");
            if (this.visibility != null) {
                if(this.visibility < WaterVisibility.size()) {
                    cdata = doc.createCDATASection(WaterVisibility.get(this.visibility));
                    el.appendChild(cdata);
                }
            }
            rootElement.appendChild(el);

            // Air temperature
            el = doc.createElement("AIRTEMP");
            if (this.airtemp != null)
                el.setTextContent(String.format("%.1f", this.airtemp));
            rootElement.appendChild(el);

            // Water temp max depth
            el = doc.createElement("WATERTEMPMAXDEPTH");
            if (this.watertemp != null)
                el.setTextContent(String.format("%.1f", this.watertemp));
            rootElement.appendChild(el);

            // Water temp end
            // --> NA

            // Partner (Buddies...) (CDATA!)
            el = doc.createElement("PARTNER");
            if (this.buddy != null) {
                cdata = doc.createCDATASection(this.buddy);
                el.appendChild(cdata);
            }
            rootElement.appendChild(el);

            // Boat name (CDATA!)
            el = doc.createElement("BOATNAME");
            if (this.boat != null) {
                cdata = doc.createCDATASection(this.boat);
                el.appendChild(cdata);
            }
            rootElement.appendChild(el);

            // Weight [kg]
            el = doc.createElement("WEIGHT");
            if (this.weight != null)
                el.setTextContent(String.format("%.2f", this.weight));
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
            if (this.comments != null) {
                cdata = doc.createCDATASection(this.comments);
                el.appendChild(cdata);
            }
            rootElement.appendChild(el);

            // Latitude
            // Longitude
            try {
                GeoConverter geo = new GeoConverter();
                double[] latLon = geo.convert(this.PlaceInfo.lat + " " + this.PlaceInfo.lon);
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
            if (this.profileint != null)
                el.setTextContent(String.valueOf(this.profileint));
            rootElement.appendChild(el);

            // <SAMPLE><DEPTH>value</DEPTH></SAMPLE>
            if (this.profileint != null) {
                if (this.profile != null){
                    List<String> DepthProfile = new ArrayList<>();
                    int index = 0;
                    while (index < this.profile.length()) {
                        DepthProfile.add(this.profile.substring(index, Math.min(index + 12,this.profile.length())));
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
        if (t.tanktype != null) {
            if(t.tanktype < TankType.size()) {
                cdata = doc.createCDATASection(TankType.get(t.tanktype));
                el.appendChild(cdata);
            }
        }
        parent.appendChild(el);

        // Double tank?
        el = doc.createElement("DBLTANK");
        if (t.dbltank != null)
            el.setTextContent(String.valueOf(t.dbltank));
        parent.appendChild(el);

        // Cylinder size [l]
        el = doc.createElement("CYLINDERSIZE");
        if (t.tanksize != null)
            el.setTextContent(String.format("%.2f", t.tanksize));
        parent.appendChild(el);

        // Cylinder start pressure [bar]
        el = doc.createElement("CYLINDERSTARTPRESSURE");
        if (t.press != null)
            el.setTextContent(String.format("%.2f", t.press));
        parent.appendChild(el);

        // Cylinder end pressure [bar]
        el = doc.createElement("CYLINDERENDPRESSURE");
        if (t.prese != null)
            el.setTextContent(String.format("%.2f", t.prese));
        parent.appendChild(el);

        // Working pressure [bar]
        el = doc.createElement("WORKINGPRESSURE");
        if (t.presw != null)
            el.setTextContent(String.format("%.2f", t.presw));
        parent.appendChild(el);

        // O2 percentage
        el = doc.createElement("O2PCT");
        if (t.o2 != null)
            el.setTextContent(String.format("%.2f", t.o2));
        parent.appendChild(el);

        // H2 percentage
        el = doc.createElement("HEPCT");
        if (t.he != null)
            el.setTextContent(String.format("%.2f", t.he));
        parent.appendChild(el);
    }

}
