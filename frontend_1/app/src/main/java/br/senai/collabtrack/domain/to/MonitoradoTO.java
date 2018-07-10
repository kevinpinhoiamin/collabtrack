package br.senai.collabtrack.domain.to;

import java.util.List;

import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.domain.Monitorado;
import br.senai.collabtrack.domain.Status;

/**
 * Created by kevin on 10/16/17.
 */
@org.parceler.Parcel
public class MonitoradoTO {

    private Monitorado monitorado;
    private int cor;
    private Monitor monitorPrincipal;
    private Monitor monitorAutenticado;
    private List<Monitor> monitores;
    private Status status;

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
    public Monitor getMonitorPrincipal() {
        return monitorPrincipal;
    }
    public void setMonitorPrincipal(Monitor monitorPrincipal) {
        this.monitorPrincipal = monitorPrincipal;
    }
    public Monitor getMonitorAutenticado() {
        return monitorAutenticado;
    }
    public void setMonitorAutenticado(Monitor monitorAutenticado) {
        this.monitorAutenticado = monitorAutenticado;
    }
    public List<Monitor> getMonitores() {
        return monitores;
    }
    public void setMonitores(List<Monitor> monitores) {
        this.monitores = monitores;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

}
