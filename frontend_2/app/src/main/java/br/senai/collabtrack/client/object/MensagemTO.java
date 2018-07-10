package br.senai.collabtrack.client.object;

/**
 * Created by ezs on 03/10/2017.
 */

public class MensagemTO {

    private Long id;
    private Long idMonitorado;
    private Long idMonitor;
    private boolean resposta;
    private String data;

    public MensagemTO(Long id, boolean resposta, String data){
        this.id = id;
        this.resposta = resposta;
        this.data = data;
    }

    public MensagemTO(boolean resposta, String data){
        this.resposta = resposta;
        this.data = data;
    }

    public MensagemTO(Long id, Long idMonitorado, Long idMonitor, boolean resposta){
        this.id = id;
        this.idMonitorado = idMonitorado;
        this.idMonitor = idMonitor;
        this.resposta = resposta;
    }

    public MensagemTO(Long idMonitorado, Long idMonitor, String data, boolean resposta){
        this.id = -1L;
        this.idMonitorado = idMonitorado;
        this.idMonitor = idMonitor;
        this.data = data;
        this.resposta = resposta;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getIdMonitor() {

        return idMonitor;
    }

    public void setIdMonitor(Long idMonitor) {
        this.idMonitor = idMonitor;
    }

    public boolean isResposta() {
        return resposta;
    }

    public void setResposta(boolean resposta) {
        this.resposta = resposta;
    }

    public Long getIdMonitorado() {
        return idMonitorado;
    }

    public void setIdMonitorado(Long idMonitorado) {
        this.idMonitorado = idMonitorado;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String toString(){
        return String.format("{\"id\":%s, \"idMonitor\":%s, \"resposta\":%s, \"data\":\"%s\"}", idMonitorado, idMonitor, resposta, data);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + idMonitor.hashCode() + idMonitorado.hashCode() + String.valueOf(resposta).hashCode() + data.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        MensagemTO m = (MensagemTO) obj;
        return (obj != null && m instanceof MensagemTO &&
                m.getId().equals(id) &&
                m.getIdMonitorado().equals(idMonitorado) &&
                m.getIdMonitor().equals(idMonitor) &&
                m.getData().equals(data));
    }
}
