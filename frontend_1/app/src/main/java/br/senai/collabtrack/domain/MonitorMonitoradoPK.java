package br.senai.collabtrack.domain;

/**
 * Created by kevin on 23/06/17.
 */
@org.parceler.Parcel
public class MonitorMonitoradoPK {
    private Monitor monitor;
    private Monitorado monitorado;

    public Monitor getMonitor() {
        return monitor;
    }
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }
    public Monitorado getMonitorado() {
        return monitorado;
    }
    public void setMonitorado(Monitorado monitorado) {
        this.monitorado = monitorado;
    }
}
