package br.senai.collabtrack.service.mapa;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

import br.senai.collabtrack.domain.Localizacao;

/**
 * Created by kevin on 8/28/17.
 */

public class MapMonitorado {

    private Localizacao localizacao;
    private Circle circle;
    private Marker marker;
    private int color;

    public Localizacao getLocalizacao() {
        return localizacao;
    }
    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }
    public Circle getCircle() {
        return circle;
    }
    public void setCircle(Circle circle) {
        this.circle = circle;
    }
    public Marker getMarker() {
        return marker;
    }
    public void setMarker(Marker marker) {
        this.marker = marker;
    }
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

}
