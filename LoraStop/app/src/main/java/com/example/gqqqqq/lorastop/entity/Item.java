package com.example.gqqqqq.lorastop.entity;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

public class Item {
    private String iName;
    private String iNum;
    private String iEmpty;
    private LatLng latLng;
    private Marker marker;

    public Item(String iName, String iNum, String iEmpty, LatLng latLng, Marker marker) {
        this.iName = iName;
        this.iNum = iNum;
        this.iEmpty = iEmpty;
        this.latLng = latLng;
        this.marker = marker;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Item() {
    }

    public String getiName() {
        return iName;
    }

    public void setiName(String iName) {
        this.iName = iName;
    }

    public String getiNum() {
        return iNum;
    }

    public void setiNum(String iNum) {
        this.iNum = iNum;
    }

    public String getiEmpty() {
        return iEmpty;
    }

    public void setiEmpty(String iEmpty) {
        this.iEmpty = iEmpty;
    }
}
