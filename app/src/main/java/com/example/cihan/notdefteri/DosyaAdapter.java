package com.example.cihan.notdefteri;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by cihan on 11.05.2019.
 */

public class DosyaAdapter extends RecyclerView.Adapter<DosyaAdapter.ViewHolder> {
    private Database database = null;
    private ArrayList<String> isimPDF = null, isimTXT = null, isimSes = null, isimVideo = null, isimResim = null,hepsi = null;
    private Context context = null;
    private int id;
    private static final String TAG = "DosyaAdapter";

    public DosyaAdapter(Context context,int id){
        this.context = context;
        this.id = id;
        database = Database.getInstance(context);
        Log.d(TAG, "DosyaAdapter: started");
        guncelle();
        Log.d(TAG, "DosyaAdapter: finished");
    }

    public void guncelle(){
        isimPDF = database.dosyaDondurPDF(id);
        isimTXT = database.dosyaDondurTXT(id);
        isimSes = database.dosyaDondurSeskayit(id);
        isimVideo = database.dosyaDondurVideo(id);
        isimResim = database.dosyaDondurResim(id);

        hepsi = new ArrayList<>();
        for(int i=0;isimPDF!=null&&i<isimPDF.size();i++){
            hepsi.add(isimPDF.get(i));
        }
        for(int i=0;isimTXT!=null&&i<isimTXT.size();i++){
            hepsi.add(isimTXT.get(i));
        }
        for(int i=0;isimResim!=null&&i<isimResim.size();i++){
            hepsi.add(isimResim.get(i));
        }
        for(int i=0;isimSes!=null&&i<isimSes.size();i++){
            hepsi.add(isimSes.get(i));
        }
        for(int i=0;isimVideo!=null&&i<isimVideo.size();i++){
            hepsi.add(isimVideo.get(i));
        }
        Log.d(TAG, "guncelle: "+hepsi.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.dosya,null);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.isim.setText(hepsi.get(position));
        holder.sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String dosyaAdi[] = hepsi.get(position).split("\\.");
                    File folder;
                    if(dosyaAdi[1].equals("txt")||dosyaAdi[1].equals("pdf"))
                        folder = context.getExternalFilesDir("Documents");
                    else
                        folder = context.getExternalFilesDir("Media");
                    File file = new File(folder, hepsi.get(position));
                    file.delete();
                    database.dosyaSilTek(hepsi.get(position));
                    hepsi.remove(position);
                    notifyItemRemoved(position);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dosyaAdi[] = hepsi.get(position).split("\\.");
                Log.d(TAG, "onClick: "+dosyaAdi.length);
                if(dosyaAdi[1].equals("txt")){
                    Intent intent = new Intent(context,dosyaTxtGosterActivity.class);
                    intent.putExtra("isim",hepsi.get(position));
                    context.startActivity(intent);
                }
                if(dosyaAdi[1].equals("pdf")){
                    Intent intent = new Intent(context,DosyaPdfGosterActivity.class);
                    intent.putExtra("isim",hepsi.get(position));
                    context.startActivity(intent);
                }
                if(dosyaAdi[1].equals("jpg")){
                    Intent intent = new Intent(context,DosyaResimGosterActivity.class);
                    intent.putExtra("isim",hepsi.get(position));
                    context.startActivity(intent);
                }
                if(dosyaAdi[1].equals("mp4")){
                    Intent intent = new Intent(context,DosyaVideoGosterActivity.class);
                    intent.putExtra("isim",hepsi.get(position));
                    context.startActivity(intent);
                }
                if(dosyaAdi[1].equals("3gp")){
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.fromFile(new File(context.getExternalFilesDir("Media"),hepsi.get(position))));
                    mediaPlayer.start();
                }

                Toast.makeText(context,hepsi.get(position),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return hepsi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView isim;
        ImageButton sil;
        View myView;
        public ViewHolder(View view) {
            super(view);
            myView = view;
            sil = (ImageButton)view.findViewById(R.id.dosya_sil);
            isim = (TextView)view.findViewById(R.id.dosya_isim);
        }
    }
}
