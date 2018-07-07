package br.com.senai.colabtrack.domain.to;

import java.util.List;

import br.com.senai.colabtrack.domain.AreaSegura;
import br.com.senai.colabtrack.domain.AreaSeguraMonitorado;
import br.com.senai.colabtrack.domain.Monitor;

/**
 * Created by kevin on 07/06/17.
 */

public class AreaSeguraTO {

    private AreaSegura areaSegura;
    private Monitor monitor;
    private List<AreaSeguraMonitorado> areaSeguraMonitorados;

    public AreaSeguraTO(AreaSegura areaSegura, Monitor monitor, List<AreaSeguraMonitorado> areaSeguraMonitorados){
        this.areaSegura = areaSegura;
        this.monitor = monitor;
        this.areaSeguraMonitorados = areaSeguraMonitorados;
    }

    public AreaSegura getAreaSegura() {
        return areaSegura;
    }
    public void setAreaSegura(AreaSegura areaSegura) {
        this.areaSegura = areaSegura;
    }
    public Monitor getMonitor() {
        return monitor;
    }
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }
    public List<AreaSeguraMonitorado> getAreaSeguraMonitorados() {
        return areaSeguraMonitorados;
    }
    public void setAreaSeguraMonitorados(List<AreaSeguraMonitorado> areaSeguraMonitorados) {
        this.areaSeguraMonitorados = areaSeguraMonitorados;
    }

}
