package br.senai.collabtrack.client.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.senai.collabtrack.client.object.Monitorado;
import br.senai.collabtrack.client.Application;

/**
 * Created by ezs on 05/09/2017.
 */

public class MonitoradoDAO extends BaseSQL {

    private String entityName = "monitorado";

    public MonitoradoDAO(Context context) {
        super(context);
    }

    public Long save(Monitorado monitorado){
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", monitorado.getId());
            contentValues.put("celular", monitorado.getCelular());
            contentValues.put("id_monitor", monitorado.getIdMonitor());
            contentValues.put("celular_monitor", monitorado.getCelularMonitor());
            return db.insert(entityName, null, contentValues);
        } finally {
            db.close();
        }
    }

    public Monitorado find(){
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor cursor = db.query(entityName, new String[]{"*"}, null, null, null, null, null);
            while(cursor.moveToNext()){
                Monitorado monitorado = new Monitorado();
                monitorado.setId(cursor.getLong(cursor.getColumnIndex("id")));
                monitorado.setCelular(cursor.getLong(cursor.getColumnIndex("celular")));
                monitorado.setIdMonitor(cursor.getLong(cursor.getColumnIndex("id_monitor")));
                monitorado.setCelularMonitor(cursor.getLong(cursor.getColumnIndex("celular_monitor")));
                return monitorado;
            }
        } catch (Exception e){
            Application.log(e);
        }
        finally {
            db.close();
        }
        return null;
    }

    public void deleteAll(){
        SQLiteDatabase db = getReadableDatabase();
        try {
            db.delete(entityName, null, null);
        } finally {
            db.close();
        }
    }
}
