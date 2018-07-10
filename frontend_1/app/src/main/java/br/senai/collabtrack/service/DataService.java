package br.senai.collabtrack.service;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lucas Matheus Nunes on 09/10/2017.
 */

public class DataService {

    public boolean dataMaiorQueUmDia(String data){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar data1 = Calendar.getInstance();
        Calendar data2 = Calendar.getInstance();


        try {
            data1.setTime(sdf.parse(retiraHoraDeData(data)));
            data2.setTime(sdf.parse(String.valueOf(new Date().getDay())));
        } catch (java.text.ParseException e ) {}
        int dias = data2.get(Calendar.DAY_OF_YEAR) - data1.get(Calendar.DAY_OF_YEAR);
        if(dias > 0){
            return true;
        }else{
            return false;
        }
    }


    public String retiraHoraDeData(String data){
        String dataSHora[] = data.split(" ");
        return dataSHora[0];
    }

    public  String converteDiaMesHora(String data){
        String dataFinal = "";

        String dataDividida[] = data.split(" ");
        String diaMesAno[] = dataDividida[0].split("/");

        String horaMinSeg[] = dataDividida[1].split(":");

        dataFinal += diaMesAno[0]+"/"+diaMesAno[1]+" às "+horaMinSeg[0]+":"+horaMinSeg[1];

        return dataFinal;
    }

    public String retiraDataDeHora(String data){
        String dataDividida[] = data.split(" ");
        String horaDividida[] = dataDividida[1].split(":");
        return horaDividida[0]+":"+horaDividida[1];
    }

    public String formataMinutos(double minutos) {
        String numero;
        DecimalFormat formato = new DecimalFormat("#.##");
        numero = formato.format(minutos);
        return numero;
    }
}

