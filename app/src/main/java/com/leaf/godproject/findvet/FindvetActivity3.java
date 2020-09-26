package com.leaf.godproject.findvet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leaf.godproject.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FindvetActivity3 extends AppCompatActivity implements OnMapReadyCallback, SearchView.OnQueryTextListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 2;
    private Toolbar mToolbar;
    private static String TAG = "MapsActivity";
    Geocoder geocoder = null;
    private FloatingActionButton ib;
    SearchView searchView;

    LocationManager mLocationManager;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 22;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findvet2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleplacesinitialization();
        findview();

    }

    private void findview() {
        mToolbar = (Toolbar) findViewById(R.id.maptoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("搜尋獸醫");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).
                addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        ib = findViewById(R.id.IB);
        ib.bringToFront();
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Address> addressLocation = null;
                try {
                    addressLocation = geocoder.getFromLocationName("花蓮縣壽豐鄉大學路二段1-24號", 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addressLocation.size() <= 0) {
                } else {
                    double latitude = addressLocation.get(0).getLatitude();
                    double longitude = addressLocation.get(0).getLongitude();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.8948419, 121.5338758), 14));
                }
            }
        });

        geocoder = new Geocoder(FindvetActivity3.this, Locale.getDefault());
    }

    private void getData(String location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ProgressDialog progDialog = ProgressDialog.show(FindvetActivity3.this,
                "Processing.....", "開啟googleMap", true, true);
        mMap = googleMap;

//        setupMyLocation();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.894300, 121.543400), 14));
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(FindvetActivity3.this);
        mMap.setInfoWindowAdapter(adapter);

//        AutocompleteSupportFragmentInit();
        progDialog.dismiss();
//        getData("");
    }


    private void setupMyLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                return;
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupMyLocation();
                } else {
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maptool2, menu);
//        MenuItem search = menu.findItem(R.id.research_clinic2);
//        searchView = (SearchView) MenuItemCompat.getActionView(search);
//        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.research_clinic2:
                onSearchCalled();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String location) { return true; }

    @Override
    public boolean onQueryTextChange(String location) { return true; }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location!=null){
                            Log.i("Location",location.getLatitude()+"/"+location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),14));
                        }
    }
    @Override
    public void onConnectionSuspended(int i) { }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }


    public void googleplacesinitialization() {
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
    }


    public void onSearchCalled() {
        SearchCalled t1=new SearchCalled();
        t1.execute();
    }


    class SearchCalled extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            return null;}
    }


}







