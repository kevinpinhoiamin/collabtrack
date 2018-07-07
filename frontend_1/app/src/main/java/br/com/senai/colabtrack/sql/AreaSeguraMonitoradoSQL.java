package br.com.senai.colabtrack.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.dao.AreaSeguraMonitoradoDAO;
import br.com.senai.colabtrack.domain.AreaSegura;
import br.com.senai.colabtrack.domain.AreaSeguraMonitorado;
import br.com.senai.colabtrack.domain.Monitorado;

/**
 * Created by kevin on 22/06/17.
 */

public class AreaSeguraMonitoradoSQL extends BaseSQL implements AreaSeguraMonitoradoDAO {

    public AreaSeguraMonitoradoSQL(Context context){
        super(context, ColabTrackApplication.DATABASE, null, ColabTrackApplication.DATABASE_VERSION);
    }

    @Override
    public Long save(AreaSeguraMonitorado o) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("id_area_segura", o.getAreaSegura().getId());
            values.put("id_monitorado", o.getMonitorado().getId());
            values.put("ativa", o.isAtiva() ? 1 : 0);
            return db.insert("area_segura_has_monitorado", "", values);
        }finally {
            db.close();
        }
    }

    @Override
    public Long update(AreaSeguraMonitorado o) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("id_area_segura", o.getAreaSegura().getId());
            values.put("id_monitorado", o.getMonitorado().getId());
            values.put("ativa", o.isAtiva() ? 1 : 0);
            String[] where = new String[]{String.valueOf(o.getAreaSegura().getId()), String.valueOf(o.getMonitorado().getId())};
            return Long.valueOf(db.update("area_segura_has_monitorado", values, "id_area_segura=? AND id_monitorado=?", where));
        }finally {
            db.close();
        }
    }

    @Override
    public int delete(Long idAreaSegura) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String where[] = new String[]{String.valueOf(idAreaSegura)};
            return db.delete("area_segura_has_monitorado", "id_area_segura=?", where);
        }finally {
            db.close();
        }
    }

    public int delete(Long idAreaSegura, Long idMonitorado){
        SQLiteDatabase db = getWritableDatabase();
        try{
            String[] where = new String[]{String.valueOf(idAreaSegura), String.valueOf(idMonitorado)};
            return db.delete("area_segura_has_monitorado", "id_area_segura=? AND id_monitorado=?", where);
        }finally {
            db.close();
        }
    }

    @Override
    public List<AreaSeguraMonitorado> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c = db.query("area_segura_has_monitorado", new String[] { ColabTrackApplication.SQL_ROW_ID_COLUMN, "*" }, null, null, null, null, null, null);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public AreaSeguraMonitorado find(Long aLong) {
        return null;
    }

    @Override
    public AreaSeguraMonitorado find(Long idAreaSegura, Long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String[] where = new String[]{String.valueOf(idAreaSegura), String.valueOf(idMonitorado)};
            Cursor c = db.query("area_segura_has_monitorado", new String[] { ColabTrackApplication.SQL_ROW_ID_COLUMN, "*" }, "id_area_segura=? AND id_monitorado=?", where, null, null, null, null);
            return parseObject(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<AreaSeguraMonitorado> findPrincipalByAreaSegura(Long idAreaSegura) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String[] where = new String[]{String.valueOf(idAreaSegura), "1"};
            String sql = "SELECT * FROM area_segura_has_monitorado am INNER JOIN monitor_has_monitorado mm ON am.id_monitorado = mm.id_monitorado WHERE am.id_area_segura = ? AND mm.principal = ?";
            Cursor c = db.rawQuery(sql, where);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<AreaSeguraMonitorado> findByAreaSegura(Long idAreaSegura) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String[] where = new String[]{String.valueOf(idAreaSegura)};
            String sql = "SELECT * FROM area_segura_has_monitorado am INNER JOIN monitor_has_monitorado mm ON am.id_monitorado = mm.id_monitorado WHERE am.id_area_segura = ?";
            Cursor c = db.rawQuery(sql, where);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<AreaSeguraMonitorado> parseList(Cursor cursor) {
        List<AreaSeguraMonitorado> areaSeguraMonitorados = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                AreaSeguraMonitorado areaSeguraMonitorado = new AreaSeguraMonitorado();
                AreaSegura areaSegura = new AreaSegura();
                Monitorado monitorado = new Monitorado();

                areaSegura.setId(cursor.getLong(cursor.getColumnIndex("id_area_segura")));
                monitorado.setId(cursor.getLong(cursor.getColumnIndex("id_monitorado")));
                areaSeguraMonitorado.setAtiva(cursor.getInt(cursor.getColumnIndex("ativa")) > 0);
                areaSeguraMonitorado.setAreaSegura(areaSegura);
                areaSeguraMonitorado.setMonitorado(monitorado);

                areaSeguraMonitorados.add(areaSeguraMonitorado);
            }while (cursor.moveToNext());
        }
        return areaSeguraMonitorados;
    }

    @Override
    public AreaSeguraMonitorado parseObject(Cursor cursor) {
        if(cursor.moveToFirst()){
            AreaSeguraMonitorado areaSeguraMonitorado = new AreaSeguraMonitorado();
            AreaSegura areaSegura = new AreaSegura();
            Monitorado monitorado = new Monitorado();

            areaSegura.setId(cursor.getLong(cursor.getColumnIndex("id_area_segura")));
            monitorado.setId(cursor.getLong(cursor.getColumnIndex("id_monitorado")));
            areaSeguraMonitorado.setAtiva(cursor.getInt(cursor.getColumnIndex("ativa")) > 0);
            areaSeguraMonitorado.setAreaSegura(areaSegura);
            areaSeguraMonitorado.setMonitorado(monitorado);

            return areaSeguraMonitorado;
        }
        return null;
    }

    @Override
    public int deleteByMonitorado(Long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            return db.delete("area_segura_has_monitorado", "id_monitorado=?", new String[]{String.valueOf(idMonitorado)});
        }finally {
            db.close();
        }
    }

}
