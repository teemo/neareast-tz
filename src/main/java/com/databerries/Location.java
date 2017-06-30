package com.databerries;

import com.databerries.tree.XYZPoint;

public class Location extends XYZPoint {
    private final String timezone;

    private Location(Double latitude, Double longitude, String timezone) {
        super(6371, latitude, longitude);
        this.timezone = timezone;
    }

    public String getTimezone() {
        return timezone;
    }


    @Override
    public String toString() {
        return "Location{" +
                "timezone='" + timezone + '\'' +
                "point='" + super.toString() + '\'' +
                '}';
    }

    public static Location create(Double latitude, Double longitude, String timezone) {
        return new Location(latitude, longitude, timezone);
    }

    public static Location create(double latitude, double longitude) {
        return create(latitude, longitude, null);
    }
}
