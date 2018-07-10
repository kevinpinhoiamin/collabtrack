package br.senai.collabtrack.domain;

/**
 * Created by kevin on 07/06/17.
 */
@org.parceler.Parcel
public class Monitor {

    private long id;
    private String nome;
    private long celular;
    private String token;
    private String tokenAutenticacao;

    private transient long rowid;
    private boolean desabilitado;
    private boolean marcado;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public long getCelular() {
        return celular;
    }
    public void setCelular(long celular) {
        this.celular = celular;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getTokenAutenticacao() {
        return tokenAutenticacao;
    }
    public void setTokenAutenticacao(String tokenAutenticacao) {
        this.tokenAutenticacao = tokenAutenticacao;
    }
    public long getRowid() {
        return rowid;
    }
    public void setRowid(long rowid) {
        this.rowid = rowid;
    }
    public boolean isDesabilitado() {
        return desabilitado;
    }
    public void setDesabilitado(boolean desabilitado) {
        this.desabilitado = desabilitado;
    }
    public boolean isMarcado() {
        return marcado;
    }
    public void setMarcado(boolean marcado) {
        this.marcado = marcado;
    }

    @Override
    public String toString() {
        return this.getNome();
    }
}
