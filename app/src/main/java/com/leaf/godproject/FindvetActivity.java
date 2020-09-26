package com.leaf.godproject;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leaf.godproject.findvet.CustomInfoWindowAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FindvetActivity extends AppCompatActivity implements OnMapReadyCallback, SearchView.OnQueryTextListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 2;
    private Toolbar mToolbar;
    private static String TAG = "MapsActivity";
    Geocoder geocoder = null;
    private FloatingActionButton ib;
    SearchView searchView;

    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findvet);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
//                googleAPI的定位方式
//                mGoogleApiClient.connect();
//                mGoogleApiClient.disconnect();
//                gps的定位方式
//                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                String provider = "gps";
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//                        Toast.makeText(getBaseContext(),"GPS權限未開",Toast.LENGTH_SHORT).show();}
//                    else{
//                        Toast.makeText(getBaseContext(), "定位中", Toast.LENGTH_SHORT).show();
//                        Location location = lm.getLastKnownLocation(provider);
//                        if(location!=null){
//                            Log.i("Location",location.getLatitude()+"/"+location.getLongitude());
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),14));
//                        }else{
//                            Toast.makeText(getBaseContext(),"GPS定位失敗請確定在室外",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }

//                Toast.makeText(getBaseContext(),"定位中",Toast.LENGTH_SHORT).show();
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.894300,  121.543400),14));
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
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),14));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.8948419, 121.5338758), 14));
                }
            }
        });

        geocoder = new Geocoder(FindvetActivity.this, Locale.getDefault());
    }

    private void getData(String location) {
//        String urlVet = "http://data.coa.gov.tw/Service/OpenData/DataFileService.aspx?UnitId=078&$top=1000&$skip=0";
//        String urlVet = "http://data.coa.gov.tw/Service/OpenData/DataFileService.aspx?UnitId=078&$top=1000&$skip=0&$filter=%e6%a9%9f%e6%a7%8b%e5%9c%b0%e5%9d%80+like+%e8%8a%b1%e8%93%ae%e7%b8%a3";
        String urlVet = "http://data.coa.gov.tw/Service/OpenData/DataFileService.aspx?UnitId=078&$top=1000&$skip=0&$filter=%e6%a9%9f%e6%a7%8b%e5%9c%b0%e5%9d%80+like+%e8%8a%b1%e8%93%ae%e7%b8%a3%e8%8a%b1%e8%93%ae";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlVet,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject student = response.getJSONObject(i);
                                String Name = student.getString("機構名稱");
                                String time = "";
                                double score = 0;
                                switch (Name) {
                                    case " 高橋動物醫院":
                                        time = "09:00–16:30";
                                        score = 4.3;
                                        break;
                                    case " 綠樁動物醫院":
                                        time = "09:00–21:00";
                                        score = 4.6;
                                        break;
                                    case " 高橋貓友善動物醫院":
                                        time = "09:00–16:30";
                                        score = 4.3;
                                        break;
                                    case " 中華動物醫院":
                                        time = "10:00–21:00";
                                        score = 4.1;
                                        break;
                                    case " 楊動物醫院":
                                        time = "09:00~16:30";
                                        score = 4.3;
                                        break;
                                    case " 蕙康動物醫院":
                                        time = "09:30–11:30";
                                        score = 4.6;
                                        break;
                                    case " 人人動物醫院":
                                        time = "09:00–21:00";
                                        score = 4.3;
                                        break;
                                }
                                String address = student.getString("機構地址");
                                String phone = student.getString("機構電話");
                                String subTitle = "評分 : " + score + "/5分" + "\n" + "營業狀態 :" + " 營業中" + "\n" + "時間 : " + time + "\n" + "地址 :" + address + "\n" + "電話 :" + phone;
                                Log.d(TAG, "response = " + Name + address + phone + response.toString());
                                List<Address> addressLocation = null;
                                try {
                                    addressLocation = geocoder.getFromLocationName(address, 1);
                                    Log.d(TAG, "response666 =" + addressLocation);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                double latitude = addressLocation.get(0).getLatitude();
                                double longitude = addressLocation.get(0).getLongitude();
                                Log.d("Vet", latitude + "," + longitude);
                                MarkerOptions mo = new MarkerOptions();
                                mo.position(new LatLng(latitude, longitude))
                                        .title(Name)
                                        .snippet(subTitle)
                                        .icon(bitmapDescriptorFromVector(getBaseContext(), R.drawable.vet));
//                                mMap.addMarker(mo).showInfoWindow();

                                mMap.addMarker(mo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "response = " + response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "error = ");
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ProgressDialog progDialog = ProgressDialog.show(FindvetActivity.this,
                "Processing.....", "開啟googleMap", true, true);
        mMap = googleMap;

//        setupMyLocation();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.894300, 121.543400), 14));
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(FindvetActivity.this);
        mMap.setInfoWindowAdapter(adapter);
        getData("");
        progDialog.dismiss();
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
        getMenuInflater().inflate(R.menu.maptool, menu);
        MenuItem search = menu.findItem(R.id.research_clinic);
        searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.research_clinic:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String location) {
//        ProgressDialog progDialog = ProgressDialog.show(FindvetActivity.this,
//                "Processing.....", "Finding Location", true, true);
//        getData(location);
        try {
            List<Address> addressLocation = null;
//        try { addressLocation = geocoder.getFromLocationName(location, 1);
//        } catch (IOException e) { e.printStackTrace(); }
            addressLocation = geocoder.getFromLocationName(location, 1);
            if (addressLocation.size() <= 0) {
                Toast.makeText(this, "請輸入正確地址", Toast.LENGTH_SHORT).show();
            } else {
                double latitude = addressLocation.get(0).getLatitude();
                double longitude = addressLocation.get(0).getLongitude();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "請檢查連線是否正常", Toast.LENGTH_SHORT).show();
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String location) {
        return true;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


//    @Override
//    protected void onStart() {
//        mGoogleApiClient.connect();
//        super.onStart();
//    }
//
//    @Override
//    protected void onStop() {
//        mGoogleApiClient.disconnect();
//        super.onStop();
//    }

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


}