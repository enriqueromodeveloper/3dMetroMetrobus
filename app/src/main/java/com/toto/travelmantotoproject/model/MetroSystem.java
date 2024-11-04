package com.toto.travelmantotoproject.model;

import java.util.List;

public class MetroSystem extends TransportingSystem{

    public MetroSystem() {
        super();
    }

    public List<TransportingLine> getLines() {
        return lines;
    }

    public void setLines(List<TransportingLine> lines) {
        this.lines = lines;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
