package br.com.senai.colabtrack.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.dao.MonitoradoDAO;
import br.com.senai.colabtrack.domain.Monitorado;

/**
 * Created by kevin on 22/06/17.
 */

public class MonitoradoSQL extends BaseSQL implements MonitoradoDAO{

    public MonitoradoSQL(Context context) {
        super(context, ColabTrackApplication.DATABASE, null, ColabTrackApplication.DATABASE_VERSION);
    }

    @Override
    public Long save(Monitorado o) {

        SQLiteDatabase db = getWritableDatabase();

        try{
            ContentValues values = new ContentValues();
            values.put("id", o.getId());
            values.put("nome", o.getNome());
            values.put("celular", o.getCelular());
            return db.insert("monitorado", "", values);
        }finally {
            db.close();
        }

    }

    @Override
    public Long update(Monitorado o) {

        SQLiteDatabase db = getWritableDatabase();
        Long id = o.getId();

        try{
            ContentValues values = new ContentValues();
            values.put("id", o.getId());
            values.put("nome", o.getNome());
            values.put("celular", o.getCelular());
            String[] where = new String[]{String.valueOf(id)};
            return Long.valueOf(db.update("monitorado", values, "id=?", where));
        }finally {
            db.close();
        }

    }

    @Override
    public int delete(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            return db.delete("monitorado", "id=?", new String[]{String.valueOf(id)});
        }finally {
            db.close();
        }
    }

    @Override
    public List<Monitorado> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c = db.query("monitorado", new String[] { ColabTrackApplication.SQL_ROW_ID_COLUMN, "*" }, null, null, null, null, null, null);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<Monitorado> findActives() {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String sql = "SELECT * FROM monitorado m INNER JOIN monitor_has_monitorado mm ON m.id = mm.id_monitorado WHERE mm.ativo = ?";
            Cursor c = db.rawQuery(sql, new String[]{"1"});
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<Monitorado> findPrincipal() {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String sql = "SELECT * FROM monitorado m INNER JOIN monitor_has_monitorado mm ON m.id == mm.id_monitorado WHERE mm.principal = ?";
            Cursor c = db.rawQuery(sql, new String[]{"1"});
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public Monitorado find(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c = db.query("monitorado", new String[] { ColabTrackApplication.SQL_ROW_ID_COLUMN, "*" }, "id=?", new String[]{String.valueOf(id)}, null, null, null, null);
            return parseObject(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<Monitorado> parseList(Cursor cursor) {
        List<Monitorado> monitorados = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Monitorado monitorado = new Monitorado();
                monitorado.setId(cursor.getLong(cursor.getColumnIndex("id")));
                monitorado.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                monitorado.setCelular(cursor.getLong(cursor.getColumnIndex("celular")));
                monitorado.setRowid(ColabTrackApplication.SQL_ROW_ID_INDEX);
                monitorados.add(monitorado);
            }while (cursor.moveToNext());
        }
        return monitorados;
    }

    @Override
    public Monitorado parseObject(Cursor cursor) {
        if(cursor.moveToFirst()){
            Monitorado monitorado = new Monitorado();
            monitorado.setId(cursor.getLong(cursor.getColumnIndex("id")));
            monitorado.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            monitorado.setCelular(cursor.getLong(cursor.getColumnIndex("celular")));
            monitorado.setRowid(ColabTrackApplication.SQL_ROW_ID_INDEX);
            return monitorado;
        }
        return null;
    }

    @Override
    public int deleteByMonitorado(Long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            return db.delete("monitorado", "id=?", new String[]{String.valueOf(idMonitorado)});
        }finally {
            db.close();
        }
    }

}
