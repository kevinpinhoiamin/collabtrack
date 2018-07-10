package br.senai.collabtrack.client.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.senai.collabtrack.client.object.Audio;
import br.senai.collabtrack.client.Application;

/**
 * Created by ezs on 04/10/2017.
 */

public class AudioDAO extends BaseSQL {
    private String entityName = "audio";

    public AudioDAO() {
        super(Application.getContext());
    }

    public AudioDAO(Context context) {
        super(context);
    }

    public Long save(Audio audio) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", audio.getId());
            contentValues.put("resposta", audio.getResposta() ? 1 : 0);
            return db.insert(entityName, null, contentValues);
        } catch (Exception e) {
            Application.log(e.getMessage(), e);
            return -1L;
        } finally {
            db.close();
        }
    }

    public List<Audio> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        List<Audio> listAudios = new ArrayList<Audio>();
        try {
            Cursor cursor = db.query(entityName, new String[]{"*"}, null, null, null, null, null);
            while (cursor.moveToNext())
                listAudios.add(new Audio(cursor.getLong(cursor.getColumnIndex("id")), cursor.getInt(cursor.getColumnIndex("resposta")) == 1 ? true : false));
        } catch (Exception e) {
            Application.log(e.getMessage(), e);
        } finally {
            db.close();
        }
        return listAudios;
    }


    public boolean exists(Long idAudio) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            Cursor cursor = db.query(entityName, new String[]{"*"}, "id=" + idAudio, null, null, null, null);
            return cursor.moveToNext();
        } catch (Exception e) {
            Application.log(e.getMessage(), e);
            return false;
        } finally {
            db.close();
        }
    }

    public void delete(Long idAudio) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            db.delete(entityName, null, null);
            db.close();
        } finally {
            db.close();
        }
    }
}
