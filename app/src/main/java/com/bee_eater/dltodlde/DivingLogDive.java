package com.bee_eater.dltodlde;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

public class DivingLogDive {

    // Other stuff used in app
    public Boolean isSelected = Boolean.FALSE;

    // DivingLog parameters read from sql
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
                " \"utcOffset\": " + String.valueOf(this.utcOffset) + "," +
                " \"DesaturationTime\": " + String.valueOf(this.DesaturationTime) + "," +
                " \"NoFlyTime\": " + String.valueOf(this.NoFlyTime) + "," +
                " \"ScrubberTime\": " + String.valueOf(this.ScrubberTime) +
                "}";
    }

    public String toDLD(){
        return "";
    }

}
