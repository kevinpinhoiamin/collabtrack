package br.senai.collabtrack.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.dao.MonitorDAO;
import br.senai.collabtrack.domain.Monitor;

/**
 * Created by kevin on 09/06/17.
 */

public class MonitorSQL extends BaseSQL implements MonitorDAO {

    public MonitorSQL(Context context){
        super(context, CollabtrackApplication.DATABASE, null, CollabtrackApplication.DATABASE_VERSION);
    }

    @Override
    public Long save(Monitor o) {

        // Quando salva um monitor, apaga a base de dados e cria as tabelas novamente para sincronizar os dados com o servidor
        context.deleteDatabase(CollabtrackApplication.DATABASE);
        SQLiteDatabase db = getWritableDatabase();
        super.onCreate(db);

        try{
            ContentValues values = new ContentValues();
            values.put("id", o.getId());
            values.put("nome", o.getNome());
            values.put("celular", o.getCelular());
            values.put("token_autenticacao", o.getTokenAutenticacao());
            return db.insert("monitor", "", values);
        }finally {
            db.close();
        }
    }
    @Override
    public Long update(Monitor o) {

        SQLiteDatabase db = getWritableDatabase();
        Long id = o.getId();

        try{
            ContentValues values = new ContentValues();
            values.put("id", o.getId());
            values.put("nome", o.getNome());
            values.put("celular", o.getCelular());
            values.put("token_autenticacao", o.getTokenAutenticacao());
            String[] where = new String[]{String.valueOf(id)};
            return Long.valueOf(db.update("monitor", values, "id=?", where));
        }finally {
            db.close();
        }
    }

    @Override
    public int delete(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            return db.delete("monitor", "id=?", new String[]{String.valueOf(id)});
        }finally {
            db.close();
        }
    }

    @Override
    public List<Monitor> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c = db.query("monitor", new String[] { CollabtrackApplication.SQL_ROW_ID_COLUMN, "*" }, null, null, null, null, null, null);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public Monitor find(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c = db.query("monitor", new String[] { CollabtrackApplication.SQL_ROW_ID_COLUMN, "*" }, "id=?", new String[]{String.valueOf(id)}, null, null, null, null);
            return parseObject(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<Monitor> parseList(Cursor cursor) {
        List<Monitor> monitores = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Monitor monitor = new Monitor();
                monitor.setId(cursor.getLong(cursor.getColumnIndex("id")));
                monitor.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                monitor.setCelular(cursor.getLong(cursor.getColumnIndex("celular")));
                monitor.setTokenAutenticacao(cursor.getString(cursor.getColumnIndex("token_autenticacao")));
                monitor.setRowid(cursor.getLong(CollabtrackApplication.SQL_ROW_ID_INDEX));
                monitores.add(monitor);
            }while (cursor.moveToNext());
        }
        return monitores;
    }

    @Override
    public Monitor parseObject(Cursor cursor) {
        if(cursor.moveToFirst()){
            Monitor monitor = new Monitor();
            monitor.setId(cursor.getLong(cursor.getColumnIndex("id")));
            monitor.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            monitor.setCelular(cursor.getLong(cursor.getColumnIndex("celular")));
            monitor.setTokenAutenticacao(cursor.getString(cursor.getColumnIndex("token_autenticacao")));
            monitor.setRowid(cursor.getLong(CollabtrackApplication.SQL_ROW_ID_INDEX));
            return monitor;
        }
        return null;
    }

    @Override
    public Monitor findAutenticado() {
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.query("monitor", null, null, null, null, null, null, null);
        return parseObject(c);
    }
}