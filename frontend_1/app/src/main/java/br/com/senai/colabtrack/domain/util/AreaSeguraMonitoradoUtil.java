package br.com.senai.colabtrack.domain.util;

import android.os.Parcelable;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import br.com.senai.colabtrack.domain.AreaSeguraMonitorado;

/**
 * Created by kevin on 07/07/17.
 */

public class AreaSeguraMonitoradoUtil {

    public static List<Parcelable> writeToParcelList(List<AreaSeguraMonitorado> areaSeguraMonitorados){
        List<Parcelable> parcelableList = null;
        if(areaSeguraMonitorados != null){
            parcelableList = new ArrayList<>();
            for(AreaSeguraMonitorado areaSeguraMonitorado : areaSeguraMonitorados){
                parcelableList.add(Parcels.wrap(areaSeguraMonitorado));
            }
        }
        return parcelableList;
    }

    public static List<AreaSeguraMonitorado> writeToAreaSeguraMonitoradoList(List<Parcelable> parcelableList){
        List<AreaSeguraMonitorado> areaSeguraMonitorados = null;
        if(parcelableList != null){
            areaSeguraMonitorados = new ArrayList<>();
            for(Parcelable parcelable : parcelableList){
                areaSeguraMonitorados.add((AreaSeguraMonitorado) Parcels.unwrap(parcelable));
            }
        }
        return areaSeguraMonitorados;
    }

    public static Parcelable writeToParcel(AreaSeguraMonitorado areaSeguraMonitorado){
        return Parcels.wrap(areaSeguraMonitorado);
    }

    public static AreaSeguraMonitorado writeToAraSeguraMonitorado(Parcelable parcelable){
        return Parcels.unwrap(parcelable);
    }

}
