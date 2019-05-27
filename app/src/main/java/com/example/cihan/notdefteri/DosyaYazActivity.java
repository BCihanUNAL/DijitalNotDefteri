package com.example.cihan.notdefteri;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class DosyaYazActivity extends AppCompatActivity {
    private static final String TAG = "DosyaYazActivity";
    private String dosyaTuru="";
    private Database database;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosya_yaz);

        id = getIntent().getIntExtra("ID",-1);

        database = Database.getInstance(this);

        Button b = (Button)findViewById(R.id.dosyaKaydet);
        final RadioButton pdf = (RadioButton)findViewById(R.id.pdfYap);
        final RadioButton txt = (RadioButton)findViewById(R.id.txtYap);
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        final EditText dosyaAdi = (EditText)findViewById(R.id.dosyaAdiEditText);
        final EditText dosyaIcerik = (EditText)findViewById(R.id.dosyaYazEditText);

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dosyaTuru = ".pdf";
            }
        });

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dosyaTuru = ".txt";
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dosyaAdi.getText().toString()==null||dosyaAdi.getText().toString().length()==0){
                    Toast.makeText(DosyaYazActivity.this,"Lütfen Dosya Adı Giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dosyaTuru==null || dosyaTuru.length()==0){
                    Toast.makeText(DosyaYazActivity.this,"Dosya Tipini Seçiniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dosyaTuru.equals(".txt")){
                    String isim = dosyaAdi.getText().toString();
                    isim+=".txt";
                    String icerik=dosyaIcerik.getText().toString();
                    FileOutputStream outputStream;
                    try {
                        outputStream = new FileOutputStream(getExternalFilesDir("Documents")+"/"+isim);
                        outputStream.write(icerik.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    database.dosyaEkle(id,isim,"txt");
                }
                if(dosyaTuru.equals(".pdf")){
                    try {
                        final File file = new File(getExternalFilesDir("Documents").toString(), dosyaAdi.getText().toString()+".pdf");
                        file.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(file);
                        ArrayList<String> list = new ArrayList<>();
                        int length=50;



                        Log.d(TAG, "onClick: "+dosyaIcerik.getText().toString());
                        String delim[]=dosyaIcerik.getText().toString().split("\n"),word="";
                        for(int i=0;i<delim.length;i++){
                            String arr[] = delim[i].split(" ");
                            word = arr[0];
                            for(int j=1;j<arr.length;j++){
                                if((word+" "+arr[j]).length()>length){
                                    list.add(word);
                                    word=arr[j];
                                }
                                else{
                                    word = word + " " + arr[j];
                                }
                            }
                            list.add(word);
                        }
                        PdfDocument document = new PdfDocument();
                        int min = Math.max(15*list.size()+15,445);
                        PdfDocument.PageInfo pageInfo = new
                                PdfDocument.PageInfo.Builder(315, min, 1).create();
                        PdfDocument.Page page = document.startPage(pageInfo);
                        Canvas canvas = page.getCanvas();
                        Paint paint = new Paint();


                        for(int i=0;i<list.size();i++)
                            canvas.drawText(list.get(i), 10, 15+15*i, paint);

                        document.finishPage(page);
                        document.writeTo(fOut);
                        document.close();
                        database.dosyaEkle(id,dosyaAdi.getText().toString()+".pdf","pdf");
                    }catch (IOException e){
                        Log.i("error",e.getLocalizedMessage());
                    }

                }
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }


}
