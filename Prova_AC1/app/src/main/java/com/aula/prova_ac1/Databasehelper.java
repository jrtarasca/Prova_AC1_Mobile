package com.aula.prova_ac1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Databasehelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "contatos.db";
    private static final int VERSAO_BANCO = 1;

    public static final String TABELA_CONTATOS = "contatos";
    public static final String COL_ID = "id";
    public static final String COL_NOME = "nome";
    public static final String COL_TELEFONE = "telefone";
    public static final String COL_EMAIL = "email";
    public static final String COL_CATEGORIA = "categoria";
    public static final String COL_CIDADE = "cidade";
    public static final String COL_FAVORITO = "favorito";

    private static final String SQL_CRIAR_TABELA =
            "CREATE TABLE " + TABELA_CONTATOS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOME + " TEXT NOT NULL, " +
                    COL_TELEFONE + " TEXT NOT NULL, " +
                    COL_EMAIL + " TEXT NOT NULL, " +
                    COL_CATEGORIA + " TEXT, " +
                    COL_CIDADE + " TEXT, " +
                    COL_FAVORITO + " INTEGER DEFAULT 0" +
                    ")";

    public Databasehelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CRIAR_TABELA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int versaoNova) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_CONTATOS);
        onCreate(db);
    }

    public long inserirContato(Contato contato) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_NOME, contato.getNome());
        valores.put(COL_TELEFONE, contato.getTelefone());
        valores.put(COL_EMAIL, contato.getEmail());
        valores.put(COL_CATEGORIA, contato.getCategoria());
        valores.put(COL_CIDADE, contato.getCidade());
        valores.put(COL_FAVORITO, contato.isFavorito() ? 1 : 0);

        long id = db.insert(TABELA_CONTATOS, null, valores);
        db.close();
        return id;
    }

    public List<Contato> buscarTodos() {
        List<Contato> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABELA_CONTATOS, null, null, null,
                null, null, COL_NOME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorParaContato(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public List<Contato> buscarPorCategoria(String categoria) {
        List<Contato> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABELA_CONTATOS, null,
                COL_CATEGORIA + " = ?",
                new String[]{categoria},
                null, null, COL_NOME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorParaContato(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public List<Contato> buscarFavoritos() {
        List<Contato> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABELA_CONTATOS, null,
                COL_FAVORITO + " = 1",
                null, null, null, COL_NOME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorParaContato(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public List<Contato> buscarPorNome(String nome) {
        List<Contato> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABELA_CONTATOS, null,
                COL_NOME + " LIKE ?",
                new String[]{"%" + nome + "%"},
                null, null, COL_NOME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorParaContato(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public int atualizarContato(Contato contato) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_NOME, contato.getNome());
        valores.put(COL_TELEFONE, contato.getTelefone());
        valores.put(COL_EMAIL, contato.getEmail());
        valores.put(COL_CATEGORIA, contato.getCategoria());
        valores.put(COL_CIDADE, contato.getCidade());
        valores.put(COL_FAVORITO, contato.isFavorito() ? 1 : 0);

        int linhasAfetadas = db.update(TABELA_CONTATOS, valores,
                COL_ID + " = ?",
                new String[]{String.valueOf(contato.getId())});
        db.close();
        return linhasAfetadas;
    }

    public int excluirContato(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int linhasAfetadas = db.delete(TABELA_CONTATOS,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return linhasAfetadas;
    }

    private Contato cursorParaContato(Cursor cursor) {
        Contato c = new Contato();
        c.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        c.setNome(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOME)));
        c.setTelefone(cursor.getString(cursor.getColumnIndexOrThrow(COL_TELEFONE)));
        c.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)));
        c.setCategoria(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORIA)));
        c.setCidade(cursor.getString(cursor.getColumnIndexOrThrow(COL_CIDADE)));
        c.setFavorito(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FAVORITO)) == 1);
        return c;
    }
}

