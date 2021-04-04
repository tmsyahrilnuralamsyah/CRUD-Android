package com.example.py7.notesapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

public class EditActivity extends AppCompatActivity {
    DBHelper helper;
    EditText editnpm, editnama, editjurusan;
    long id;
    ImageView imageView;
    Button editgambar;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        helper = new DBHelper(this);

        id = getIntent().getLongExtra(DBHelper.row_id, 0);

        editnpm = (EditText)findViewById(R.id.editnpm);
        editnama = (EditText)findViewById(R.id.editnama);
        editjurusan = (EditText)findViewById(R.id.editjurusan);
        editgambar = (Button)findViewById(R.id.gambar);
        imageView = (ImageView)findViewById(R.id.imageView2);

        editgambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(EditActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE);
            }
        });

        getData();
    }

    private void getData() {
        Cursor cursor = helper.oneData(id);
        if (cursor.moveToFirst()) {
            String npm = cursor.getString(cursor.getColumnIndex(DBHelper.row_npm));
            String nama = cursor.getString(cursor.getColumnIndex(DBHelper.row_nama));
            String jurusan = cursor.getString(cursor.getColumnIndex(DBHelper.row_jurusan));
            byte[] imageByte = cursor.getBlob(cursor.getColumnIndex(DBHelper.gambar));

            editnpm.setText(npm);
            editnama.setText(nama);
            editjurusan.setText(jurusan);
            if (imageByte != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }

    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_edit:
                String npm = editnpm.getText().toString().trim();
                String nama = editnama.getText().toString().trim();
                String jurusan = editjurusan.getText().toString().trim();
                byte[] gambar = getImageInByte(imageView);

                ContentValues values = new ContentValues();
                values.put(DBHelper.row_npm, npm);
                values.put(DBHelper.row_nama, nama);
                values.put(DBHelper.row_jurusan, jurusan);
                values.put(DBHelper.gambar, gambar);

                if (npm.equals("") && nama.equals("") && jurusan.equals("") && gambar.equals("")) {
                    Toast.makeText(EditActivity.this, "Nothing to save", Toast.LENGTH_SHORT).show();
                } else {
                    helper.updateData(values, id);
                    Toast.makeText(EditActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
        switch (item.getItemId()) {
            case R.id.delete_edit:
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setMessage("This note will be deleted.");
                builder.setCancelable(true);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper.deleteData(id);
                        Toast.makeText(EditActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PICK_IMAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied!", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public byte[] getImageInByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
