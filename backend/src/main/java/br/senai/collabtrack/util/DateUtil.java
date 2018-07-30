package br.senai.collabtrack.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DateUtil {
	
	@Value("${application.period.last_3_days}")
	private int lastTreeDays;
	
	@Value("${application.period.last_7_days}")
	private int last7Days;
	
	@Value("${application.period.last_15_days}")
	private int last15Days;
	
	@Value("${application.period.from_beginning}")
	private int fromTheBeginning;
	
	@Value("${application.time.type.secons}")
	private int typeSeconds;
	
	@Value("${application.time.type.minutes}")
	private int typeMinutes;

	/**
	 * Método que pega o tempo no formato HH:MM:SS de um objeto Date
	 * @param date Instância do objeto Date
	 * @return String no formato HH:MM:SS
	 */
	public String getTime(Date date) {
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}
	
	/**
	 * Método que formata a data
	 * @param date Instância da data
	 */
	public String format(Date date) {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
	}
	
	/**
	 * Calcula a quantidade de tempo de uma data até o momento atual
	 * @param data  Instância da data
	 * @param tipo Tipo de retorno (segundos, minutos, horas)
	 * @return Tempo até atingir o momento atual
	 */
	public long diferencaAteDataHoraAtual(Date data, int tipo){
		Date date = new Date();  

		Calendar dataAtual = Calendar.getInstance();
		dataAtual.setTime(date);
		
		Date dataUltimaMensagem = data;
        Calendar dataInicial = Calendar.getInstance();
        dataInicial.setTime(dataUltimaMensagem);
        
        long diferenca = dataAtual.getTimeInMillis() - dataInicial.getTimeInMillis();
        long diferencaSeg = diferenca / 1000;    //DIFERENCA EM SEGUNDOS   
        long diferencaMin = diferenca / (60 * 1000);    //DIFERENCA EM MINUTOS   
        
        if(tipo == typeSeconds){
        	return diferencaSeg;
        }else if(tipo == typeMinutes){
        	return diferencaMin;
        }else{
        	return diferenca; 
        }
	}
	
	public Date getPeriodo(int periodo) {
		
		// Hoje
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        // Últimos 3 dias
		if (periodo == lastTreeDays) {
			calendar.add(Calendar.DAY_OF_YEAR, -3);
		// Última semana
		} else if (periodo == last7Days) {
			calendar.add(Calendar.DAY_OF_YEAR, -7);
		// Últimos 15 dias
		} else if (periodo == last15Days) {
			calendar.add(Calendar.DAY_OF_YEAR, -15);
		}
		
		if(periodo == fromTheBeginning) {
			return null;
		} else {
			return calendar.getTime();
		}
		
	}
	
}
