package com.example.cihan.notdefteri;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import android.os.StrictMode;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private NotAdapter adapter;
    private int colorR = 0xFF, colorG = 0xFF, colorB = 0xFF;
    private ConstraintLayout constraintLayout;
    private Database database;
    private int id;
    private Location startLocation = null;
    private List<Address> addressList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},9001);
        }

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},9002);
        }

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},9003);
        }

        database = Database.getInstance(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        ArrayList<String[]> list = new ArrayList<>();

        constraintLayout = (ConstraintLayout) findViewById(R.id.myLayout);
        adapter = new NotAdapter(this, list);
        setPreviousNotes(null);
        id = database.notEkle();

        ImageButton addNote = (ImageButton) findViewById(R.id.main_notGir);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,"Lütfen Uygulamaya Dosya Yazma İzni Verin.",Toast.LENGTH_SHORT).show();
                    return;
                }
                startLocation = getLastKnownLocation();
                if(startLocation!=null) {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocation(startLocation.getLatitude(), startLocation.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String arr[] = new String[4];
                arr[0] = ((EditText) findViewById(R.id.main_baslik)).getText().toString();
                if (arr[0].length() == 0) {
                    Toast.makeText(MainActivity.this, "Lütfen Notunuza Başlık Giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                String time[] = Calendar.getInstance().getTime().toString().split(" ");
                arr[1] = (time[2] + " " + time[1] + " " + time[5] + " " + time[3]);
                arr[2] = id + "";
                if(startLocation!=null) {
                    arr[3] = addressList.get(0).getAddressLine(0);
                }
                else{
                    arr[3] = "Adres Bilgisine Ulaşılamadı                                                          ";
                }
                database.notGuncelle(id, arr[0], arr[1], arr[3], colorR, colorG, colorB);

                adapter.notEkle(colorR, colorG, colorB, arr);
                id = database.notEkle();
            }
        });
        
        ImageButton dosyaAl = (ImageButton) findViewById(R.id.main_dosyaAl);
        ImageButton kayitAl = (ImageButton) findViewById(R.id.main_kayitAl);


        RecyclerView rv = (RecyclerView) findViewById(R.id.mainRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        rv.addItemDecoration(new MyItemDecoration());

        dosyaAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DosyaYazActivity.class);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });

        kayitAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, KayitActivity.class);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_not_ara);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: searchView");
                adapter.setValues();

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_not_ara) {
            Log.d(TAG, "onOptionsItemSelected: search selected");
            return true;
        }

        if (id == R.id.renk_beyaz) {
            colorR = 0xFF;
            colorG = 0xFF;
            colorB = 0xFF;
            constraintLayout.setBackgroundColor(Color.rgb(255, 255, 255));
            return true;
        }

        if (id == R.id.renk_kirmizi) {
            colorR = 0xFF;
            colorG = 0x00;
            colorB = 0x00;
            constraintLayout.setBackgroundColor(Color.rgb(255, 00, 00));
            return true;
        }

        if (id == R.id.renk_mavi) {
            colorR = 0x10;
            colorG = 0x10;
            colorB = 0xDF;
            constraintLayout.setBackgroundColor(Color.rgb(00, 00, 223));
            return true;
        }

        if (id == R.id.renk_sari) {
            colorR = 0xFF;
            colorG = 0xFF;
            colorB = 0x00;
            constraintLayout.setBackgroundColor(Color.rgb(255, 255, 00));
            return true;
        }

        if (id == R.id.renk_yesil) {
            colorR = 0x00;
            colorG = 0xFF;
            colorB = 0x00;
            constraintLayout.setBackgroundColor(Color.rgb(00, 255, 00));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setPreviousNotes(String tarih) {
        ArrayList<ArrayList<String>> list = database.notDondur(tarih);
        for (int i = 0; list != null && i < list.size(); i++) {
            int x[] = database.renkDondur(Integer.parseInt(list.get(i).get(0)));
            adapter.notEkle(x[0], x[1], x[2], new String[]{list.get(i).get(1), list.get(i).get(2), list.get(i).get(0),list.get(i).get(3)});
        }
    }

    public class MyItemDecoration extends RecyclerView.ItemDecoration {
        private final int decorationHeight;

        public MyItemDecoration() {
            decorationHeight = MainActivity.this.getResources().getDimensionPixelSize(R.dimen.recyclerview_margin);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent != null && view != null) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int totalCount = parent.getAdapter().getItemCount();

                if (itemPosition >= 0 && itemPosition < totalCount) {
                    outRect.bottom = decorationHeight;
                }

            }
        }
    }

    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},9001);
            return null;
        }
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        for (int i = 0; i < providers.size(); i++) {
            String provider = providers.get(i);


            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
