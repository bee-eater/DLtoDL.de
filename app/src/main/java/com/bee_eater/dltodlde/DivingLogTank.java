package com.bee_eater.dltodlde;

import java.lang.reflect.Field;

public class DivingLogTank {
    public Integer ID;
    public Integer LogID;
    public Integer TankID;
    public Integer SortOrd;
    public Integer Tanktype;
    public Double Tanksize;
    public Double PresS;
    public Double PresE;
    public Double PresW;
    public Double O2;
    public Double He;
    public Integer DblTank;
    public Integer SupplyType;
    public Double MinPPO2;
    public Double MaxPPO2;
    public Double ScrubberTime;

    public String UUID;
    public String Updated;

    public void setMemberByName(String name, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(name);
        field.set(this, value);
    }
}
