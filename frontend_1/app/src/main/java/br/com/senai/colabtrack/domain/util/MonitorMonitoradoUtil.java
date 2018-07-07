package br.com.senai.colabtrack.domain.util;

import android.os.Parcelable;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import br.com.senai.colabtrack.domain.MonitorMonitorado;

/**
 * Created by kevin on 3/6/18.
 */

public class MonitorMonitoradoUtil {

    public static List<Parcelable> writeToParcelList(List<MonitorMonitorado> monitorMonitorados) {
        List<Parcelable> parcelableList = null;
        if(monitorMonitorados != null){
            parcelableList = new ArrayList<>();
            for(MonitorMonitorado monitorMonitorado : monitorMonitorados){
                parcelableList.add(Parcels.wrap(monitorMonitorado));
            }
        }
        return parcelableList;
    }

    public static List<MonitorMonitorado> writeToMonitorMonitoradoList(List<Parcelable> parcelableList){
        List<MonitorMonitorado> monitorMonitorados = null;
        if(parcelableList != null){
            monitorMonitorados = new ArrayList<>();
            for(Parcelable parcelable : parcelableList){
                monitorMonitorados.add((MonitorMonitorado) Parcels.unwrap(parcelable));
            }
        }
        return monitorMonitorados;
    }

}
