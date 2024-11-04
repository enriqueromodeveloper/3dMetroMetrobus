package com.toto.travelmantotoproject.model;

import com.google.android.gms.maps.model.LatLng;

public class Station {

    private StationLogoModel stationLogoModel;
    private StationLogoModel miniStationLogoModel;
    private StationLogoModel tinyStationLogoModel;
    private String lineId;
    private String color;
    private LatLng latLng;
    private String name;

    public Station(String name,LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }


    public StationLogoModel getStationLogoModel() {
        return stationLogoModel;
    }

    public void setStationLogoModel(StationLogoModel stationLogoModel) {
        this.stationLogoModel = stationLogoModel;
    }

    public StationLogoModel getMiniStationLogoModel() {
        return miniStationLogoModel;
    }

    public void setMiniStationLogoModel(StationLogoModel miniStationLogoModel) {
        this.miniStationLogoModel = miniStationLogoModel;
    }

    public StationLogoModel getTinyStationLogoModel() {
        return tinyStationLogoModel;
    }

    public void setTinyStationLogoModel(StationLogoModel tinyStationLogoModel) {
        this.tinyStationLogoModel = tinyStationLogoModel;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
