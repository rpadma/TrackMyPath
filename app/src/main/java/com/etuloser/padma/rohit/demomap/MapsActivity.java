package com.etuloser.padma.rohit.demomap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private UiSettings mUiSettings;
    private LocationManager locationManager;
    ArrayList<LatLng> llist=new ArrayList<LatLng>();
    LatLngBounds.Builder b   = new LatLngBounds.Builder();;
    boolean longpress =false;
    private double lat;
    private double clat;
    private double lon;
    private double clon;
    LocationListener locationlistener;
    PolylineOptions  pOptions = new PolylineOptions();
    Polyline polygon;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
        setTitle("Tracking App");
        //setIcon(R.mipmap.ic_launcher);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings=mMap.getUiSettings();

        mMap.setOnMyLocationButtonClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //mMap.setMyLocationEnabled(true);
        //mUiSettings.setMyLocationButtonEnabled(true);
        enableMyLocation();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if(!longpress) {

                    Location mylocation=mMap.getMyLocation();

                    llist.add(new LatLng(mylocation.getLatitude(),mylocation.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(llist.get(0)).title("Start Point"));
                    pOptions.add(new LatLng(mylocation.getLatitude(),mylocation.getLongitude()));
                    b.include(llist.get(0));
                    longpress = true;

                    Toast.makeText(MapsActivity.this,"Start location tracking",Toast.LENGTH_SHORT).show();
                }else
                {
                    longpress = false;

                    Toast.makeText(MapsActivity.this,"Stop location tracking",Toast.LENGTH_SHORT).show();

                    Location mylocation=mMap.getMyLocation();

                    llist.add(new LatLng(mylocation.getLatitude(),mylocation.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(mylocation.getLatitude(),mylocation.getLongitude())).title("End Point"));

                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    pOptions.add(new LatLng(mylocation.getLatitude(),mylocation.getLongitude()));
                    b.include(new LatLng(mylocation.getLatitude(),mylocation.getLongitude()));
                    LatLngBounds bounds = b.build();

                    int width = getResources().getDisplayMetrics().widthPixels;
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 200, 5);
                    mMap.animateCamera(cu);


                }


            }
        });


    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            mUiSettings.setMyLocationButtonEnabled(true);
            mUiSettings.setZoomControlsEnabled(true);
            mUiSettings.setCompassEnabled(true);
            mUiSettings.setAllGesturesEnabled(true);
            mUiSettings.setRotateGesturesEnabled(true);

        }
    }


    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();



        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onPolygonClick(Polygon polygon) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }



    @Override
    protected void onResume() {
        super.onResume();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS Not Enabled").setMessage("Would like to enable GPS settings ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            locationlistener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("Latitude and Longitude:", location.getLatitude() + " " + location.getLongitude());

                    if(longpress) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();

                        LatLng toAdd = new LatLng(location.getLatitude(), location.getLongitude());
                        llist.add(toAdd);


                        pOptions.add(toAdd);


                        mMap.getUiSettings().setZoomControlsEnabled(true);

                        b.include(toAdd);
                        polygon = mMap.addPolyline(pOptions);
                        LatLngBounds bounds = b.build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 200, 5);
                        mMap.animateCamera(cu);

                    }

                }




                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationlistener);


        }
    }
}
