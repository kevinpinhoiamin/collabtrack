package br.senai.collabtrack.domain.util;

import android.os.Parcelable;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.senai.collabtrack.domain.AreaSegura;
import br.senai.collabtrack.service.mapa.MapArea;

/**
 * Created by kevin on 30/05/17.
 */

public class AreaSeguraUtil {

    public static List<Parcelable> writeToParcelList(Map<String, MapArea> areaMap){
        List<Parcelable> secureAreaList = null;
        if(areaMap != null){
            secureAreaList =  new ArrayList<>();
            for (MapArea mapAreaIterator : areaMap.values()){
                AreaSegura areaSegura = new AreaSegura();
                areaSegura.setId(mapAreaIterator.getAreaSegura().getId());
                areaSegura.setNome(mapAreaIterator.getAreaSegura().getNome());
                areaSegura.setCor(mapAreaIterator.getAreaSegura().getCor());
                areaSegura.setCorBorda(mapAreaIterator.getAreaSegura().getCorBorda());
                areaSegura.setRaio(mapAreaIterator.getAreaSegura().getRaio());
                areaSegura.setLatitude(mapAreaIterator.getAreaSegura().getLatitude());
                areaSegura.setLongitude(mapAreaIterator.getAreaSegura().getLongitude());
                secureAreaList.add(Parcels.wrap(areaSegura));
            }
        }
        return secureAreaList;
    }

    public static List<Parcelable> writeToParcelList(List<AreaSegura> areaSeguraList){
        List<Parcelable> secureAreaList = null;
        if(areaSeguraList != null){
            secureAreaList =  new ArrayList<>();
            for (AreaSegura areaSeguraIterator : areaSeguraList){
                AreaSegura areaSegura = new AreaSegura();
                areaSegura.setId(areaSeguraIterator.getId());
                areaSegura.setNome(areaSeguraIterator.getNome());
                areaSegura.setCor(areaSeguraIterator.getCor());
                areaSegura.setCorBorda(areaSeguraIterator.getCorBorda());
                areaSegura.setRaio(areaSeguraIterator.getRaio());
                areaSegura.setLatitude(areaSeguraIterator.getLatitude());
                areaSegura.setLongitude(areaSeguraIterator.getLongitude());
                secureAreaList.add(Parcels.wrap(areaSegura));
            }
        }
        return secureAreaList;
    }

    public static Parcelable writeToParcel(AreaSegura areaSegura){
        return Parcels.wrap(areaSegura);
    }

    public static AreaSegura writeToAraSegura(Parcelable parcelable){
        return Parcels.unwrap(parcelable);
    }

    public static List<AreaSegura> writeToAreaSeguraList(List<Parcelable> secureAreaParcelable){
        List<AreaSegura> areaSeguraList = null;
        if(secureAreaParcelable != null){
            areaSeguraList = new ArrayList<>();
            for (Parcelable secureArea : secureAreaParcelable){
                areaSeguraList.add((AreaSegura) Parcels.unwrap(secureArea));
            }
        }
        return areaSeguraList;
    }

}
