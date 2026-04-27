package com.example.delestage;

public class ZoneStats {
    private String nomZone;
    private int nombreCoupures;

    public ZoneStats(String nomZone, int nombreCoupures) {
        this.nomZone = nomZone;
        this.nombreCoupures = nombreCoupures;
    }

    public String getNomZone() { return nomZone; }
    public int getNombreCoupures() { return nombreCoupures; }
}