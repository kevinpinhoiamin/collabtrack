package br.senai.collabtrack.client.object;

/**
 * Created by ezs on 02/10/2017.
 */

public class AppErrorException extends AppException {

    public AppErrorException(String msg) {
        super(msg);
    }

    public AppErrorException(Throwable e) {
        super(e);
    }

    public AppErrorException(String msg, Throwable e) {
        super(msg, e);
    }

}
