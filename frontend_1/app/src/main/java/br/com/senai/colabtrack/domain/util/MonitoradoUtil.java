package br.com.senai.colabtrack.domain.util;

import android.os.Parcelable;

import org.parceler.Parcels;

import br.com.senai.colabtrack.domain.Monitorado;
import br.com.senai.colabtrack.domain.to.MonitoradoTO;

/**
 * Created by kevin on 10/23/17.
 */

public class MonitoradoUtil {

    public static Parcelable writeToParcel(Monitorado monitorado){
        return Parcels.wrap(monitorado);
    }

    public static Monitorado writeToMonitorado(Parcelable parcelable){
        return Parcels.unwrap(parcelable);
    }

    public static Parcelable writeToParcel(MonitoradoTO monitoradoTO){
        return Parcels.wrap(monitoradoTO);
    }

    public static MonitoradoTO writeToMonitoradoTO(Parcelable parcelable){
        return Parcels.unwrap(parcelable);
    }

}
