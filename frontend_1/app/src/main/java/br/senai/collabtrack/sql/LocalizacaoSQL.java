package br.senai.collabtrack.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.dao.LocalizacaoDAO;
import br.senai.collabtrack.domain.Localizacao;
import br.senai.collabtrack.domain.Monitorado;
import br.senai.collabtrack.util.DateUtil;

/**
 * Created by kevin on 8/11/17.
 */

public class LocalizacaoSQL extends BaseSQL implements LocalizacaoDAO{

    public LocalizacaoSQL(Context context){
        super(context, CollabtrackApplication.DATABASE, null, CollabtrackApplication.DATABASE_VERSION);
    }

    @Override
    public Long save(Localizacao localizacao) {
        SQLiteDatabase db = getWritableDatabase();

        try{
            ContentValues values = new ContentValues();
            values.put("id", localizacao.getId());
            values.put("latitude", localizacao.getLatitude());
            values.put("longitude", localizacao.getLongitude());
            values.put("data", DateUtil.formatDatabase(localizacao.getData()));
            values.put("id_monitorado", localizacao.getMonitorado().getId());
            return db.insert("localizacao", "", values);
        }finally {
            db.close();
        }
    }

    @Override
    public Long update(Localizacao localizacao) {
        SQLiteDatabase db = getWritableDatabase();
        Long id = localizacao.getId();

        try{
            ContentValues values = new ContentValues();
            values.put("id", localizacao.getId());
            values.put("latitude", localizacao.getLatitude());
            values.put("longitude", localizacao.getLongitude());
            values.put("data", DateUtil.formatDatabase(localizacao.getData()));
            values.put("id_monitorado", localizacao.getMonitorado().getId());
            String[] where = new String[]{String.valueOf(id)};
            return Long.valueOf(db.update("localizacao", values, "id=?", where));
        }finally {
            db.close();
        }
    }

    @Override
    public int delete(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            return db.delete("localizacao", "id=?", new String[]{String.valueOf(id)});
        }finally {
            db.close();
        }
    }

    @Override
    public List<Localizacao> findAll() {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String sql = "SELECT l.*, m.nome, mm.cor FROM localizacao l INNER JOIN monitor_has_monitorado mm ON l.id_monitorado = mm.id_monitorado INNER JOIN monitorado m ON mm.id_monitorado = m.id ";
            Cursor c = db.rawQuery(sql, null);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public Localizacao find(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String[] where = new String[]{String.valueOf(id)};
            String sql = "SELECT l.*, m.nome, m.celular, mm.cor FROM localizacao l INNER JOIN monitor_has_monitorado mm ON l.id_monitorado = mm.id_monitorado INNER JOIN monitorado m ON mm.id_monitorado = m.id WHERE l.id = ?";
            Cursor c = db.rawQuery(sql, where);
            return parseObject(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<Localizacao> parseList(Cursor cursor) {
        List<Localizacao> localizacaoList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Localizacao localizacao = new Localizacao();
                localizacao.setId(cursor.getLong(cursor.getColumnIndex("id")));
                localizacao.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                localizacao.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                localizacao.setCor(cursor.getColumnIndex("cor") == -1 ? 0 : cursor.getInt(cursor.getColumnIndex("cor")));
                localizacao.setData(cursor.getColumnIndex("data") == -1 ? "" : DateUtil.formatBR(cursor.getString(cursor.getColumnIndex("data"))));

                Monitorado monitorado = new Monitorado();
                monitorado.setId(cursor.getLong(cursor.getColumnIndex("id_monitorado")));
                monitorado.setNome(cursor.getColumnIndex("nome") == -1 ? null : cursor.getString(cursor.getColumnIndex("nome")));
                monitorado.setCelular(cursor.getColumnIndex("celular") == -1 ? 0 : cursor.getLong(cursor.getColumnIndex("celular")));
                localizacao.setMonitorado(monitorado);

                localizacaoList.add(localizacao);
            }while (cursor.moveToNext());
        }
        return localizacaoList;
    }

    @Override
    public Localizacao parseObject(Cursor cursor) {
        if(cursor.moveToFirst()) {
            Localizacao localizacao = new Localizacao();
            localizacao.setId(cursor.getLong(cursor.getColumnIndex("id")));
            localizacao.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            localizacao.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
            localizacao.setCor(cursor.getColumnIndex("cor") == -1 ? 0 : cursor.getInt(cursor.getColumnIndex("cor")));
            localizacao.setData(cursor.getColumnIndex("data") == -1 ? "" : DateUtil.formatBR(cursor.getString(cursor.getColumnIndex("data"))));

            Monitorado monitorado = new Monitorado();
            monitorado.setId(cursor.getLong(cursor.getColumnIndex("id_monitorado")));
            monitorado.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            monitorado.setCelular(cursor.getLong(cursor.getColumnIndex("celular")));
            localizacao.setMonitorado(monitorado);

            return localizacao;
        }
        return null;
    }

    @Override
    public void deletarLocalizacoes() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM localizacao");
        db.close();
    }

    @Override
    public void deletarLocalizacoes(long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            db.delete("localizacao", "id_monitorado=?", new String[]{String.valueOf(idMonitorado)});
        }finally {
            db.close();
        }
    }

    @Override
    public List<Localizacao> findByMonitorado(long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c = db.query("localizacao", new String[] { CollabtrackApplication.SQL_ROW_ID_COLUMN, "*" }, "id_monitorado=?", new String[]{String.valueOf(idMonitorado)}, null, null, null, null);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<Localizacao> findActives() {
        SQLiteDatabase db = getWritableDatabase();
        try{
            String[] where = new String[]{"1"};
            String sql = "SELECT l.*, m.nome, m.celular, mm.cor FROM localizacao l INNER JOIN monitor_has_monitorado mm ON l.id_monitorado = mm.id_monitorado INNER JOIN monitorado m ON mm.id_monitorado = m.id WHERE mm.ativo = ? ORDER BY l.data ASC";
            Cursor c = db.rawQuery(sql, where);
            return parseList(c);
        }finally {
            db.close();
        }
    }

    @Override
    public int deleteByMonitorado(Long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            return db.delete("localizacao", "id_monitorado=?", new String[]{String.valueOf(idMonitorado)});
        }finally {
            db.close();
        }
    }

    @Override
    public List<Localizacao> findAll(long idMonitorado, int periodo, int pontos) {
        SQLiteDatabase db = getWritableDatabase();
        try{

            String data = null;
            Date date = DateUtil.getPeriodo(periodo);
            if (date != null) {
                data = DateUtil.formatDatabase(DateUtil.getPeriodo(periodo));
            }

            String sql = "SELECT lo.*, m.nome, mm.cor " +
                    "FROM localizacao lo " +
                    "INNER JOIN monitor_has_monitorado mm ON lo.id_monitorado = mm.id_monitorado " +
                    "INNER JOIN monitorado m ON mm.id_monitorado = m.id " +
                    "WHERE lo.id_monitorado = "+idMonitorado+" " +
                    (data != null ? "AND lo.data >= '" + data + "' " : "") +
                    "ORDER BY lo.id DESC " +
                    "LIMIT " + pontos;
            Cursor c = db.rawQuery(sql, null);
            return parseList(c);
        }finally {
            db.close();
        }
    }

}
