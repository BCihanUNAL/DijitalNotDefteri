package com.example.cihan.notdefteri;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class DosyaGosterActivity extends AppCompatActivity {
    private static final String TAG = "DosyaGosterActivity";
    DosyaAdapter dosyaAdapter = null;
    int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dosya_goster);

        id = getIntent().getIntExtra("ID",-1);
        RecyclerView rv = (RecyclerView)findViewById(R.id.dosyaGosterRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        dosyaAdapter = new DosyaAdapter(this,id);
        rv.addItemDecoration(new MyItemDecoration());
        rv.setAdapter(dosyaAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addfile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();


        switch(itemId){
            case R.id.menuDosyaEkle:
                Intent intent1 = new Intent(DosyaGosterActivity.this,DosyaYazActivity.class);
                intent1.putExtra("ID",id);
                startActivityForResult(intent1,8000);

                break;
            case R.id.menuMedyaEkle:
                Log.d(TAG, "onOptionsItemSelected: chosen");
                Intent intent2 = new Intent(DosyaGosterActivity.this,KayitActivity.class);
                intent2.putExtra("ID",id);
                startActivityForResult(intent2,8001);
                break;
            case R.id.menuAdresNotifikasyon:
                Intent intent4 = new Intent(this, GoogleMapActivity.class);
                intent4.putExtra("ID", id);
                Location coords = getLastKnownLocation();
                if(coords!=null) {
                    intent4.putExtra("x", coords.getLatitude());
                    intent4.putExtra("y", coords.getLongitude());
                }
                startActivity(intent4);
                break;
            case R.id.menuZamanNotifikasyon:
                Intent intent3 = new Intent(this, NotificationZamanActivity.class);
                intent3.putExtra("ID", id);
                startActivity(intent3);
                break;
        }
        Log.d(TAG, "onOptionsItemSelected: "+dosyaAdapter.getItemCount());

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

            dosyaAdapter.guncelle();
            //    dosyaAdapter.notifyItemRangeRemoved(0,dosyaAdapter.getItemCount());
            dosyaAdapter.notifyItemRangeInserted(0,dosyaAdapter.getItemCount());
            dosyaAdapter.notifyDataSetChanged();

    }

    public class MyItemDecoration extends RecyclerView.ItemDecoration {
        private final int decorationHeight;

        public MyItemDecoration() {
            decorationHeight = DosyaGosterActivity.this.getResources().getDimensionPixelSize(R.dimen.recyclerview_margin);
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
