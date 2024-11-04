package com.toto.travelmantotoproject.model;

import java.util.ArrayList;
import java.util.List;

public abstract class TransportingSystem {

    protected   List<TransportingLine> lines;
    protected boolean enable;

    public TransportingSystem() {
        this.lines = new ArrayList<>();
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
