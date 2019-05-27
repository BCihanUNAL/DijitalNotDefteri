package com.example.cihan.notdefteri;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by cihan on 11.05.2019.
 */

public class Database extends SQLiteOpenHelper{
    private static final String TAG = "Database";
    private static Database database = null;
    private Context context;

    public Database(Context context){
        super(context,"Not_Defteri.db",null,1);
        Log.d(TAG, "Database: created");
        this.context = context;
    }

    public static Database getInstance(Context context){
        if(database == null){
            database = new Database(context);
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_not,create_document,create_date,create_location;

        create_not = "CREATE TABLE NOTLAR( " +
                     "ID INT, " +
                     "ISIM VARCHAR(30), " +
                     "TARIH VARCHAR(30), " +
                     "ADRES VARCHAR(100), " +
                     "RED INT, " +
                     "GREEN INT, " +
                     "BLUE INT, " +
                     "CONSTRAINT PRIM PRIMARY KEY(ID));";

        create_document = "CREATE TABLE DOCUMENT( " +
                          "ID INT, " +
                          "NAME VARCHAR(30), " +
                          "TYPE VARCHAR(30), " +
                          "CONSTRAINT FORE FOREIGN KEY(ID) REFERENCES NOTLAR(ID));";

        sqLiteDatabase.execSQL(create_not);
        sqLiteDatabase.execSQL(create_document);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int notEkle(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(true,"NOTLAR",new String[]{"MAX(ID)"},null,null,null,null,null,null);
        cursor.moveToFirst();
        int id;

        if(cursor.getCount() == 0){
            id = 1;
        }
        else{
            id=cursor.getInt(0)+1;
        }

        String insert = "insert into notlar (ID) values (%d);";
        db.execSQL(String.format(insert,id));
        return id;
    }

    /*public void notButunSil(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from NOTLAR;");
    }HATALI*/

    public void notSil(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from document where id="+id+";");
        db.execSQL("delete from notlar where id="+id+";");
        db.execSQL("insert into notlar (ID) values ("+id+");");
        Log.d(TAG, "notSil: "+id+" silindi");
    }

    public void dosyaEkle(int id,String name,String type){
        SQLiteDatabase db = getWritableDatabase();
        String insert = "insert into document values(%d,'%s','%s');";
        Log.d(TAG, "dosyaEkle: "+id+" "+name+" "+type);
        db.execSQL(String.format(insert,id,name,type));
    }

    public ArrayList<String> dosyaDondurVideo(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("DOCUMENT",new String[]{"NAME"},"ID="+id+" and TYPE='mp4'",null,null,null,null);
        boolean cont = cursor.moveToFirst();
        ArrayList <String> list = new ArrayList<>();
        if(!cont)
            return null;
        while(cont){
            list.add(cursor.getString(0));
            cont = cursor.moveToNext();
        }
        return list;
    }
    public ArrayList<String> dosyaDondurSeskayit(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("DOCUMENT",new String[]{"NAME"},"ID="+id+" and TYPE='3gp'",null,null,null,null);
        boolean cont = cursor.moveToFirst();
        ArrayList <String> list = new ArrayList<>();
        if(!cont)
            return null;
        while(cont){
            list.add(cursor.getString(0));
            cont = cursor.moveToNext();
        }
        return list;
    }
    public ArrayList<String> dosyaDondurResim(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("DOCUMENT",new String[]{"NAME"},"ID="+id+" and TYPE='jpg'",null,null,null,null);
        boolean cont = cursor.moveToFirst();
        if(!cont)
            return null;
        ArrayList <String> list = new ArrayList<>();
        while(cont){
            list.add(cursor.getString(0));
            cont = cursor.moveToNext();
        }
        return list;
    }
    public ArrayList<String> dosyaDondurPDF(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("DOCUMENT",new String[]{"NAME"},"ID="+id+" and TYPE='pdf'",null,null,null,null);
        boolean cont = cursor.moveToFirst();
        if(!cont)
            return null;
        ArrayList <String> list = new ArrayList<>();
        while(cont){
            list.add(cursor.getString(0));
            cont = cursor.moveToNext();
        }
        return list;
    }
    public ArrayList<String> dosyaDondurTXT(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("DOCUMENT",new String[]{"NAME"},"ID="+id+" and TYPE='txt'",null,null,null,null);
        boolean cont = cursor.moveToFirst();
        if(!cont)
            return null;
        ArrayList <String> list = new ArrayList<>();
        while(cont){
            list.add(cursor.getString(0));
            cont = cursor.moveToNext();
        }
        return list;
    }

    public void dosyaSilID(int id){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query("Document",new String[]{"NAME"},"id="+id,null,null,null,null);
        boolean cont = cursor.moveToFirst();
        File folder = context.getExternalFilesDir("Media");
        while(cont){
            File file = new File(folder,cursor.getString(0));
            file.delete();
            cont = cursor.moveToNext();
        }
        db.execSQL("delete from document where id="+id+";");
    }

    public void dosyaSilTek(String isim){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from document where name='"+isim+"';");
    }

    public void notGuncelle(int id, String isim, String tarih,String adres, int red, int green, int blue){
        SQLiteDatabase db = getWritableDatabase();
        String update = "update notlar set isim='"+isim+"', tarih='"+tarih+"', adres='"+adres+"', RED="+red+", GREEN="+green+", BLUE="+blue+" where id="+id+";";
        db.execSQL(update);
        Log.d(TAG, "notEkle: "+id+" eklendi");
    }

    public ArrayList<ArrayList<String>> notDondur(String s){
        SQLiteDatabase db =getReadableDatabase();
        Cursor cursor;
        if(s == null)
            cursor = db.query("NOTLAR",null,"ISIM IS NOT NULL",null,null,null,null);
        else
            cursor = db.query("NOTLAR",null,"ISIM IS NOT NULL and TARIH='"+s+"'",null,null,null,null);
        boolean cont = cursor.moveToNext();
        Log.d(TAG, "notDondur: "+cursor.getCount());
        if(!cont)
            return null;
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        while(cont){
            ArrayList<String> arr = new ArrayList<>();
            arr.add(cursor.getInt(0)+"");
            arr.add(cursor.getString(1));
            arr.add(cursor.getString(2));
            arr.add(cursor.getString(3));
            list.add(arr);
            cont = cursor.moveToNext();
        }
        return list;
    }

    public int[] renkDondur(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("NOTLAR",new String[]{"RED","GREEN","BLUE"},"ID="+id,null,null,null,null);
        cursor.moveToFirst();
        return new int[]{cursor.getInt(0),cursor.getInt(1),cursor.getInt(2)};
    }

    public String notAdiDondur(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("NOTLAR",new String[]{"ISIM"},"ID="+id,null,null,null,null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

}
