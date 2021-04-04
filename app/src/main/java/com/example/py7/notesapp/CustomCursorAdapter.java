package com.example.py7.notesapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomCursorAdapter extends CursorAdapter {

    private LayoutInflater layoutInflater;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = layoutInflater.inflate(R.layout.row_notes, viewGroup, false);
        MyHolder holder = new MyHolder();
        holder.ListID = (TextView)v.findViewById(R.id.listID);
        holder.Listnpm = (TextView)v.findViewById(R.id.listnpm);
        holder.Listnama = (TextView)v.findViewById(R.id.listnama);
        holder.Listjurusan = (TextView)v.findViewById(R.id.listjurusan);
        holder.gambar = (ImageView)v.findViewById(R.id.listgambar);
        holder.ListCreated = (TextView)v.findViewById(R.id.listCreated);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MyHolder holder = (MyHolder)view.getTag();

        holder.ListID.setText(cursor.getString(cursor.getColumnIndex(DBHelper.row_id)));
        holder.Listnpm.setText(cursor.getString(cursor.getColumnIndex(DBHelper.row_npm)));
        holder.Listnama.setText(cursor.getString(cursor.getColumnIndex(DBHelper.row_nama)));
        holder.Listjurusan.setText(cursor.getString(cursor.getColumnIndex(DBHelper.row_jurusan)));
        holder.ListCreated.setText(cursor.getString(cursor.getColumnIndex(DBHelper.row_created)));

        byte[] imageByte = cursor.getBlob(cursor.getColumnIndex(DBHelper.gambar));
        if (imageByte != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            holder.gambar.setImageBitmap(bitmap);
        } else {
            holder.gambar.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    class MyHolder {
        TextView ListID;
        TextView Listnpm;
        TextView Listnama;
        TextView Listjurusan;
        ImageView gambar;
        TextView ListCreated;
    }
}
