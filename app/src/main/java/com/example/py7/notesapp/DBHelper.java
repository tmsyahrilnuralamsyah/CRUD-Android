package com.example.py7.notesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileInputStream;
import java.io.IOException;

public class DBHelper extends SQLiteOpenHelper {

    public static final String database_name = "db_note";
    public static final String table_name = "tabel_notes";

    public static final String row_id = "_id";
    public static final String row_npm = "NPM";
    public static final String row_nama = "Nama";
    public static final String row_jurusan = "Jurusan";
    public static final String gambar = "Gambar";
    public static final String row_created = "Created";

    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, database_name, null, 2);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + table_name + "(" + row_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + row_npm + " TEXT, " + row_nama + " TEXT, "+ row_jurusan + " TEXT, " + gambar + " BLOB, " + row_created + " TEXT)";
        db.execSQL(query);
    }

    public Boolean insertGambar(String x, Integer i) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            FileInputStream fs = new FileInputStream(x);
            byte[] imgbyte = new byte[fs.available()];
            fs.read(imgbyte);
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", i);
            contentValues.put("img", imgbyte);
            db.insert("images", null, contentValues);
            fs.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int x) {
        db.execSQL("DROP TABLE IF EXISTS " + table_name);
    }

    //Get All SQLite Data
    public Cursor allData() {
        Cursor cur = db.rawQuery("SELECT * FROM " + table_name + " ORDER BY " + row_id + " DESC ", null);
        return cur;
    }

    //GET 1 DATA By ID
    public Cursor oneData(Long id) {
        Cursor cur = db.rawQuery("SELECT * FROM " + table_name + " WHERE " + row_id + "=" + id, null);
        return cur;
    }

    //Insert Data
    public void insertData(ContentValues values) {
        db.insert(table_name, null, values);
    }

    //Update Data
    public void updateData(ContentValues values, long id) {
        db.update(table_name, values, row_id + "=" + id, null);
    }

    //Delete Data
    public void deleteData(long id) {
        db.delete(table_name, row_id + "=" + id, null);
    }
}
