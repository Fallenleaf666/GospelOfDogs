package com.leaf.godproject.findvet;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.leaf.godproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class
FindvetActivity2 extends AppCompatActivity {
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 2;
    private Toolbar mToolbar;
    private TextView placeid;
    private TextView placename;
    private TextView placetime;
    private TextView placescore;
    private TextView placelat;
    private TextView placelng;
    private static String TAG = "MapsActivity";

    private String loc="";
    private String name="";
    private String doctor="";
    private String location="";
    private String phone1="";
    private String l1="";
    private String l2="";

    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);

        initview();
        client=new OkHttpClient();
        new JsonTask().execute();
    }

    private void initview() {
        placeid=findViewById(R.id.PLACEID);
        placename=findViewById(R.id.PLACENAME);
        placetime=findViewById(R.id.TIME);
        placescore=findViewById(R.id.SCORE);
        placelat=findViewById(R.id.LAT);
        placelng=findViewById(R.id.LNG);
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... url) {
//            String strName="普羅動物醫院";
//            double l1=24.991229999999998;
//            double l2=121.29893700000001;
//                String url = "https://maps.googleapis.com/maps/api/place/textsearch/json" +
//                        "?query=" + URLEncoder.encode(strName, "UTF-8")+ "&location=" +l1+","+l2 +
//                        "&radius=10000" +"&key=" + getString(R.string.google_maps_key);
//                Request request=new Request.Builder().url(urlurl).build();
//                https://maps.googleapis.com/maps/api/place/details/json?place_id=ChIJ6RHMhBQfaDQRDLeyR2RPGhk&fields=place_id,name,geometry,rating,opening_hours,formatted_phone_number&key=AIzaSyBz86HVhzBjq4KW2_d6Pp3z8Ivy3-sI2GI
            Request request;
            try {request=new Request.Builder().url(String.valueOf(url)).build();
                Call call=client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) { Log.d("OKhttp回傳","失敗"); }
                    @Override
                    public void onResponse(Call call, Response response)throws IOException{
                        String json=response.body().string();
                        Log.d("OKhttp回傳檔案",json);
                        parseJSON(json);
                    }
                });
            }
            catch (Exception e){
                Log.d("OKhttp回傳檔案","失敗");
            }
                return null;
        }
    }
    private class JsonTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... Void) {
            String str=getJson("allvet1.json",getBaseContext());
            String url;
            try {
                JSONArray jsonArray = new JSONArray(str);
                if(jsonArray!=null){
                    for (int i = 0; i < jsonArray.length()-1610; i++) {
                        JSONObject student = jsonArray.getJSONObject(i);
                        loc=student.getString("縣市");
                        name=student.getString("機構名稱");
                        doctor=student.getString("負責獸醫");
                        phone1=student.getString("機構電話");
                        location=student.getString("機構地址");
                        l1=student.getString("經度");
                        l2=student.getString("緯度");
                        try {
                            url = "https://maps.googleapis.com/maps/api/place/textsearch/json" +
                                    "?query=" + URLEncoder.encode(name, "UTF-8")+ "&location=" +l1+","+l2 +
                                    "&radius=10000" +"&key=" + getString(R.string.google_maps_key);
                            Log.d(TAG, url);

                            Request request;
                            try {request=new Request.Builder().url(url).build();
                                Call call=client.newCall(request);
                                call.enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) { Log.d("OKhttp回傳","失敗"); }
                                    @Override
                                    public void onResponse(Call call, Response response)throws IOException{
                                        String json=response.body().string();
                                        Log.d("OKhttp回傳檔案",json);
                                        parseJSON(json);
                                    }
                                });
                            }
                            catch (Exception e){
                                Log.d("OKhttp回傳檔案","失敗");
                            }

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                            new HttpAsyncTask().execute(url);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, i+1+"/"+jsonArray.length()+"醫院 =" + student.toString());
                    }
                }
            }catch (JSONException e) { e.printStackTrace(); }
            return null;
        }
    }
    private void parseJSON(String json) {
        try {
            JSONObject obj= new JSONObject(json);
            for(int i = 0;i<obj.getJSONArray("results").length();i++){
                Log.i("results name", obj.getJSONArray("results").getJSONObject(i).getString("name"));
            }
        }
            catch (JSONException e) {
            e.printStackTrace();
        }
    }

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


    //            Gson gson=new Gson();
//            ArrayList<Transaction>list=
//                    gson.fromJson(
//                            s,new TypeToken<Transaction>(){}.getType());
//            Log.d("JSON",list.size()+"/"+list.get(0).getAmount());
}