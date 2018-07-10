package br.senai.collabtrack.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.dao.StatusDAO;
import br.senai.collabtrack.domain.Monitorado;
import br.senai.collabtrack.domain.Status;

/**
 * Created by kevin on 8/8/17.
 */

public class StatusSQL extends BaseSQL implements StatusDAO{

    public StatusSQL(Context context){
        super(context, CollabtrackApplication.DATABASE, null, CollabtrackApplication.DATABASE_VERSION);
    }

    @Override
    public Long save(Status status) {
        SQLiteDatabase db = getWritableDatabase();

        try{
            ContentValues values = new ContentValues();
            values.put("id", status.getId());
            values.put("wifi", status.isWifi() ? 1 : 0);
            values.put("internet_movel", status.isInternetMovel() ? 1 : 0);
            values.put("localizacao", status.isLocalizacao() ? 1 : 0);
            values.put("bateria", status.getBateria());
            values.put("data", status.getData() == null ? "" : status.getData());
            values.put("id_monitorado", status.getMonitorado().getId());
            return db.insert("status", "", values);
        }finally {
            db.close();
        }
    }

    @Override
    public Long update(Status status) {
        SQLiteDatabase db = getWritableDatabase();
        Long id = status.getId();

        try{
            ContentValues values = new ContentValues();
            values.put("id", status.getId());
            values.put("wifi", status.isWifi() ? 1 : 0);
            values.put("internet_movel", status.isInternetMovel() ? 1 : 0);
            values.put("localizacao", status.isLocalizacao() ? 1 : 0);
            values.put("bateria", status.getBateria());
            values.put("data", status.getData() == null ? "" : status.getData());
            values.put("id_monitorado", status.getMonitorado().getId());
            String[] where = new String[]{String.valueOf(id)};
            return Long.valueOf(db.update("status", values, "id=?", where));
        }finally {
            db.close();
        }
    }

    @Override
    public int delete(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            return db.delete("status", "id=?", new String[]{String.valueOf(id)});
        }finally {
            db.close();
        }
    }

    @Override
    public List<Status> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String sql = "SELECT DISTINCT mm.ativo, mm.principal, mm.em_monitoramento, mm.cor, s.*, m.nome, m.celular FROM status s INNER JOIN monitorado m ON s.id_monitorado = m.id INNER JOIN monitor_has_monitorado mm ON m.id = mm.id_monitorado GROUP BY m.id ORDER BY mm.ativo DESC, m.nome COLLATE NOCASE ASC";
            Cursor c = db.rawQuery(sql, null);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public Status find(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c = db.query("status", new String[] { CollabtrackApplication.SQL_ROW_ID_COLUMN, "*" }, "id=?", new String[]{String.valueOf(id)}, null, null, null, null);
            return parseObject(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<Status> parseList(Cursor cursor) {
        List<Status> statusList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Status status = new Status();
                status.setId(cursor.getLong(cursor.getColumnIndex("id")));
                status.setWifi(cursor.getInt(cursor.getColumnIndex("wifi")) > 0);
                status.setInternetMovel(cursor.getInt(cursor.getColumnIndex("internet_movel")) > 0);
                status.setLocalizacao(cursor.getInt(cursor.getColumnIndex("localizacao")) > 0);
                status.setBateria(cursor.getInt(cursor.getColumnIndex("bateria")));
                status.setData(cursor.getString(cursor.getColumnIndex("data")));
                status.setAtivo(cursor.getColumnIndex("ativo") == -1 ? false : cursor.getInt(cursor.getColumnIndex("ativo")) > 0);
                status.setPrincipal(cursor.getColumnIndex("principal") == -1 ? false : cursor.getInt(cursor.getColumnIndex("principal")) > 0);
                status.setEmMonitoramento(cursor.getColumnIndex("em_monitoramento") == -1 ? false : cursor.getInt(cursor.getColumnIndex("em_monitoramento")) > 0);
                status.setCor(cursor.getColumnIndex("cor") == -1 ? 0 : cursor.getInt(cursor.getColumnIndex("cor")));
                status.setMonitorado(new Monitorado());
                status.getMonitorado().setId(cursor.getLong(cursor.getColumnIndex("id_monitorado")));
                status.getMonitorado().setNome(cursor.getColumnIndex("nome") == -1 ? "" : cursor.getString(cursor.getColumnIndex("nome")));
                status.getMonitorado().setCelular(cursor.getColumnIndex("celular") == -1 ? 0 : cursor.getLong(cursor.getColumnIndex("celular")));
                statusList.add(status);
            }while (cursor.moveToNext());
        }
        return statusList;
    }

    @Override
    public Status parseObject(Cursor cursor) {
        if(cursor.moveToFirst()) {
            Status status = new Status();
            status.setId(cursor.getLong(cursor.getColumnIndex("id")));
            status.setWifi(cursor.getInt(cursor.getColumnIndex("wifi")) > 0);
            status.setInternetMovel(cursor.getInt(cursor.getColumnIndex("internet_movel")) > 0);
            status.setLocalizacao(cursor.getInt(cursor.getColumnIndex("localizacao")) > 0);
            status.setBateria(cursor.getInt(cursor.getColumnIndex("bateria")));
            status.setData(cursor.getString(cursor.getColumnIndex("data")));
            status.setAtivo(cursor.getColumnIndex("ativo") == -1 ? false : cursor.getInt(cursor.getColumnIndex("ativo")) > 0);
            status.setPrincipal(cursor.getColumnIndex("principal") == -1 ? false : cursor.getInt(cursor.getColumnIndex("principal")) > 0);
            status.setEmMonitoramento(cursor.getColumnIndex("em_monitoramento") == -1 ? false : cursor.getInt(cursor.getColumnIndex("em_monitoramento")) > 0);
            status.setCor(cursor.getColumnIndex("cor") == -1 ? 0 : cursor.getInt(cursor.getColumnIndex("cor")));
            status.setMonitorado(new Monitorado());
            status.getMonitorado().setId(cursor.getLong(cursor.getColumnIndex("id_monitorado")));
            status.getMonitorado().setNome(cursor.getColumnIndex("nome") == -1 ? "" : cursor.getString(cursor.getColumnIndex("nome")));
            status.getMonitorado().setCelular(cursor.getColumnIndex("celular") == -1 ? 0 : cursor.getLong(cursor.getColumnIndex("celular")));
            return status;
        }
        return null;
    }

    @Override
    public Status findByMonitorado(Long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String[] where = new String[]{String.valueOf(idMonitorado)};
            String sql = "SELECT mm.ativo, mm.principal, mm.em_monitoramento, mm.cor, s.*, m.nome FROM status s INNER JOIN monitor_has_monitorado mm ON s.id_monitorado = mm.id_monitorado INNER JOIN monitorado m ON mm.id_monitorado = m.id WHERE mm.id_monitorado = ? GROUP BY m.id";
            Cursor c = db.rawQuery(sql, where);
            return parseObject(c);
        }finally {
            db.close();
        }
    }

    @Override
    public int deleteByMonitorado(Long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            return db.delete("status", "id_monitorado=?", new String[]{String.valueOf(idMonitorado)});
        }finally {
            db.close();
        }
    }

}
