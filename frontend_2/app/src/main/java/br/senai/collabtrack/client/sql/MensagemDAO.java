package br.senai.collabtrack.client.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.senai.collabtrack.client.object.MensagemTO;
import br.senai.collabtrack.client.Application;

/**
 * Created by ezs on 09/10/2017.
 */

public class MensagemDAO extends BaseSQL {

    private String entityName = "mensagem";

    public MensagemDAO(){
        super(Application.getContext());
    }

    public MensagemDAO(Context context) {
        super(context);
    }

    public Long save(MensagemTO mensagemTO) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Long nextId = 1L;

            Cursor cursor = db.query(entityName, new String[]{"id"}, null, null, null, null, "id desc");
            if (cursor.moveToNext())
                nextId = cursor.getLong(cursor.getColumnIndex("id")) + 1;

            ContentValues contentValues = new ContentValues();
            contentValues.put("id", nextId);
            contentValues.put("resposta", mensagemTO.isResposta() ? 1 : 0);
            contentValues.put("datahora", mensagemTO.getData());
            String datahora = mensagemTO.getData();
            datahora = datahora.substring(0, 19);
            Application.log("ADD NEXT ID " + nextId);
            SQLiteDatabase db2 = getWritableDatabase();
            Long r = db2.insert(entityName, null, contentValues);
            db2.close();
            db.close();
            return r;
        } catch (Exception e) {
            Application.log(e.getMessage(), e);
            return -1L;
        } finally {
            db.close();
        }
    }

    public List<MensagemTO> findAll() {
        List<MensagemTO> listMsgs = new ArrayList<MensagemTO>();
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.query(entityName, new String[]{"*"}, null, null, null, null, null);
            while (cursor.moveToNext())
                listMsgs.add(new MensagemTO(cursor.getLong(cursor.getColumnIndex("id")), cursor.getInt(cursor.getColumnIndex("resposta")) == 1, cursor.getString(cursor.getColumnIndex("datahora"))));
        } catch (Exception e) {
            Application.log(e.getMessage(), e);
        } finally {
            db.close();
        }
        return listMsgs;
    }

    public boolean exists(Long idMsg) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            Cursor cursor = db.query(entityName, new String[]{"*"}, "id=" + idMsg, null, null, null, null);
            return cursor.moveToNext();
        } catch (Exception e) {
            Application.log(e.getMessage(), e);
            return false;
        } finally {
            db.close();
        }
    }

    public void delete(Long idMsg) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            db.delete(entityName, "id=?", new String[]{String.valueOf(idMsg)});
            db.close();
        } finally {
            db.close();
        }
    }

    public void deleteAll() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            db.delete(entityName, null, null);
        } finally {
            db.close();
        }
    }

}
