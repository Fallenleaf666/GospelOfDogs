package com.leaf.godproject.dictionary;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.leaf.godproject.R;

public class DictionaryDetailPage extends AppCompatActivity {
    TextView dtypetext,dtitletext,dcontenttext;
    private String dTitle;
    private String dType;
    private String dIslike;
    private String dContent;
    private DictionaryDB db;
    private int dReceivedID;
    public static final String EXTRA_REMINDER_ID = "Member_ID";
    private Doginfo dReceivedDoginfo;
    Toolbar toolbar;
    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TYPE = "type_key";
    private static final String KEY_CONTENT= "content_key";
    private static final String KEY_ISLIKE = "islike_key";

    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary_detal_page2);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        CollapsingToolbarLayout ctb=findViewById(R.id.ctb) ;

        dtypetext=(TextView)findViewById(R.id.typetext);
        dtitletext=(TextView)findViewById(R.id.titletext);
        dcontenttext=(TextView)findViewById(R.id.contenttext);

        // Get reminder id from intent
        dReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_REMINDER_ID));

        // Get reminder using reminder id
        db = new DictionaryDB(this);
        db.openDatabase();
        dReceivedDoginfo = db.getMember(dReceivedID);

        // Get values from reminder
        dTitle = dReceivedDoginfo.getTitle();
        dType = dReceivedDoginfo.getType();
        dContent = dReceivedDoginfo.getContent();
        dIslike = dReceivedDoginfo.getLike();

        // Setup TextViews using reminder values
        ctb.setTitle(dTitle);
        dtitletext.setText(dTitle);
        dtypetext.setText(dType);
        dcontenttext.setText(dContent);


//         To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            dtitletext.setText(savedTitle);
            dTitle = savedTitle;

            String savedType = savedInstanceState.getString(KEY_TYPE);
            dtypetext.setText(savedType);
            dType = savedType;

            String savedContent = savedInstanceState.getString(KEY_CONTENT);
            dcontenttext.setText(savedContent);
            dContent = savedContent;

            String savedIslike = savedInstanceState.getString(KEY_ISLIKE);
//            設置icon
            dIslike = savedIslike;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dictionarydetailtool, menu);
        //        icon設置  測試玩再弄
        if(dReceivedDoginfo.getLike().equals("ture")){
            menu.findItem(R.id.save_dictionary).setIcon(R.drawable.ic_favorite_black_24dp);
            menu.findItem(R.id.save_dictionary).setTitle("取消收藏");
        }
        else{menu.findItem(R.id.save_dictionary).setIcon(R.drawable.ic_favorite_border_black_24dp);
            menu.findItem(R.id.save_dictionary).setTitle("收藏");
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.save_dictionary:
                if(item.getTitle().equals("收藏")){
                    Toast.makeText(getApplicationContext(), "已收藏",
                        Toast.LENGTH_SHORT).show();
                    item.setTitle("取消收藏");
                    dIslike="ture";
                    updateMember();
                    item.setIcon(R.drawable.ic_favorite_black_24dp);}
                else if(item.getTitle().equals("取消收藏")){
                    Toast.makeText(getApplicationContext(), "取消收藏",
                            Toast.LENGTH_SHORT).show();
                    item.setTitle("收藏");
                    dIslike="false";
                    updateMember();
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TITLE, dtitletext.getText());
        outState.putCharSequence(KEY_TYPE, dtypetext.getText());
        outState.putCharSequence(KEY_CONTENT, dcontenttext.getText());
        outState.putCharSequence(KEY_ISLIKE, dIslike);
    }
    public void updateMember(){
        dReceivedDoginfo.setLike(dIslike);
        db.updateMember(dReceivedDoginfo);
    }
}
