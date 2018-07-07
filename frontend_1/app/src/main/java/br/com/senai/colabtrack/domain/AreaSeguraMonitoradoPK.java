package br.com.senai.colabtrack.domain;

/**
 * Created by kevin on 21/06/17.
 */
@org.parceler.Parcel
public class AreaSeguraMonitoradoPK {
    private AreaSegura areaSegura;
    private Monitorado monitorado;

    public AreaSegura getAreaSegura() {
        return areaSegura;
    }
    public void setAreaSegura(AreaSegura areaSegura) {
        this.areaSegura = areaSegura;
    }
    public Monitorado getMonitorado() {
        return monitorado;
    }
    public void setMonitorado(Monitorado monitorado) {
        this.monitorado = monitorado;
    }
}
