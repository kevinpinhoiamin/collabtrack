package br.senai.collabtrack.domain;

import java.util.Date;

/**
 * Created by Lucas Matheus Nunes on 26/07/2017.
 */

public class Mensagem {
    private long id;
    private String mensagem;
    private int tipo;
    private String data;
    private int resposta;
    private Monitorado monitorado;
    private Monitor monitor;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Monitorado getMonitorado() {
        return monitorado;
    }

    public void setMonitorado(Monitorado monitorado) {
        this.monitorado = monitorado;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getResposta() {
        return resposta;
    }

    public void setResposta(int resposta) {
        this.resposta = resposta;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
