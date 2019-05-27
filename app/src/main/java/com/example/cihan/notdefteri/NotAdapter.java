package com.example.cihan.notdefteri;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cihan on 30.04.2019.
 */

public class NotAdapter extends RecyclerView.Adapter<NotAdapter.ViewHolder> implements Filterable{
    private ArrayList<String[]> list;
    private ArrayList<Integer[]> colors;
    private ArrayList<String[]> listHolder;
    private ArrayList<Integer[]> colorsHolder;

    private Context context;
    private static final String TAG = "NotAdapter";
    private int color=0;
    private Database database = null;


    public NotAdapter(Context context,ArrayList<String[]> list){
        this.list=list;
        colors=new ArrayList<>();
        this.context=context;
        database = Database.getInstance(context);
    }

    public void setValues(){
        listHolder = new ArrayList<>(list);
        colorsHolder = new ArrayList<>(colors);
    }

    public void notEkle(int red,int green,int blue,String []arr){
        list.add(arr);
        colors.add(new Integer[]{red, green, blue});
        notifyItemInserted(list.size()-1);
        notifyItemRangeChanged(list.size()-1,getItemCount());
        color=Color.rgb(red,green,blue);
        if(listHolder!=null&&colorsHolder!=null) {
            listHolder.add(arr);
            colorsHolder.add(colors.get(colors.size() - 1));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.not,null);


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {

        holder.baslik.setText(list.get(position)[0]);
        holder.tarih.setText(list.get(position)[1]);
        holder.adres.setText(list.get(position)[3]);
        holder.myView.setBackgroundColor(Color.rgb(colors.get(position)[0],colors.get(position)[1],colors.get(position)[2]));

        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked");
                Intent intent = new Intent(context,DosyaGosterActivity.class);
                intent.putExtra("ID",Integer.parseInt(list.get(position)[2]));

                context.startActivity(intent);
            }
        });
        holder.sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.notSil(Integer.parseInt(list.get(position)[2]));
                database.dosyaSilID(Integer.parseInt(list.get(position)[2]));
                list.remove(position);
                colors.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView baslik;
        TextView tarih;
        TextView adres;
        View myView;
        ImageView sil;


        public ViewHolder(View view){
            super(view);
            myView=view;
            baslik=(TextView)view.findViewById(R.id.not_baslik);
            tarih=(TextView)view.findViewById(R.id.not_tarih);
            adres=(TextView)view.findViewById(R.id.not_adres);

            sil=(ImageButton)view.findViewById(R.id.not_sil);

        }
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private Filter myFilter = new Filter() {
        private ArrayList<Integer[]> colorList;
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<String[]> filteredList = new ArrayList<>();
            ArrayList<Integer[]> filteredColors = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                Log.d(TAG, "performFiltering: "+listHolder.size());
                filteredList.addAll(listHolder);
                filteredColors.addAll(colorsHolder);
            }
            else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(int i=0;i<listHolder.size();i++){
                    if(listHolder.get(i)[1].toLowerCase().contains(filterPattern)||listHolder.get(i)[3].toLowerCase().contains(filterPattern)){
                        filteredList.add(listHolder.get(i));
                        filteredColors.add(colorsHolder.get(i));
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            colorList = filteredColors;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((ArrayList<String[]>)filterResults.values);
            colors.clear();
            colors.addAll(colorList);
            notifyDataSetChanged();
        }
    };
}
