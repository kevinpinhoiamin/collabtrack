package br.senai.collabtrack.domain.to;

import br.senai.collabtrack.domain.Monitorado;

/**
 * Created by lucas on 13/03/2018.
 */

public class RespostaTO {
    private long id;
    private boolean resposta;
    private int idMinitor;
    private String data;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isResposta() {
        return resposta;
    }

    public void setResposta(boolean resposta) {
        this.resposta = resposta;
    }

    public int getIdMinitor() {
        return idMinitor;
    }

    public void setIdMinitor(int idMinitor) {
        this.idMinitor = idMinitor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
