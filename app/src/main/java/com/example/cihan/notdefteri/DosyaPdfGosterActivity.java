package com.example.cihan.notdefteri;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutCompat;
import android.widget.ImageView;

import java.io.File;

public class DosyaPdfGosterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosya_pdf_goster);
        int width=315,height=445;
        File folder = getExternalFilesDir("Documents");
        File file = new File(folder,getIntent().getStringExtra("isim"));
        Bitmap myBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_4444);
        try {
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer.Page page = new PdfRenderer(pfd).openPage(0);
            page.render(myBitmap,null,null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            ImageView iv = (ImageView)findViewById(R.id.pdfImageView);
            iv.setImageBitmap(myBitmap);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
