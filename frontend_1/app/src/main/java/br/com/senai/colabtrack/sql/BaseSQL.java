package br.com.senai.colabtrack.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Objects;

import br.com.senai.colabtrack.ColabTrackApplication;

/**
 * Created by kevin on 25/05/17.
 */

public class BaseSQL extends SQLiteOpenHelper {

    protected Context context;

    public BaseSQL(Context context, String database, Object o, Integer databaseVersion){
        super(context, ColabTrackApplication.DATABASE, null, ColabTrackApplication.DATABASE_VERSION);
        this.context = context;
    }

    // https://www.sqlite.org/datatype3.html
    // https://www.sqlite.org/lang_datefunc.html

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Monitor
        db.execSQL("CREATE TABLE IF NOT EXISTS monitor (" +
                    "id INTEGER NOT NULL, " +
                    "nome VARCHAR(50) NOT NULL, " +
                    "celular INTEGER NOT NULL, " +
                    "token_autenticacao VARCHAR(6))");
        // Monitorado
        db.execSQL("CREATE TABLE IF NOT EXISTS monitorado (" +
                    "id INTEGER NOT NULL, " +
                    "nome VARCHAR(50) NOT NULL, " +
                    "celular INTEGER NOT NULL)");
        // Relação entre monitor e monitorado
        db.execSQL("CREATE TABLE IF NOT EXISTS monitor_has_monitorado (" +
                    "id_monitor INTEGER, " +
                    "id_monitorado INTEGER, " +
                    "principal INT(1) NOT NULL, "+
                    "ativo INT(1) NOT NULL, "+
                    "em_monitoramento INT(1) NOT NULL, " +
                    "cor INTEGER NOT NULL, " +
                    "FOREIGN KEY(id_monitor) REFERENCES monitor(id), " +
                    "FOREIGN KEY(id_monitorado) REFERENCES monitorado(id))");
        // Área segura
        db.execSQL("CREATE TABLE IF NOT EXISTS area_segura (" +
                    "id INTEGER, " +
                    "nome VARCHAR(45) NOT NULL, " +
                    "cor INTEGER NOT NULL, " +
                    "cor_borda INTEGER NOT NULL, " +
                    "raio INTEGER NOT NULL, " +
                    "latitude DOUBLE NOT NULL, " +
                    "longitude DOUBLE NOT NULL)");
        // Relção entre área segura e monitorado
        db.execSQL("CREATE TABLE IF NOT EXISTS area_segura_has_monitorado (" +
                    "id_area_segura INTEGER NOT NULL, " +
                    "id_monitorado INTEGER NOT NULL, " +
                    "ativa INT(1) NOT NULL, " +
                    "FOREIGN KEY(id_area_segura) REFERENCES area_segura(id), " +
                    "FOREIGN KEY(id_monitorado) REFERENCES monitorado(id))");
        // Mensagem
        db.execSQL("CREATE TABLE IF NOT EXISTS mensagem (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "data DATETIME NOT NULL, " +
                "mensagem VARCHAR(255) NOT NULL, " +
                "id_resposta INTEGER, " +
                "tipo INTEGER," +
                "id_monitor INTEGER NOT NULL," +
                "id_monitorado INTEGER NOT NULL," +
                "FOREIGN KEY(id_monitor) REFERENCES monitor(id)," +
                "FOREIGN KEY(id_monitorado) REFERENCES monitorado(id))");
        // Status
        db.execSQL("CREATE TABLE IF NOT EXISTS status (" +
                    "id INTEGER NOT NULL, " +
                    "wifi INT(1) NOT NULL, " +
                    "internet_movel INT(1) NOT NULL, " +
                    "localizacao INT(1) NOT NULL, " +
                    "bateria INT(1)," +
                    "data VARCHAR(19) NOT NULL," +
                    "id_monitorado INTEGER NOT NULL," +
                    "FOREIGN KEY(id_monitorado) REFERENCES monitorado(id))");
        // Localização
        db.execSQL("CREATE TABLE IF NOT EXISTS localizacao (" +
                    "id INTEGER NOT NULL, " +
                    "latitude DOUBLE NOT NULL, " +
                    "longitude DOUBLE NOT NULL, " +
                    "data VARCHAR(19) NOT NULL, " +
                    "id_monitorado INTEGER NOT NULL," +
                    "FOREIGN KEY(id_monitorado) REFERENCES monitorado(id))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
