package br.com.senai.collabtrack.to;

public class FirebaseTO {
	
//	public static final String ACAO_INICIAR_MONITORAMENTO_START = "START";
//	public static final String ACAO_INICIAR_MONITORAMENTO_STOP = "STOP";
	
//	public static final int ACAO_INICIAR_MONITORAMENTO = 0;
//	public static final int ACAO_MENSAGEM = 1;
//	public static final int ACAO_AREA_SEGURA = 3;
//	public static final int ACAO_ATUALIZAR_STATUS = 4;
//	public static final int ACAO_AUDIO = 5;
//	public static final int ACAO_RESPOSTA = 6;
//	public static final int ACAO_DIVERSOS = 7;
//	public static final int ACAO_CADASTRO_MONITORADO = 8;
//	public static final int ACAO_EDICAO_MONITORADO = 9;
//	public static final int ACAO_REMOCAO_MONITORADO = 10;
//	public static final int ACAO_ALTERAR_FOTO_PERFIL = 11;
//	public static final int ACAO_ALTERAR_DADOS_MONITOR = 12;
	
	private int acao;
	private long id;
	private String mensagem;
	private String json;
	private Object objeto;

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

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public Object getObjeto() {
		return objeto;
	}

	public void setObjeto(Object objeto) {
		this.objeto = objeto;
	}

}
