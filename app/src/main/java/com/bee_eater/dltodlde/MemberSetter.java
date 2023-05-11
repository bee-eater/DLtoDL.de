package com.bee_eater.dltodlde;

import android.util.Log;

import java.io.InvalidObjectException;
import java.lang.reflect.Field;

public class MemberSetter {
    public void setMemberByName(String name, Object value) throws NoSuchFieldException, IllegalAccessException, InvalidObjectException {
        Field field = findUnderlying(getClass(), name.toLowerCase());
        if (field != null) {
            if(value != null) {
                if (field.getType().equals(value.getClass())) {
                    field.set(this, value);
                } else {
                    throw new InvalidObjectException("Found member " + name.toLowerCase() + " of type " + field.getType() + ", got object: " + value.getClass());
                }
            } else {
                field.set(this, null);
            }
        } else {
            Log.e("DIVE", "Field not found: " + name);
        }
    }

    public static Field findUnderlying(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        do {
            try {
                return current.getDeclaredField(fieldName.toLowerCase());
            } catch(Exception e) {}
        } while((current = current.getSuperclass()) != null);
        return null;
    }
}
