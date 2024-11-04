package com.toto.travelmantotoproject.model;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

public class TransportingLine {

   private List<Station> stations;
   private String hexColor;
   private Paint paintColor;

   public TransportingLine(String hexColor,Station ... varArgStations){
       this.hexColor = hexColor;
       paintColor  = new Paint();
       paintColor.setColor(Color.parseColor(hexColor));
       stations = new ArrayList<Station>();
       for (Station station:varArgStations) {
           stations.add(station);
       }

   }


    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public Paint getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(Paint paintColor) {
        this.paintColor = paintColor;
    }
}
