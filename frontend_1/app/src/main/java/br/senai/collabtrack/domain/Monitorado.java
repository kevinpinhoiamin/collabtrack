package br.senai.collabtrack.domain;

/**
 * Created by kevin on 21/06/17.
 */
@org.parceler.Parcel
public class Monitorado {
    private long id;
    private String nome;
    private long celular;

    private transient long rowid;

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

    public long getRowid() {
        return rowid;
    }
    public void setRowid(long rowid) {
        this.rowid = rowid;
    }
}
