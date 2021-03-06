package br.senai.collabtrack.domain.util;

import android.os.Parcelable;

import org.parceler.Parcels;

import br.senai.collabtrack.domain.Status;

/**
 * Created by kevin on 9/4/17.
 */

public class StatusUtil {

    public static Parcelable writeToParcel(Status status){
        return Parcels.wrap(status);
    }

    public static Status writeToStatus(Parcelable parcelable){
        return Parcels.unwrap(parcelable);
    }

}
