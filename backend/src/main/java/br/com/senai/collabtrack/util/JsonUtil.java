package br.com.senai.collabtrack.util;

import com.google.gson.GsonBuilder;

public class JsonUtil {

	public String toJson(Object object) {
		
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat("dd/MM/yyyy HH:mm:ss");		
		return builder.create().toJson(object);
		
	}

	public Object toObject(String json, Class<?> clazz) {
		return new GsonBuilder().create().fromJson(json, clazz);
	}

}
