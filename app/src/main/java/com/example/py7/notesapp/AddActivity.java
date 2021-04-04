package com.example.py7.notesapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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
import java.io.InputStream;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {
    DBHelper helper;
    EditText addnpm, addnama, addjurusan;
    long id;
    ImageView imageView;
    Button addgambar;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        helper = new DBHelper(this);

        id = getIntent().getLongExtra(DBHelper.row_id, 0);

        addnpm = (EditText)findViewById(R.id.addnpm);
        addnama = (EditText)findViewById(R.id.addnama);
        addjurusan = (EditText)findViewById(R.id.addjurusan);
        addgambar = (Button)findViewById(R.id.gambar);
        imageView = (ImageView)findViewById(R.id.imageView);

        addgambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(AddActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE);
            }
        });
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

    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_add:
                String npm = addnpm.getText().toString().trim();
                String nama = addnama.getText().toString().trim();
                String jurusan = addjurusan.getText().toString().trim();
                byte[] gambar = getImageInByte(imageView);

                //Get Date
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDate = new SimpleDateFormat("MMM dd, yyyy");
                String created = simpleDate.format(calendar.getTime());

                ContentValues values = new ContentValues();
                values.put(DBHelper.row_npm, npm);
                values.put(DBHelper.row_nama, nama);
                values.put(DBHelper.row_jurusan, jurusan);
                values.put(DBHelper.gambar, gambar);
                values.put(DBHelper.row_created, created);

                //Create Condition if Title and Detail is empty
                if (npm.equals("") && nama.equals("") && jurusan.equals("") && gambar.equals("")) {
                    Toast.makeText(AddActivity.this, "Nothing to save", Toast.LENGTH_SHORT).show();
                } else {
                    helper.insertData(values);
                    Toast.makeText(AddActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
