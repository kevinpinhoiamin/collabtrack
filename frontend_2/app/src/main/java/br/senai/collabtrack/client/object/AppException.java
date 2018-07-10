package br.senai.collabtrack.client.object;

/**
 * Created by ezs on 02/10/2017.
 */

public class AppException extends Exception {

    public AppException(String msg) {
        super(msg);
    }

    public AppException(Throwable e) {
        super(e);
    }

    public AppException(String msg, Throwable e) {
        super(msg, e);
    }
}
