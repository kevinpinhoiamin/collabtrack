package br.senai.collabtrack.client.object;

/**
 * Created by ezs on 25/07/2017.
 */

public class Audio {

    private Long id;
    private boolean resposta;

    public Audio(){ }

    public Audio(Long id){
        this.id = id;
    }

    public Audio(Long id, boolean resposta){
        this.id = id;
        this.resposta = resposta;
    }

    public Long getId(){
        return id;
    }

    public boolean getResposta(){
        return this.resposta;
    }

    public void setResposta(boolean resposta){
        this.resposta = resposta;
    }

    @Override
    public String toString() {
        return "{\"id\":" + id + ", \"resposta\":" + resposta + "}";
    }

    @Override
    public boolean equals(Object obj) {
        Audio audio = (Audio) obj;
        return (audio.getId() == this.id && audio.getResposta() == this.resposta);
    }

    @Override
    public int hashCode() {
        return String.valueOf(id + (resposta ? 1 : 0)).hashCode();
    }
}
