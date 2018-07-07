package br.com.senai.colabtrack.domain.util;

import android.os.Parcelable;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import br.com.senai.colabtrack.domain.Monitor;

/**
 * Created by kevin on 30/06/17.
 */

public class MonitorUtil {

    public static List<Parcelable> writeToParcelList(List<Monitor> monitorList){
        List<Parcelable> parcelableList = null;
        if(monitorList != null){
            parcelableList = new ArrayList<>();
            for (Monitor monitor : monitorList){
                parcelableList.add(Parcels.wrap(monitor));
            }
        }
        return parcelableList;
    }

    public static List<Monitor> writeToMonitorList(List<Parcelable> parcelableList){
        List<Monitor> monitorList = null;
        if(parcelableList != null){
            monitorList = new ArrayList<>();
            for(Parcelable parcelable : parcelableList){
                monitorList.add((Monitor) Parcels.unwrap(parcelable));
            }
        }
        return monitorList;
    }

    public static Parcelable writeToParcel(Monitor monitor){
        return Parcels.wrap(monitor);
    }

    public static Monitor writeToMonitor(Parcelable parcelable){
        return Parcels.unwrap(parcelable);
    }

}
