package br.com.senai.colabtrack.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kevin on 21/06/17.
 */
@org.parceler.Parcel
public class AreaSeguraMonitorado {
    private AreaSeguraMonitoradoPK primaryKey = new AreaSeguraMonitoradoPK();
    private boolean ativa;

    public AreaSeguraMonitoradoPK getPrimaryKey() {
        return primaryKey;
    }
    public void setPrimaryKey(AreaSeguraMonitoradoPK pk) {
        this.primaryKey = pk;
    }
    public boolean isAtiva() {
        return ativa;
    }
    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
    public AreaSegura getAreaSegura(){
        return primaryKey.getAreaSegura();
    }
    public void setAreaSegura(AreaSegura areaSegura){
        primaryKey.setAreaSegura(areaSegura);
    }
    public Monitorado getMonitorado(){
        return primaryKey.getMonitorado();
    }
    public void setMonitorado(Monitorado monitorado){
        primaryKey.setMonitorado(monitorado);
    }
}
