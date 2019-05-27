package com.example.cihan.notdefteri;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

public class NotificationZamanActivity extends AppCompatActivity {
    private static final String TAG = "NotificationZamanActivi";
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private String hatirlatmaTarih = "";
    private String hatirlatmaSaat = "";
    private TextView tarihTv;
    private TextView saatTv;
    private int id;
    private Database database;
    private EditText aralik;
    private EditText adet;
    private AlarmManager alarmManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_zaman);
        database = Database.getInstance(this);

        adet = (EditText)findViewById(R.id.adetEditText);
        aralik = (EditText)findViewById(R.id.aralikEditText);

        final Button tarih = (Button)findViewById(R.id.tarihButton);
        Button saat = (Button)findViewById(R.id.saatButton);
        final Button hatirlatma = (Button)findViewById(R.id.zamanHatirlatmaButton);

        tarihTv = (TextView)findViewById(R.id.tarihTextView);
        saatTv = (TextView)findViewById(R.id.saatTextView);
        id = getIntent().getIntExtra("ID",-1);

        tarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int gun = calendar.get(Calendar.DAY_OF_MONTH);
                int ay = calendar.get(Calendar.MONTH);
                int yil = calendar.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(NotificationZamanActivity.this,
                            android.R.style.Theme_Holo_Dialog_MinWidth,
                            dateSetListener,yil,ay,gun);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                hatirlatmaTarih = i2+"/"+i1+"/"+i;
                tarihTv.setText(hatirlatmaTarih);
                tarihTv.setVisibility(View.VISIBLE);
            }
        };

        saat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int saat = calendar.get(Calendar.HOUR);
                int dakika = calendar.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(NotificationZamanActivity.this,
                                        android.R.style.Theme_Holo_Dialog_MinWidth,
                                        timeSetListener,
                                        saat,dakika,false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hatirlatmaSaat = i+":"+i1;
                saatTv.setText(hatirlatmaSaat);
                Log.d(TAG, "onTimeSet: "+hatirlatmaSaat);
            }
        };

        hatirlatma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hatirlatmaSaat.length() == 0 || hatirlatmaTarih.length() == 0){
                    Toast.makeText(NotificationZamanActivity.this,"Hatırlatmanın tarihini ve zamanını giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                String date[] = hatirlatmaTarih.split("/");
                String hour[] = hatirlatmaSaat.split(":");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hour[0]));
                calendar.set(Calendar.MINUTE,Integer.parseInt(hour[1]));
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(date[0]));
                calendar.set(Calendar.MONTH,Integer.parseInt(date[1]));
                calendar.set(Calendar.YEAR,Integer.parseInt(date[2]));

                updateTimeText(calendar);
                try {
                    startAlarm(calendar);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    finish();
                }
            }
        });
    }
    private void updateTimeText(Calendar c){
        String timeText = "Alarm zamanı = ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        Toast.makeText(this,timeText,Toast.LENGTH_SHORT).show();
    }
    private void startAlarm(Calendar c) throws PendingIntent.CanceledException{
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int ara = Integer.parseInt("0"+aralik.getText().toString());
        int kac = Integer.parseInt("0"+adet.getText().toString());
        Intent intent = new Intent(this,AlertKarsila.class);
        intent.putExtra("isim",database.notAdiDondur(id));
        intent.putExtra("saat",saatTv.getText().toString());
        intent.putExtra("select",0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,234324234,intent,0);
        AlertKarsila.kac=kac;
        AlertKarsila.pIntent = pendingIntent;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),(long)ara*60000L,pendingIntent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tarihTv.setText(savedInstanceState.getString("tarihTv"));
        saatTv.setText(savedInstanceState.getString("saatTv"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tarihTv",tarihTv.getText().toString());
        outState.putString("saatTv",saatTv.getText().toString());
        super.onSaveInstanceState(outState);
    }


}
