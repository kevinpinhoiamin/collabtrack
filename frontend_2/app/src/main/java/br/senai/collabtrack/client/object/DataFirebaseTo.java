package br.senai.collabtrack.client.object;

/**
 * Created by ezs on 28/08/2017.
 */

public class DataFirebaseTo {

    public static final int ACAO_STATUS = 0;
    public static final int ACAO_MENSAGEM = 1;
    public static final int ACAO_AUDIO = 5;

    private String mensagem;
    private int acao;
    private long id;

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getAcao() {
        return acao;
    }

    public void setAcao(int acao) {
        this.acao = acao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}