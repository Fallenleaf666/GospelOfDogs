package com.leaf.godproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.leaf.godproject.dictionary.*;
import com.leaf.godproject.dictionary.DictionaryDB;
import com.leaf.godproject.dictionary.SectionsPagerAdapter;


import java.util.ArrayList;

public class DictionaryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String TAG ="DictionaryActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private String EXTRA_REMINDER_ID="Member_ID";
    public ViewPager mViewPager;
    public RecyclerView sView;
    private Toolbar mToolbar;
    private int sition=0;
    private SectionsPagerAdapter adapter;
    private TabLayout tabLayout;

//資料庫
    public DictionaryDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictional);

//        初次導入資料庫之後考慮寫在welcome
        dbHelper = new DictionaryDB(this);
        dbHelper.openDatabase();
        dbHelper.closeDatabase();

//初始資料庫
//        mb = new DictionaryDB(getApplicationContext());

        mToolbar = (Toolbar) findViewById(R.id.dictoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("狗狗百科");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mSectionsPagerAdapter =new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager=(ViewPager)findViewById(R.id.container);
        sView=(RecyclerView)findViewById(R.id.searchrecycleView);
        setupViewPager(mViewPager);

        tabLayout=(TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                sition=tab.getPosition();
                if(sition==4){
                    try {
                        adapter.getItem(4).onCreate(null);
                    }catch (Exception e){}

                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
    public void onPause(){ super.onPause(); }
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }
//重新回到葉面時

//    @Override
//    protected void onResume() {
//        super.onResume();
//    }



    public void onRestart() {
//        Toast.makeText(this, ""+sition, Toast.LENGTH_SHORT).show();
        super.onRestart();
//        收藏刷新
        int x=sition;
        if(x==4||x==3){
            adapter.getItem(4).onCreate(null);
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(x);
        }



//        Toast.makeText(this, ""+sition, Toast.LENGTH_SHORT).show();
    }


    private void setupViewPager(ViewPager viewPager){
        adapter =new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DicallFragment(),"全部");
        adapter.addFragment(new DicadoptFragment(),"飼養");
//        訓練改生活
//        adapter.addFragment(new DictrainFragment(),"訓練");
        adapter.addFragment(new DichospitalFragment(),"健康");
        adapter.addFragment(new DicotherFragment(),"其他");
        adapter.addFragment(new DictypeFragment(),"收藏");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dictionarytool2, menu);
        MenuItem search=menu.findItem(R.id.research_dictionary);
//        androidx.appcompat.widget.SearchView searchView=(SearchView) MenuItemCompat.getActionView(search);
        androidx.appcompat.widget.SearchView searchView =
                (androidx.appcompat.widget.SearchView)search.getActionView();
        searchView.setOnQueryTextListener(this);

        SearchView.SearchAutoComplete msearchAutoComplete=
                search.getActionView().findViewById(R.id.search_src_text);
        msearchAutoComplete.setTextColor(ContextCompat.getColor(this,R.color.titlecolor));
        msearchAutoComplete.setHintTextColor(ContextCompat.getColor(this,R.color.hinttitlecolor));

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.research_dictionary:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onQueryTextSubmit(String query){
return true;
    }
    @Override
    public boolean onQueryTextChange(String newTest){
        if(newTest.isEmpty()!=true)
        {
            CollectRecycleAdapter mCollectRecyclerAdapter;
            ArrayList<Doginfo> newList ;
            dbHelper.openDatabase();
            newList=dbHelper.getsearchMember(newTest);
//            if(newList.isEmpty()!=true){Toast.makeText(this, "有資料", Toast.LENGTH_SHORT).show();}
//            沒檢查
            mCollectRecyclerAdapter=new CollectRecycleAdapter(this, newList);
            sView.setAdapter(mCollectRecyclerAdapter);
            sView.setLayoutManager(new LinearLayoutManager(this, sView.VERTICAL, false));
            sView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mCollectRecyclerAdapter.setOnItemClickListener(new CollectRecycleAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(View view, Doginfo data) {
                    Intent intent = new Intent(DictionaryActivity.this, DictionaryDetailPage.class);
                    Bundle bundle =new Bundle();
                    bundle.putString(EXTRA_REMINDER_ID,Integer.toString(data.getId()));
                    intent.putExtras(bundle);
                    startActivity(intent);}});
            sView.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.INVISIBLE);
            tabLayout.setVisibility(View.INVISIBLE);

        }
        else {
            mViewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            sView.setVisibility(View.INVISIBLE);
        }
        return true;
    }


}