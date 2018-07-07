package br.com.senai.colabtrack.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import br.com.senai.colabtrack.ColabTrackApplication;

/**
 * Created by kevin on 8/9/17.
 */

public class DateUtil {

    private static boolean isBRDate(String date) {
        return date != null && !date.isEmpty() && Pattern.matches("^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}$", date);
    }

    public static String fromAPITODescribed(String date){
        if(DateUtil.isBRDate(date)){

            // Pega os dados da data
            String[] array = date.split(" ");
            String[] data = array[0].split("/");
            String[] horario = array[1].split(":");
            String dia = data[0];
            String mes = data[1];
            String ano = data[2];
            String hora = horario[0];
            String minuto = horario[1];
            // String segundo = horario[2];

            // Calendário com a data de hoje
            Calendar calendarHoje = Calendar.getInstance();
            calendarHoje.set(Calendar.HOUR_OF_DAY, 0);
            calendarHoje.set(Calendar.MINUTE, 0);
            calendarHoje.set(Calendar.SECOND, 0);
            calendarHoje.set(Calendar.MILLISECOND, 0);
            calendarHoje.add(Calendar.MONTH, 1);

            Calendar calendarOntem = (Calendar) calendarHoje.clone();
            calendarOntem.add(Calendar.DAY_OF_YEAR, -1);

            Calendar calendarAnoPassado = (Calendar) calendarHoje.clone();
            calendarAnoPassado.add(Calendar.YEAR, -1);

            // Transforma o calendário com os dados da data
            Calendar calendarData = Calendar.getInstance();
            calendarData.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dia));
            calendarData.set(Calendar.MONTH, Integer.parseInt(mes));
            calendarData.set(Calendar.YEAR, Integer.parseInt(ano));
            /*
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hora));
            calendar.set(Calendar.MINUTE, Integer.parseInt(minuto));
            calendar.set(Calendar.SECOND, Integer.parseInt(segundo));
            */

            // Se for hoje, imprime apenas a hora
            if(calendarData.get(Calendar.YEAR) == calendarHoje.get(Calendar.YEAR)
                    && calendarData.get(Calendar.DAY_OF_YEAR) == calendarHoje.get(Calendar.DAY_OF_YEAR)) {
                return "Hoje, às " + hora + ":" + minuto;
            } else if (calendarData.get(Calendar.YEAR) == calendarOntem.get(Calendar.YEAR)
                    && calendarData.get(Calendar.DAY_OF_YEAR) == calendarOntem.get(Calendar.DAY_OF_YEAR)) {
                return "Ontém, às " + hora + ":" + minuto;
            } else if(calendarData.get(Calendar.YEAR) > calendarAnoPassado.get(Calendar.YEAR)) {
                return dia + "/" + mes + ", às " + hora + ":" + minuto;
            } else {
                return dia + "/" + mes + "/" + ano + ", às " + hora + ":" + minuto;
            }

        }
        return "";
    }

    public static boolean isFiveMinutesOlder(String date) {

        if(DateUtil.isBRDate(date)){

            // Pega os dados da data
            String[] array = date.split(" ");
            String[] data = array[0].split("/");
            String[] horario = array[1].split(":");
            String dia = data[0];
            String mes = data[1];
            String ano = data[2];
            String hora = horario[0];
            String minuto = horario[1];
            String segundo = horario[2];

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dia));
            calendar.set(Calendar.MONTH, Integer.parseInt(mes));
            calendar.set(Calendar.YEAR, Integer.parseInt(ano));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hora));
            calendar.set(Calendar.MINUTE, Integer.parseInt(minuto));
            calendar.set(Calendar.SECOND, Integer.parseInt(segundo));

            Calendar calendarFiveMinutesAgo = Calendar.getInstance();
            calendarFiveMinutesAgo.set(Calendar.MINUTE, calendarFiveMinutesAgo.get(Calendar.MINUTE) - 5);
            calendarFiveMinutesAgo.add(Calendar.MONTH, 1);

            if(
                    calendar.get(Calendar.YEAR) <= calendarFiveMinutesAgo.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) <= calendarFiveMinutesAgo.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) <= calendarFiveMinutesAgo.get(Calendar.DAY_OF_MONTH) &&
                    calendar.get(Calendar.HOUR_OF_DAY) <= calendarFiveMinutesAgo.get(Calendar.HOUR_OF_DAY) &&
                    calendar.get(Calendar.MINUTE) <= calendarFiveMinutesAgo.get(Calendar.MINUTE)
            ) {
                return true;
            }

        }

        return false;

    }

    public static int PERIODO_HOJE = 1;
    public static int PERIODO_ULTIMOS_3_DIAS = 2;
    public static int PERIODO_ULTIMA_SEMANA = 3;
    public static int PERIODO_ULTIMOS_15_DIAS = 4;
    public static int PERIODO_DESDE_O_COMECO = 5;
    public static Date getPeriodo(int periodo) {

        // Hoje
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Últimos 3 dias
        if (periodo == PERIODO_ULTIMOS_3_DIAS) {
            calendar.add(Calendar.DAY_OF_YEAR, -3);
            // Última semana
        } else if (periodo == PERIODO_ULTIMA_SEMANA) {
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            // Últimos 15 dias
        } else if (periodo == PERIODO_ULTIMOS_15_DIAS) {
            calendar.add(Calendar.DAY_OF_YEAR, -15);
        }

        if(periodo == PERIODO_DESDE_O_COMECO) {
            return null;
        } else {
            return calendar.getTime();
        }

    }

    public static String formatBR(String date) {
        if (date != null && !date.isEmpty() && Pattern.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$", date)) {
            // 2018-05-25 21:34:41


            // Pega os dados da data
            String[] array = date.split(" ");
            String[] data = array[0].split("-");
            String[] horario = array[1].split(":");
            String dia = data[2];
            String mes = data[1];
            String ano = data[0];
            String hora = horario[0];
            String minuto = horario[1];
            String segundo = horario[2];

            return dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto + ":" + segundo;

        }
        return "";
    }

    public static String formatDatabase(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String formatDatabase(String date) {
        if(DateUtil.isBRDate(date)) {

            // Pega os dados da data
            String[] array = date.split(" ");
            String[] data = array[0].split("/");
            String[] horario = array[1].split(":");
            String dia = data[0];
            String mes = data[1];
            String ano = data[2];
            String hora = horario[0];
            String minuto = horario[1];
            String segundo = horario[2];

            return ano + "-" + mes + "-" + dia + " " + hora + ":" + minuto + ":" + segundo;
        }
        return "";
    }

}
