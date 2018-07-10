package br.senai.collabtrack.client.sql;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Objects;

import br.senai.collabtrack.client.Application;

/**
 * Created by ezs on 05/09/2017.
 */

public class BaseSQL extends SQLiteOpenHelper {

    private static final String DATABASE = "collabtrack_monitorado";
    private static final int VERSION = 25;

    public BaseSQL(Context context){
        super(context, DATABASE, null, VERSION);
        Application.log("BaseSQL INSTANCED");

        execSQL("CREATE TABLE IF NOT EXISTS monitorado (" +
                " id               INTEGER   NOT NULL, " +
                " celular          INTEGER   NOT NULL, " +
                " id_monitor       INTEGER   NOT NULL, " +
                " celular_monitor  INTEGER   NOT NULL)");

        execSQL("CREATE TABLE IF NOT EXISTS audio (" +
                " id        INTEGER  NOT NULL, " +
                " resposta  INTEGER  NOT NULL)");

        execSQL("CREATE TABLE IF NOT EXISTS mensagem (" +
                " id        INTEGER   NOT NULL, " +
                " resposta  INTEGER   NOT NULL," +
                " datahora  CHAR(19)  NOT NULL)");
    }

    public BaseSQL(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Application.log("BaseSQL 2");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Application.log("BaseSQL 3");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void execSQL(String sql){
        SQLiteDatabase db = null;
        try{
            db = getWritableDatabase();
            db.execSQL(sql);
        }catch (SQLException e){
            Application.log(e);
        }finally {
            if (db != null)
                db.close();
        }
    }

    private void execSQL(String sql, Objects[] args){
        SQLiteDatabase db = getWritableDatabase();
        try{
            db.execSQL(sql, args);
        }finally {
            db.close();
        }
    }
}
