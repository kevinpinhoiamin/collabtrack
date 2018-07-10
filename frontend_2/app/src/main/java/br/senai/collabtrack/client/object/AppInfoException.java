package br.senai.collabtrack.client.object;

/**
 * Created by ezs on 04/09/2017.
 */

public class AppInfoException extends AppException {

    public AppInfoException(String msg) {
        super(msg);
    }

    public AppInfoException(Throwable e) {
        super(e);
    }

    public AppInfoException(String msg, Throwable e) {
        super(msg, e);
    }
}
