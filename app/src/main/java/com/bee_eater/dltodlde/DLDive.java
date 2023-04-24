package com.bee_eater.dltodlde;

import java.lang.reflect.Field;

public class DLDive {
    public Integer ID;
    public Integer Number;
    public String Divedate;
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
    public Integer Tanktype;
    public Double Tanksize;
    public Double PresS;
    public Double PresE;
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
    public Double PresW;
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
    public Double O2;
    public Double He;
    public Integer DblTank;
    public Integer SupplyType;
    public Double MinPPO2;
    public Double MaxPPO2;
    public Integer ShopID;
    public Integer TripID;
    public Integer utcOffset;
    public Double DesaturationTime;
    public Double NoFlyTime;
    public Double ScrubberTime;

    public void setMemberByName(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(name);
        field.set(this,value);
    }

    public String toString() {
        return "{ \"ID\": " + String.valueOf(this.ID) + "," +
                " \"Number\": " + String.valueOf(this.Number) + "," +
                " \"Divedate\": \"" + this.Divedate + "\"," +
                " \"Entrytime\": \"" + this.Entrytime + "\"," +
                " \"Surfint\": \"" + this.Surfint + "\"," +
                " \"Country\": \"" + this.Country + "\"," +
                " \"CountryID\": " + String.valueOf(this.CountryID) + "," +
                " \"City\": \"" + this.City + "\"," +
                " \"CityID\": " + String.valueOf(this.CityID) + "," +
                " \"Place\": \"" + this.Place + "\"," +
                " \"PlaceID\": " + String.valueOf(this.PlaceID) + "," +
                " \"Divetime\": " + String.valueOf(this.Divetime) + "," +
                " \"Depth\": " + String.valueOf(this.Depth) + "," +
                " \"Buddy\": \"" + this.Buddy + "\"," +
                " \"BuddyIDs\": \"" + this.BuddyIDs + "\"," +
                " \"Signature\": \"" + this.Signature + "\"," +
                " \"Comments\": \"" + this.Comments + "\"," +
                " \"Water\": " + String.valueOf(this.Water) + "," +
                " \"Entry\": " + String.valueOf(this.Entry) + "," +
                " \"Divetype\": \"" + this.Divetype + "\"," +
                " \"Tanktype\": " + String.valueOf(this.Tanktype) + "," +
                "}";
    }

    public String toDLD(){
        return "";
    }

}
