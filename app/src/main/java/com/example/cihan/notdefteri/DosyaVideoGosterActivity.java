package com.example.cihan.notdefteri;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

public class DosyaVideoGosterActivity extends AppCompatActivity {
    private static final String TAG = "DosyaVideoGosterActivit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosya_video_goster);

        VideoView videoView = (VideoView)findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.fromFile(new File(getExternalFilesDir("Media"),getIntent().getStringExtra("isim"))));
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();
    }


}
