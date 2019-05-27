package com.example.cihan.notdefteri;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static GeofencingClient geofencingClient = null;
    private Database database = null;
    private static final String TAG = "GoogleMapActivity";
    private static PendingIntent pendingIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        database = Database.getInstance(this);
        geofencingClient = LocationServices.getGeofencingClient(GoogleMapActivity.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng coords = mMap.getCameraPosition().target;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(coords).title("MyMarker"));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                new AlertDialog.Builder(GoogleMapActivity.this)
                        .setTitle("Hatırlatma Oluştur")
                        .setMessage("İşaretçinin gösterdiği yerde hatırlatma oluşturmak istiyormusunuz?")
                        .setPositiveButton(R.string.evet, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Geofence geofence = new Geofence.Builder()
                                        .setRequestId(getIntent().getIntExtra("ID", -1) + "")
                                        .setCircularRegion(marker.getPosition().latitude, marker.getPosition().longitude, 50.0f)
                                        .setExpirationDuration(90000000L)
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build();

                                GeofencingRequest.Builder builder1 = new GeofencingRequest.Builder()
                                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                                        .addGeofence(geofence);
                                GeofencingRequest gr = builder1.build();

                                Intent intent = new Intent(GoogleMapActivity.this, GeofenceTransition.class);
                                intent.putExtra("isim",database.notAdiDondur(getIntent().getIntExtra("ID",-1)));
                                pendingIntent = PendingIntent.getService(GoogleMapActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                if (ActivityCompat.checkSelfPermission(GoogleMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                geofencingClient.addGeofences(gr, pendingIntent);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.hayir, null)
                        .show();

                return false;
            }
        });
        double x = getIntent().getDoubleExtra("x",41.750);
        double y = getIntent().getDoubleExtra("y",28.89);
        LatLng Istanbul = new LatLng(x, y);
        mMap.addMarker(new MarkerOptions().position(Istanbul).title("MyMarker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Istanbul));
    }

    public static void remove(){
        if(pendingIntent != null)
            geofencingClient.removeGeofences(pendingIntent);
    }
}
