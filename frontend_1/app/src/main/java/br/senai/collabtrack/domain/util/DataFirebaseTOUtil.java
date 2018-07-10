package br.senai.collabtrack.domain.util;

import android.os.Parcelable;

import org.parceler.Parcels;

import br.senai.collabtrack.domain.to.DataFirebaseTO;

/**
 * Created by kevin on 8/24/17.
 */

public class DataFirebaseTOUtil {

    public static Parcelable writeToParcel(DataFirebaseTO dataFirebaseTO){
        return Parcels.wrap(dataFirebaseTO);
    }

    public static DataFirebaseTO writeToDataFirebaseTO(Parcelable parcelable){
        return Parcels.unwrap(parcelable);
    }

}
