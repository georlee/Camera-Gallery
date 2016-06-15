package com.george.assignment3cameragalleryapp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.location.Location;
import android.media.ExifInterface;

/**
 * Created by George on 2016-02-02.
 */
public class gpstag {



    public void MarkGeoTagImage(String imagePath, Location location)
    {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,
                    gpstagHelper.convert(location.getLatitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
                    gpstagHelper.latitudeRef(location.getLatitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,
                    gpstagHelper.convert(location.getLongitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
                    gpstagHelper.longitudeRef(location.getLongitude()));
            SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            exif.setAttribute(ExifInterface.TAG_DATETIME,
                    fmt_Exif.format(new Date(location.getTime())));
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location readGeoTagImage(String imagePath)
    {
        Location loc = new Location("");
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            float [] latlong = new float[2] ;
            if(exif.getLatLong(latlong)){
                loc.setLatitude(latlong[0]);
                loc.setLongitude(latlong[1]);
            }
            String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
            SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            loc.setTime(fmt_Exif.parse(date).getTime());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return loc;
    }
}



