package br.senai.collabtrack.component;

import android.app.ProgressDialog;
import android.content.Context;

import br.senai.collabtrack.R;

/**
 * Created by kevin on 14/06/17.
 */

public class Loader {

    private ProgressDialog progressDialog;

    private String title= "";
    private String message = "";
    private final Context context;

    public Loader(Context context){
        this.context = context;
    }

    public Loader(Context context, String message){
        this.context = context;
        this.message = message;
    }

    public Loader(Context context, String message, String title){
        this.context = context;
        this.message = message;
        this.title = title;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }
    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public void show(){

        if(message.equals("")){
            message = context.getString(R.string.carregando)+"...";
        }

        hide();
        this.progressDialog = ProgressDialog.show(context, title, message, true, false);
    }

    public void hide(){
        if(this.progressDialog != null){
            this.progressDialog.dismiss();
        }
    }

}
