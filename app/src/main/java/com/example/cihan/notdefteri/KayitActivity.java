package com.example.cihan.notdefteri;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class KayitActivity extends AppCompatActivity {
    private static final String TAG = "KayitActivity";
    private final int IMAGE_CAPTURE = 100;
    private final int SOUND_RECORD = 101;
    private final int VIDEO_CAPTURE = 102;
    private static int capture;
    private int id;
    private MediaRecorder recorder = null;
    private Database database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);
        capture = 0;
        database = Database.getInstance(this);
        id = getIntent().getIntExtra("ID",-1);

        Button resim = (Button)findViewById(R.id.kayit_resimCek);
        final Button ses = (Button)findViewById(R.id.kayit_sesKaydi);
        Button video = (Button)findViewById(R.id.kayit_videoCek);

        resim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText)findViewById(R.id.kayit_dosyaIsmi)).getText().toString();
                if(name == null || name.length()==0) {
                    Toast.makeText(KayitActivity.this,"Lütfen Dosya Adı Giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri file = Uri.fromFile(getFileLocation(IMAGE_CAPTURE));
                intent.putExtra(MediaStore.EXTRA_OUTPUT,file);
                database.dosyaEkle(id,name+".jpg","jpg");

                startActivityForResult(intent,IMAGE_CAPTURE);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }

        });

        ses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(KayitActivity.this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(KayitActivity.this,"Lütfen Uygulamaya Ses Kaydı Alma İzni Verin.",Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = ((EditText)findViewById(R.id.kayit_dosyaIsmi)).getText().toString();
                if(name == null || name.length()==0) {
                    Toast.makeText(KayitActivity.this,"Lütfen Dosya Adı Giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                int YOUR_REQUEST_CODE = 200; // could be something else..
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //check if permission request is necessary
                    ActivityCompat.requestPermissions(KayitActivity.this, new String[] {android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, YOUR_REQUEST_CODE);

                try {
                    boolean lock = true;
                    if(capture == 0) {

                        recorder = new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(getFileLocation(SOUND_RECORD).toString());
                        recorder.prepare();
                        recorder.start();   // Recording is now started
                        ses.setText("Ses Kaydı Bitir");
                        capture++;
                        lock = false;
                        Toast.makeText(KayitActivity.this,"Ses Kaydı Başladı",Toast.LENGTH_SHORT).show();
                    }

                    if(capture == 1 && lock) {
                        recorder.stop();
                        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
                        recorder.release(); // Now the object cannot be reused
                        ses.setText("Ses Kaydı Başlat");
                        capture--;
                        database.dosyaEkle(id,name+".3gp","3gp");
                        Toast.makeText(KayitActivity.this,"Ses Kaydı Bitti",Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    }

                }
                catch(IOException e){

                }
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText)findViewById(R.id.kayit_dosyaIsmi)).getText().toString();
                if(name == null || name.length()==0) {
                    Toast.makeText(KayitActivity.this,"Lütfen Dosya Adı Giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                Uri file = Uri.fromFile(getFileLocation(VIDEO_CAPTURE));
                database.dosyaEkle(id,name+".mp4","mp4");

                intent.putExtra(MediaStore.EXTRA_OUTPUT,file);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
                startActivityForResult(intent,VIDEO_CAPTURE);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });


    }

    private File getFileLocation(int i){
        String extension = "";
        String name = ((EditText)findViewById(R.id.kayit_dosyaIsmi)).getText().toString();

        switch(i){
            case IMAGE_CAPTURE:
                 extension = ".jpg";
                 break;
            case SOUND_RECORD:
                 extension = ".3gp";
                 break;
            case VIDEO_CAPTURE:
                 extension = ".mp4";
                 break;
        }

        File mainFolder = getExternalFilesDir("Media");
        File mediaFolder = new File(mainFolder,name+extension);

        return mediaFolder;
    }
}
