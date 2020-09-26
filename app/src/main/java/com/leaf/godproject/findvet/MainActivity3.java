package com.leaf.godproject.findvet;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.leaf.godproject.R;
import com.leaf.godproject.findvet.clusterRenderer.MarkerClusterRenderer;
import com.leaf.godproject.findvet.data.User;
import com.leaf.godproject.findvet.util.GoogleMapHelper;

import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.location.Location;


public class MainActivity3 extends AppCompatActivity implements SearchView.OnQueryTextListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    Geocoder geocoder = null;
    GoogleApiClient mGoogleApiClient;

    private String loc="";
    private String name="";
    private String doctor="";
    private String location="";
    private String phone1="";
    private double l1;
    private double l2;
    private String subTitle="";
    private MarkerOptions mo;
    private List<User> userList;

    private boolean mLocationPermissionGranted;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

//    SearchView searchView;
    androidx.appcompat.widget.SearchView searchView;

    private Toolbar mToolbar;

    ClusterManager<User> clusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_findvet4);
        SetActbar();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                GoogleMapHelper.defaultMapSettings(googleMap);
                MainActivity3.this.setUpClusterManager(googleMap);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.894300, 121.543400), 14));
            }
        });
//        supportMapFragment.getMapAsync(googleMap -> {
//            GoogleMapHelper.defaultMapSettings(googleMap);
//            setUpClusterManager(googleMap);
//        });
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).
                addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        geocoder = new Geocoder(MainActivity3.this, Locale.getDefault());
    }

    private void setUpClusterManager(GoogleMap googleMap) {

        mMap=googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.8948419, 121.5338758), 14));

        clusterManager = new ClusterManager<>(this, googleMap);

        MarkerClusterRenderer markerClusterRenderer = new MarkerClusterRenderer(this, googleMap, clusterManager);

        clusterManager.setRenderer(markerClusterRenderer);

        List<User> items = getItems();

        getLocationPermission();

        updateLocationUI();

//        clusterManager.addItems(items);
//
//        clusterManager.cluster();
    }

    private List<User> getItems() {
        Handler mainHandler =new Handler((getApplicationContext().getMainLooper()));
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    userList=new ArrayList<>();
                    String str=getJson("allvet1.json",getBaseContext());
                    JSONArray jsonArray = new JSONArray(str);
                    if(jsonArray!=null){
                        User tempuser;
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
//                            Log.d(TAG, i+1+"/"+jsonArray.length()+"醫院 =" + student.toString());
//                            subTitle = "地址 :" + location + "\n" + "電話 :" + phone1;
                            tempuser=new User(name,location,phone1,new LatLng(l1, l2));
                            userList.add(tempuser);
                        }
                        clusterManager.addItems(userList);
                        clusterManager.cluster();
                    }
                }catch (JSONException e) { e.printStackTrace(); }

            }

        });
        return userList;
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

    private void SetActbar() {
        mToolbar = (Toolbar) findViewById(R.id.maptoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("搜尋獸醫");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        填充Menu
        getMenuInflater().inflate(R.menu.maptool, menu);
//        找Menu中的item
        MenuItem search = menu.findItem(R.id.research_clinic);
//        舊版找SearchView方法
//        searchView = (SearchView) MenuItemCompat.getActionView(search);
//        找Search符號的View
        searchView = (androidx.appcompat.widget.SearchView)search.getActionView();
//        設置Search監聽
        searchView.setOnQueryTextListener(this);
//        取得Search中的TextView
        SearchView.SearchAutoComplete msearchAutoComplete=
                search.getActionView().findViewById(R.id.search_src_text);
//        設定輸入字體顏色及提示字體顏色
        msearchAutoComplete.setTextColor(ContextCompat.getColor(this,R.color.titlecolor));
        msearchAutoComplete.setHintTextColor(ContextCompat.getColor(this,R.color.hinttitlecolor));

//        把搜尋時中間的icon去掉
//        ImageView magImage=searchView.findViewById(R.id.search_mag_icon);
//        magImage.setLayoutParams(new LinearLayout.LayoutParams(0,0));
//        final ImageView searchicon=(ImageView)searchView.findViewById(R.id.search_mag_icon);
//        final ImageView searchicon=(ImageView)searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
//        searchicon.post(new Runnable() {
//            @Override
//            public void run() {
//        searchicon.setImageDrawable(null);
//        searchicon.setVisibility(View.GONE);
//            }
//        });
//        設定提示字元
        searchView.setQueryHint("請輸入地址或地名");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.research_clinic2:
//                onSearchCalled();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //        ProgressDialog progDialog = ProgressDialog.show(FindvetActivity.this,
//                "Processing.....", "Finding Location", true, true);
//        getData(location);
        try {
            List<Address> addressLocation = null;
            addressLocation = geocoder.getFromLocationName(query, 1);
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
//        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) { return false; }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
//        成功連結後google回傳地址
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






    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}


