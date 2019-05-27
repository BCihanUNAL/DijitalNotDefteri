package com.example.cihan.notdefteri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class dosyaTxtGosterActivity extends AppCompatActivity {
    private static final String TAG = "dosyaTxtGosterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosya_txt_goster);
        String fileName = getIntent().getStringExtra("isim"),text="";
        File folder = getExternalFilesDir("Documents");
        File file = new File(folder,fileName);
        try {
            FileInputStream fs = new FileInputStream(file);
            byte buffer[] = new byte[1024];
            int offset = 0;
            while(true) {
                int charsRead = fs.read(buffer, offset, 1024);
                offset+=1024;
                text+=new String(buffer);
                if(charsRead != 1024){
                    break;
                }
                fs.close();
                Log.d(TAG, "onCreate: "+text);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            TextView tv = findViewById(R.id.txtTextView);
            tv.setMovementMethod(new ScrollingMovementMethod());
            tv.setText(text);
        }
    }
}
