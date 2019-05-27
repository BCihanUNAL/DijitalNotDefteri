package com.example.cihan.notdefteri;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.FileDescriptor;

public class DosyaResimGosterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosya_resim_goster);

        ImageView iv = (ImageView)findViewById(R.id.resimGosterImageView);
        String filename = getIntent().getStringExtra("isim");
        File folder = getExternalFilesDir("Media");
        File file = new File(folder,filename);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        iv.setImageBitmap(bitmap);
    }
}
