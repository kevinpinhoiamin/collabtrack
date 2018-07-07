package br.com.senai.colabtrack.domain;

/**
 * Created by kevin on 23/06/17.
 */
@org.parceler.Parcel
public class MonitorMonitorado {

    private MonitorMonitoradoPK primaryKey = new MonitorMonitoradoPK();
    private boolean principal;
    private boolean ativo;
    private boolean emMonitoramento;
    private int cor;

    public MonitorMonitoradoPK getPrimaryKey() {
        return primaryKey;
    }
    public void setPrimaryKey(MonitorMonitoradoPK pk) {
        this.primaryKey = pk;
    }
    public boolean isPrincipal() {
        return principal;
    }
    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
    public boolean isAtivo() {
        return ativo;
    }
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    public Monitor getMonitor(){
        return primaryKey.getMonitor();
    }
    public void setMonitor(Monitor monitor){
        primaryKey.setMonitor(monitor);
    }
    public Monitorado getMonitorado(){
        return primaryKey.getMonitorado();
    }
    public void setMonitorado(Monitorado monitorado){
        primaryKey.setMonitorado(monitorado);
    }
    public boolean isEmMonitoramento() {
        return emMonitoramento;
    }
    public void setEmMonitoramento(boolean emMonitoramento) {
        this.emMonitoramento = emMonitoramento;
    }
    public int getCor() {
        return cor;
    }
    public void setCor(int cor) {
        this.cor = cor;
    }
    
}
