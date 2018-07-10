package br.senai.collabtrack.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.dao.MensagemDAO;
import br.senai.collabtrack.domain.Chat;
import br.senai.collabtrack.domain.Mensagem;

/**
 * Created by Lucas Matheus Nunes on 31/07/2017.
 */

public class MensagemSQL extends BaseSQL implements MensagemDAO{

    public MensagemSQL(Context context){
        super(context, CollabtrackApplication.DATABASE, null, CollabtrackApplication.DATABASE_VERSION);
    }

    @Override
    public Long save(Mensagem o) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            if(o.getData() != ""){
                values.put("data", o.getData());
            }else{
                values.put("data", "");
            }

            if(o.getId() > 0){
                values.put("id", o.getId());
            }

            values.put("mensagem", o.getMensagem());
            values.put("id_resposta", o.getResposta());
            values.put("tipo", o.getTipo());
            values.put("id_monitor", o.getMonitor().getId());
            values.put("id_monitorado", o.getMonitorado().getId());
            return db.insert("mensagem", "", values);
        }finally {
            db.close();
        }
    }

    public void updadeMensagem(long id, String data){
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("data", data);
            String[] where = new String[]{String.valueOf(id)};
            db.update("mensagem", values, "id = ? ", where);
        }finally {
            db.close();
        }
    }

    public Long updateId(Mensagem o, Long idAtual) {
        SQLiteDatabase db = getWritableDatabase();
        Long id = idAtual;
        try {
            ContentValues values = new ContentValues();
            if (o.getId() > 0) {
                values.put("id", o.getId());
            }
            values.put("data", o.getData());
            String[] where = new String[]{String.valueOf(id)};
            return Long.valueOf(db.update("mensagem", values, "id = ? ", where));
        } finally {
            db.close();
        }
    }

    @Override
    public Long update(Mensagem o) {
        SQLiteDatabase db = getWritableDatabase();
        Long id = o.getId();
        try{
            ContentValues values = new ContentValues();
            values.put("data", o.getData());
            String[] where = new String[]{String.valueOf(id)};
            return Long.valueOf(db.update("mensagem", values, "id = ? ", where));
        }finally {
            db.close();
        }
    }

    @Override
    public int delete(Long aLong) {
        return 0;
    }

    public  List<Chat> buscaPorMonitorado(Long id, boolean principal){
        SQLiteDatabase db = getWritableDatabase();

        List<Chat> lista = new ArrayList<Chat>();

        try{
            Cursor cursor = null;
            if(!principal){
                String[] where = new String[]{String.valueOf(id)};
                cursor = db.query("mensagem", new String[] { CollabtrackApplication.SQL_ROW_ID_COLUMN, "*" }, "id_monitorado=? AND tipo NOT IN(2,3)", where, null, null, null, null);
            }else{
                cursor = db.query("mensagem", new String[] { CollabtrackApplication.SQL_ROW_ID_COLUMN, "*" }, "id_monitorado=?", new String[]{String.valueOf(id)}, null, null, null, null);
            }
            while(cursor.moveToNext()){
                Chat c = new Chat();
                c.setId(cursor.getInt(cursor.getColumnIndex("id")));
                c.setIdUsuario(cursor.getInt(cursor.getColumnIndex("id_monitor")));
                c.setIdUsuarioEnvio(cursor.getInt(cursor.getColumnIndex("id_monitorado")));
                c.setMensagem(cursor.getString(cursor.getColumnIndex("mensagem")));
                c.setData(cursor.getString(cursor.getColumnIndex("data")));
                c.setTipo(cursor.getInt(cursor.getColumnIndex("tipo")));
                c.setIdResposta(cursor.getInt(cursor.getColumnIndex("id_resposta")));
                lista.add(c);
            }

            return  lista;
        }finally {
            db.close();
        }
    }

    @Override
    public List<Mensagem> findAll() {
        return null;
    }

    @Override
    public Mensagem find(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c = db.query("mensagem", new String[] { CollabtrackApplication.SQL_ROW_ID_COLUMN, "*" }, "id=?", new String[]{String.valueOf(id)}, null, null, null, null);
            return parseObject(c);
        }finally {
            db.close();
        }
    }

    @Override
    public List<Mensagem> parseList(Cursor cursor) { return null; }

    @Override
    public Mensagem parseObject(Cursor cursor) {
        if(cursor.moveToFirst()){
            Mensagem mensagem = new Mensagem();
            mensagem.setId(cursor.getLong(cursor.getColumnIndex("id")));
            mensagem.setMensagem(cursor.getString(cursor.getColumnIndex("mensagem")));
            mensagem.setTipo(cursor.getInt(cursor.getColumnIndex("tipo")));
            mensagem.setData(cursor.getString(cursor.getColumnIndex("data")));
            mensagem.setResposta(cursor.getInt(cursor.getColumnIndex("id_resposta")));
            return mensagem;
        }
        return null;
    }

    @Override
    public int deleteByMonitorado(Long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            return db.delete("mensagem", "id_monitorado=?", new String[]{String.valueOf(idMonitorado)});
        }finally {
            db.close();
        }
    }

    @Override
    public Mensagem findUltimaMensagem(Long idMonitorado) {
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c = db.query("mensagem", new String[] { CollabtrackApplication.SQL_ROW_ID_COLUMN, "*" }, "id_monitorado=?", new String[]{String.valueOf(idMonitorado)}, null, null, "id DESC", "1");
            return parseObject(c);
        }finally {
            db.close();
        }
    }

}
