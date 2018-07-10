package br.senai.collabtrack.domain.to;

/**
 * Created by kevin on 8/23/17.
 */
@org.parceler.Parcel
public class DataFirebaseTO {

    transient public static final int ACAO_MENSAGEM = 1;
    transient public static final int ACAO_AREA_SEGURA = 3;
    transient public static final int ACAO_ATUALIZAR_STATUS = 4;
    transient public static final int ACAO_AUDIO = 5;
    transient public static final int ACAO_RESPOSTA = 6;
    transient public static final int ACAO_DIVERSOS = 7;
    transient public static final int ACAO_CADASTRO_MONITORADO = 8;
    public static final int ACAO_EDICAO_MONITORADO = 9;
    public static final int ACAO_REMOCAO_MONITORADO = 10;

    private String mensagem;
    private int acao;
    private long id;
    private String json;

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
    public String getJson() {
        return json;
    }
    public void setJson(String json) {
        this.json = json;
    }

}
