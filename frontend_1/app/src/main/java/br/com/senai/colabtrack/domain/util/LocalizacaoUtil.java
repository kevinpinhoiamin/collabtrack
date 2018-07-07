package br.com.senai.colabtrack.domain.util;

import android.os.Parcelable;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import br.com.senai.colabtrack.domain.Localizacao;

/**
 * Created by kevin on 8/25/17.
 */

public class LocalizacaoUtil {

    public static Parcelable writeToParcel(Localizacao localizacao){
        return Parcels.wrap(localizacao);
    }

    public static Localizacao writeToLocalizacao(Parcelable parcelable){
        return Parcels.unwrap(parcelable);
    }

}
