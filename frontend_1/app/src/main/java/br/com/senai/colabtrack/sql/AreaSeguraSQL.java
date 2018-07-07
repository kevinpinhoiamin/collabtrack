package br.com.senai.colabtrack.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.senai.colabtrack.dao.AreaSeguraDAO;
import br.com.senai.colabtrack.dao.GenericDAO;
import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.domain.AreaSegura;

/**
 * Created by kevin on 25/05/17.
 */

public class AreaSeguraSQL extends BaseSQL implements AreaSeguraDAO {

    public AreaSeguraSQL(Context context){
        super(context, ColabTrackApplication.DATABASE, null, ColabTrackApplication.DATABASE_VERSION);
    }

    @Override
    public Long save(AreaSegura o) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("id", o.getId());
            values.put("nome", o.getNome());
            values.put("cor", o.getCor());
            values.put("cor_borda", o.getCorBorda());
            values.put("raio", o.getRaio());
            values.put("latitude", o.getLatitude());
            values.put("longitude", o.getLongitude());
            return db.insert("area_segura", "", values);
        }finally {
            db.close();
        }
    }

    @Override
    public Long update(AreaSegura o) {
        Long id = o.getId();
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("id", o.getId());
            values.put("nome", o.getNome());
            values.put("cor", o.getCor());
            values.put("cor_borda", o.getCorBorda());
            values.put("raio", o.getRaio());
            values.put("latitude", o.getLatitude());
            values.put("longitude", o.getLongitude());
            String[] where = new String[]{String.valueOf(id)};
            return Long.valueOf(db.update("area_segura", values, "id=?", where));
        }finally {
            db.close();
        }
    }

    @Override
    public int delete(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            return db.delete("area_segura", "id=?", new String[]{String.valueOf(id)});
        }finally {
            db.close();
        }
    }

    @Override
    public List<AreaSegura> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c = db.query("area_segura", new String[] { ColabTrackApplication.SQL_ROW_ID_COLUMN, "*" }, null, null, null, null, "nome COLLATE NOCASE ASC", null);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<AreaSegura> findByMonitorado(Long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String[] where = new String[]{String.valueOf(idMonitorado)};
            String sql = "SELECT a.* FROM area_segura a INNER JOIN area_segura_has_monitorado am ON a.id = am.id_area_segura WHERE am.id_monitorado = ?";
            Cursor c = db.rawQuery(sql, where);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<AreaSegura> findAtivasByMonitorado(Long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String[] where = new String[]{String.valueOf(idMonitorado)};
            String sql = "SELECT a.* FROM area_segura a INNER JOIN area_segura_has_monitorado am ON a.id = am.id_area_segura WHERE am.id_monitorado = ? AND am.ativa = 1";
            Cursor c = db.rawQuery(sql, where);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<AreaSegura> search(String search) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c;
            String sql = "SELECT a.* FROM area_segura a";
            if (search != null && !search.equals("")) {
                sql += " WHERE a.nome LIKE '%"+search+"%'";
            }
            c = db.rawQuery(sql, null);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public AreaSegura find(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c = db.query("area_segura", new String[] { ColabTrackApplication.SQL_ROW_ID_COLUMN, "*" }, "id=?", new String[]{String.valueOf(id)}, null, null, null, null);
            return parseObject(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<AreaSegura> parseList(Cursor cursor) {
        List<AreaSegura> areaSeguras = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                AreaSegura areaSegura = new AreaSegura();
                areaSegura.setId(cursor.getLong(cursor.getColumnIndex("id")));
                areaSegura.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                areaSegura.setCor(cursor.getInt(cursor.getColumnIndex("cor")));
                areaSegura.setCorBorda(cursor.getInt(cursor.getColumnIndex("cor_borda")));
                areaSegura.setRaio(cursor.getInt(cursor.getColumnIndex("raio")));
                areaSegura.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                areaSegura.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                areaSegura.setRowid(cursor.getLong(ColabTrackApplication.SQL_ROW_ID_INDEX));
                areaSeguras.add(areaSegura);
            }while (cursor.moveToNext());
        }
        return areaSeguras;
    }

    @Override
    public AreaSegura parseObject(Cursor cursor) {
        if(cursor.moveToFirst()){
            AreaSegura areaSegura = new AreaSegura();
            areaSegura.setId(cursor.getLong(cursor.getColumnIndex("id")));
            areaSegura.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            areaSegura.setCor(cursor.getInt(cursor.getColumnIndex("cor")));
            areaSegura.setCorBorda(cursor.getInt(cursor.getColumnIndex("cor_borda")));
            areaSegura.setRaio(cursor.getInt(cursor.getColumnIndex("raio")));
            areaSegura.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            areaSegura.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
            areaSegura.setRowid(cursor.getLong(ColabTrackApplication.SQL_ROW_ID_INDEX));
            return areaSegura;
        }
        return null;
    }
}
