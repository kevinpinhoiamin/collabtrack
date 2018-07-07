package br.com.senai.colabtrack.service.mapa;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import br.com.senai.colabtrack.domain.AreaSegura;
import br.com.senai.colabtrack.domain.AreaSeguraMonitorado;

/**
 * Created by kevin on 11/05/17.
 */

public class MapArea {

    private final Circle circle;
    private final Marker marker;
    private final AreaSegura areaSegura;
    private final List<AreaSeguraMonitorado> areaSeguraMonitorados;

    public MapArea(Circle circle, Marker marker, AreaSegura areaSegura, List<AreaSeguraMonitorado> areaSeguraMonitorados){
        this.circle = circle;
        this.marker = marker;
        this.areaSegura = areaSegura;
        this.areaSeguraMonitorados = areaSeguraMonitorados;
    }

    public Circle getCircle() {
        return circle;
    }
    public Marker getMarker() {
        return marker;
    }
    public AreaSegura getAreaSegura(){
        return areaSegura;
    }
    public List<AreaSeguraMonitorado> getAreaSeguraMonitorados(){
        return this.areaSeguraMonitorados;
    }

}
