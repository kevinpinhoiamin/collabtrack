package br.senai.collabtrack.domain;

import android.os.Parcelable;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kevin on 18/05/17.
 */
@org.parceler.Parcel
public class AreaSegura {

    private long id;
    private String nome;
    private int cor;
    private int corBorda;
    private int raio;
    private double latitude;
    private double longitude;

    private transient long rowid;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public int getCor() {
        return cor;
    }
    public void setCor(int cor) {
        this.cor = cor;
    }
    public int getCorBorda() {
        return corBorda;
    }
    public void setCorBorda(int corBorda) {
        this.corBorda = corBorda;
    }
    public int getRaio() {
        return raio;
    }
    public void setRaio(int raio) {
        this.raio = raio;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getRowid() {
        return rowid;
    }
    public void setRowid(long rowid) {
        this.rowid = rowid;
    }
}
