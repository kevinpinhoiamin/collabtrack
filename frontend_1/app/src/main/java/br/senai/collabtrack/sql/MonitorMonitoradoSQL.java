package br.senai.collabtrack.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.dao.MonitorMonitoradoDAO;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.domain.MonitorMonitorado;
import br.senai.collabtrack.domain.Monitorado;

/**
 * Created by kevin on 23/06/17.
 */

public class MonitorMonitoradoSQL extends BaseSQL implements MonitorMonitoradoDAO{

    public MonitorMonitoradoSQL(Context context){
        super(context, CollabtrackApplication.DATABASE, null, CollabtrackApplication.DATABASE_VERSION);
    }

    @Override
    public Long save(MonitorMonitorado o) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("id_monitor", o.getMonitor().getId());
            values.put("id_monitorado", o.getMonitorado().getId());
            values.put("principal", o.isPrincipal() ? 1 : 0);
            values.put("ativo", o.isAtivo() ? 1 : 0);
            values.put("em_monitoramento", o.isEmMonitoramento() ? 1 : 0);
            values.put("cor", o.getCor());
            return db.insert("monitor_has_monitorado", "", values);
        }finally {
            db.close();
        }
    }

    @Override
    public Long update(MonitorMonitorado o) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("id_monitor", o.getMonitor().getId());
            values.put("id_monitorado", o.getMonitorado().getId());
            values.put("principal", o.isPrincipal() ? 1 : 0);
            values.put("ativo", o.isAtivo() ? 1 : 0);
            values.put("em_monitoramento", o.isEmMonitoramento() ? 1 : 0);
            values.put("cor", o.getCor());
            String[] where = new String[]{String.valueOf(o.getMonitor().getId()), String.valueOf(o.getMonitorado().getId())};
            return Long.valueOf(db.update("monitor_has_monitorado", values, "id_monitor=? AND id_monitorado=?", where));
        }finally {
            db.close();
        }
    }

    @Override
    public int delete(Long aLong) {
        return 0;
    }

    @Override
    public int delete(Long monitorID, Long monitoradoID) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String[] where = new String[]{String.valueOf(monitorID), String.valueOf(monitoradoID)};
            return db.delete("monitor_has_monitorado", "id_monitor=? AND id_monitorado=?", where);
        }finally {
            db.close();
        }
    }

    @Override
    public List<MonitorMonitorado> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String sql = "SELECT mm.*, m2.* FROM monitor_has_monitorado mm INNER JOIN monitor m1 ON mm.id_monitor = m1.id INNER JOIN monitorado m2 ON mm.id_monitorado = m2.id ORDER BY mm.ativo DESC, m2.nome COLLATE NOCASE ASC";
            Cursor c = db.rawQuery(sql, null);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public MonitorMonitorado find(Long aLong) {
        return null;
    }

    @Override
    public MonitorMonitorado find(Long monitorID, Long monitoradoID) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String sql = "SELECT mm.*, m2.*  FROM monitor_has_monitorado mm INNER JOIN monitor m1 ON mm.id_monitor = m1.id INNER JOIN monitorado m2 ON mm.id_monitorado = m2.id WHERE m1.id = ? AND m2.id = ?";
            Cursor c = db.rawQuery(sql, new String[] { String.valueOf(monitorID), String.valueOf(monitoradoID) });
            return parseObject(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<MonitorMonitorado> findPrincipalByMonitor(Long monitorID) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String[] where = new String[]{String.valueOf(monitorID), String.valueOf(1)};
            Cursor c = db.query("monitor_has_monitorado", new String[] { CollabtrackApplication.SQL_ROW_ID_COLUMN, "*" }, "id_monitor=? AND principal=?", where, null, null, null, null);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<MonitorMonitorado> parseList(Cursor cursor) {
        List<MonitorMonitorado> monitorMonitorados = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                MonitorMonitorado monitorMonitorado = new MonitorMonitorado();
                Monitor monitor = new Monitor();
                Monitorado monitorado = new Monitorado();

                monitor.setId(cursor.getLong(cursor.getColumnIndex("id_monitor")));
                monitorado.setId(cursor.getLong(cursor.getColumnIndex("id_monitorado")));
                monitorado.setNome(cursor.getColumnIndex("nome") == -1 ? "" : cursor.getString(cursor.getColumnIndex("nome")));
                monitorado.setCelular(cursor.getColumnIndex("celular") == -1 ? 0 : cursor.getLong(cursor.getColumnIndex("celular")));
                monitorMonitorado.setPrincipal(cursor.getInt(cursor.getColumnIndex("principal")) > 0);
                monitorMonitorado.setAtivo(cursor.getInt(cursor.getColumnIndex("ativo")) > 0);
                monitorMonitorado.setEmMonitoramento(cursor.getInt(cursor.getColumnIndex("em_monitoramento")) > 0);
                monitorMonitorado.setCor(cursor.getInt(cursor.getColumnIndex("cor")));
                monitorMonitorado.setMonitor(monitor);
                monitorMonitorado.setMonitorado(monitorado);

                monitorMonitorados.add(monitorMonitorado);
            }while (cursor.moveToNext());
        }
        return monitorMonitorados;
    }

    @Override
    public MonitorMonitorado parseObject(Cursor cursor) {
        if(cursor.moveToFirst()){
            MonitorMonitorado monitorMonitorado = new MonitorMonitorado();
            Monitor monitor = new Monitor();
            Monitorado monitorado = new Monitorado();
            monitor.setId(cursor.getLong(cursor.getColumnIndex("id_monitor")));
            monitorado.setNome(cursor.getColumnIndex("nome") == -1 ? "" : cursor.getString(cursor.getColumnIndex("nome")));
            monitorado.setCelular(cursor.getColumnIndex("celular") == -1 ? 0 : cursor.getLong(cursor.getColumnIndex("celular")));
            monitorado.setId(cursor.getLong(cursor.getColumnIndex("id_monitorado")));
            monitorMonitorado.setPrincipal(cursor.getInt(cursor.getColumnIndex("principal")) > 0);
            monitorMonitorado.setAtivo(cursor.getInt(cursor.getColumnIndex("ativo")) > 0);
            monitorMonitorado.setEmMonitoramento(cursor.getInt(cursor.getColumnIndex("em_monitoramento")) > 0);
            monitorMonitorado.setCor(cursor.getInt(cursor.getColumnIndex("cor")));
            monitorMonitorado.setMonitor(monitor);
            monitorMonitorado.setMonitorado(monitorado);

            return monitorMonitorado;
        }
        return null;
    }

    @Override
    public int deleteByMonitorado(Long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            return db.delete("monitor_has_monitorado", "id_monitorado=?", new String[]{String.valueOf(idMonitorado)});
        }finally {
            db.close();
        }
    }

}
