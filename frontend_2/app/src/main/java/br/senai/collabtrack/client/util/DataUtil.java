package br.senai.collabtrack.client.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ezs on 10/10/2017.
 */

public class DataUtil {
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String getDateTime(){
        return sdf.format(new Date().getTime());
    }

}
