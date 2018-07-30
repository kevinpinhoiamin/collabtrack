package br.senai.collabtrack.util;

import java.util.Random;

public class TokenUtil {


	public String getToken() {		
		String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890";
		int TOKEN_LENGTH = 6;
		
	    StringBuilder token = new StringBuilder(TOKEN_LENGTH);
	    for (int i = 0; i < TOKEN_LENGTH; i++) {
	        token.append(CHARS.charAt(new Random().nextInt(CHARS.length())));
	    }
	    return token.toString();
	}
	
}
