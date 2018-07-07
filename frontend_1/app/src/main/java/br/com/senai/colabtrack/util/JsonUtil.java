package br.com.senai.colabtrack.util;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kevin on 09/06/17.
 */

public class JsonUtil {

    public static String toJson(Object object){
        return new Gson().toJson(object);
    }

    public static Object toObject(String json, Class c){
        return new Gson().fromJson(json, c);
    }

    public static final <T> List<T> toObjectList(String json, Class<T[]> clazz) {
        if(!json.equals("null") && json != null && !json.isEmpty()){
            return Arrays.asList(new Gson().fromJson(json, clazz));
        }else{
            return new ArrayList<T>();
        }
    }

}
