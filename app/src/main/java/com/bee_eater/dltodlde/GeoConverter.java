package com.bee_eater.dltodlde;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoConverter {

    private final static Pattern DMS_PATTERN = Pattern.compile(
            "(-?)([0-9]{1,2})°([0-5]?[0-9])'([0-5]?[0-9].*?)\"([NS])\\s*" +
                    "(-?)([0-1]?[0-9]{1,2})°([0-5]?[0-9])'([0-5]?[0-9].*?)\"([EW])");

    private double toDouble(Matcher m, int offset) {
        int sign = "".equals(m.group(1 + offset)) ? 1 : -1;
        double degrees = Double.parseDouble(m.group(2 + offset));
        double minutes = Double.parseDouble(m.group(3 + offset));
        double seconds = Double.parseDouble(m.group(4 + offset));
        int direction = "NE".contains(m.group(5 + offset)) ? 1 : -1;

        return sign * direction * (degrees + minutes / 60 + seconds / 3600);
    }

    public double[] convert(String dms) {
        Matcher m = DMS_PATTERN.matcher(dms.trim());

        if (m.matches()) {
            double latitude = toDouble(m, 0);
            double longitude = toDouble(m, 5);

            if ((Math.abs(latitude) > 90) || (Math.abs(longitude) > 180)) {
                throw new NumberFormatException("Invalid latitude or longitude");
            }

            return new double[] { latitude, longitude };
        } else {
            throw new NumberFormatException(
                    "Malformed degrees/minutes/seconds/direction coordinates");
        }
    }

}
