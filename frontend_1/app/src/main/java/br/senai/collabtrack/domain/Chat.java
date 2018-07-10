package br.senai.collabtrack.domain;

/**
 * Created by Lucas Matheus Nunes on 27/06/2017.
 */

public class Chat {

    transient public static final int TIPO_AUDIO = 2;
    public static final int TIPO_RESPOSTA = 3;
    public static final int TIPO_STATUS= 4;
    public static final int TIPO_DIVERSOS= 5;
    public static final int TIPO_AREA_SEGURA= 6;
    public static final int TIPO_LOCALIZACAO = 7;

    private int id;
    private int idUsuarioEnvio;
    private int idUsuario;
    private String mensagem;
    private String data;
    private int tipo;
    private int idResposta;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuarioEnvio() {
        return idUsuarioEnvio;
    }

    public void setIdUsuarioEnvio(int idUsuarioEnvio) {
        this.idUsuarioEnvio = idUsuarioEnvio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public static int getTipoAudio() {
        return TIPO_AUDIO;
    }

    public int getIdResposta() {
        return idResposta;
    }

    public void setIdResposta(int idResposta) {
        this.idResposta = idResposta;
    }
}
