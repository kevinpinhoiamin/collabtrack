package br.com.senai.colabtrack.domain;

/**
 * Created by kevin on 8/11/17.
 */
@org.parceler.Parcel
public class Localizacao {

    private long id;
    private double latitude;
    private double longitude;
    private String data;
    private Monitorado monitorado;

    private int cor;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
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
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public Monitorado getMonitorado() {
        return monitorado;
    }
    public void setMonitorado(Monitorado monitorado) {
        this.monitorado = monitorado;
    }

    public int getCor() {
        return cor;
    }
    public void setCor(int cor) {
        this.cor = cor;
    }

}
