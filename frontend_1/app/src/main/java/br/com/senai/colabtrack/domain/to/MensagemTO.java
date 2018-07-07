package br.com.senai.colabtrack.domain.to;

/**
 * Created by Lucas Matheus Nunes on 08/08/2017.
 */

public class MensagemTO {
    private int id;
    private String mensagem;
    private int tipo;
    private String data;
    private int resposta;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
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
}
