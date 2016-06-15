package com.george.assignment3cameragalleryapp;

/**
 * Created by George on 2016-02-02.
 */
class gpstagHelper {
    private static StringBuilder stringbuild = new StringBuilder(20);
    public static String latitudeRef(final double latitude) {
        return latitude < 0.0d ? "S" : "N";
    }

    public static String longitudeRef(final double longitude) {
        return longitude < 0.0d ? "W" : "E";
    }

    public static final String convert(double latitude) {
        latitude = Math.abs(latitude);
        final int degree = (int) latitude;
        latitude *= 60;
        latitude -= degree * 60.0d;
        final int minute = (int) latitude;
        latitude *= 60;
        latitude -= minute * 60.0d;
        final int second = (int) (latitude * 1000.0d);
        stringbuild.setLength(0);
        stringbuild.append(degree);
        stringbuild.append("/1,");
        stringbuild.append(minute);
        stringbuild.append("/1,");
        stringbuild.append(second);
        stringbuild.append("/1000,");
        return stringbuild.toString();
    }
}