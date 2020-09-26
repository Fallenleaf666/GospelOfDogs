package com.leaf.godproject.findvet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leaf.godproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FindvetActivity4 extends AppCompatActivity implements OnMapReadyCallback, SearchView.OnQueryTextListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 22;
    private static final int REQUEST_LOCATION = 2;
    private static final String TAG = FindvetActivity4.class.getSimpleName();

    private Toolbar mToolbar;
    Geocoder geocoder = null;
    private FloatingActionButton ib;
    SearchView searchView;

    private String loc="";
    private String name="";
    private String doctor="";
    private String location="";
    private String phone1="";
    private double l1;
    private double l2;
    private String subTitle="";
    private MarkerOptions mo;

    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findvet3);

        SetSupportMap();
        SetActbar();

        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) { Places.initialize(getApplicationContext(), apiKey); }

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).
                addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        SetMysiteFab();

//        啟用地理編碼
        geocoder = new Geocoder(FindvetActivity4.this, Locale.getDefault());
    }

//    定位
    private void SetMysiteFab() {
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
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),14));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.8948419, 121.5338758), 14));
                }
            }
        });
    }

//   初始Fragment
    private void SetSupportMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
//設置功能欄
    private void SetActbar() {
        mToolbar = (Toolbar) findViewById(R.id.maptoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("搜尋獸醫");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void getData(String location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ProgressDialog progDialog = ProgressDialog.show(FindvetActivity4.this,
                "Processing.....", "開啟googleMap", true, true);
        mMap = googleMap;

//        setupMyLocation();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.894300, 121.543400), 14));
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(FindvetActivity4.this);
        mMap.setInfoWindowAdapter(adapter);

        Handler mainHandler =new Handler((getApplicationContext().getMainLooper()));
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    String str=getJson("allvet1.json",getBaseContext());
                    JSONArray jsonArray = new JSONArray(str);
                    if(jsonArray!=null){
                    for (int i = 0; i < jsonArray.length(); i++) {
//                        for (int i = 0; i < jsonArray.length()-1610; i++) {
                            JSONObject student = jsonArray.getJSONObject(i);
                            loc=student.getString("縣市");
                            name=student.getString("機構名稱");
                            doctor=student.getString("負責獸醫");
                            phone1=student.getString("機構電話");
                            location=student.getString("機構地址");
                            l1=Double.parseDouble(student.getString("經度"));
                            l2=Double.parseDouble(student.getString("緯度"));
                            Log.d(TAG, i+1+"/"+jsonArray.length()+"醫院 =" + student.toString());

                            subTitle = "地址 :" + location + "\n" + "電話 :" + phone1;
                            mo=null;
                            mo = new MarkerOptions();
                            mo.position(new LatLng(l1, l2))
                                    .title(name)
                                    .snippet(subTitle)
                                    .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.vet));
//                                mMap.addMarker(mo).showInfoWindow();
                            mMap.addMarker(mo);
                        }
                    }
                }catch (JSONException e) { e.printStackTrace(); }
            }
        });
//        getData("");
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
        getMenuInflater().inflate(R.menu.maptool2, menu);
//        MenuItem search = menu.findItem(R.id.research_clinic);
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


    public void onSearchCalled() {
                 // Set the fields to specify which types of place data to return.
                 List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                 // Start the autocomplete intent.
                 Intent intent = new Autocomplete.IntentBuilder(
                                 AutocompleteActivityMode.FULLSCREEN, fields).setCountry("NG") //NIGERIA
                         .build(this);
                 startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
             }
    @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                String address = place.getAddress();
                // do query with address
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


//    取得json檔案
    public static String getJson(String fileName, Context context) {
        //將json資料變成字串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //獲取assets資源管理器
            AssetManager assetManager = context.getAssets();
            //通過管理器開啟檔案並讀取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
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
}