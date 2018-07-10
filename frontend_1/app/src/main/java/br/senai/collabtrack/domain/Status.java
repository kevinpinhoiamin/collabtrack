package br.senai.collabtrack.domain;

import java.util.Date;

/**
 * Created by kevin on 8/8/17.
 */
@org.parceler.Parcel
public class Status {

    private long id;
    private boolean wifi;
    private boolean internetMovel;
    private boolean localizacao;
    private int bateria;
    private String data;
    private Monitorado monitorado;

    private transient boolean ativo;
    private transient boolean principal;
    private transient boolean emMonitoramento;
    private transient int cor;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public boolean isWifi() {
        return wifi;
    }
    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }
    public boolean isInternetMovel() {
        return internetMovel;
    }
    public void setInternetMovel(boolean internetMovel) {
        this.internetMovel = internetMovel;
    }
    public boolean isLocalizacao() {
        return localizacao;
    }
    public void setLocalizacao(boolean localizacao) {
        this.localizacao = localizacao;
    }
    public int getBateria() {
        return bateria;
    }
    public void setBateria(int bateria) {
        this.bateria = bateria;
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

    public boolean isAtivo() {
        return ativo;
    }
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    public boolean isPrincipal() {
        return principal;
    }
    public void setPrincipal(boolean principal) {
        this.principal = principal;
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