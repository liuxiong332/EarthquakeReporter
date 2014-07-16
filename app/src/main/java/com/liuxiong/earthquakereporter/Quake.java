package com.liuxiong.earthquakereporter;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuxi_000 on 2014/7/16.
 */
public class Quake {
    private Date date;
    private String details;
    private Location location;
    private double magnitude;
    private String link;

    public Quake(Date _d, String _det, Location _loc, double _mag, String _link) {
        date = _d;
        details = _det;
        location = _loc;
        magnitude = _mag;
        link = _link;
    }
    public Date getDate() { return date;}
    public String getDetails() { return details; }
    public Location getLocation() { return location; }
    public double getMagnitude() { return magnitude; }
    public String getLink() { return link; }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("HH.mm");
        return format.format(date) + ": " + magnitude + "  " + details;
    }
}
