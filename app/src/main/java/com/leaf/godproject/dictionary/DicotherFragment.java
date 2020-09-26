package com.leaf.godproject.dictionary;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leaf.godproject.R;

import java.util.ArrayList;

public class DicotherFragment extends Fragment {
    private View view;//定义view用来设置fragment的layout
    public RecyclerView mCollectRecyclerView;//定义RecyclerView
    //定义以goodsentity实体类为对象的数据集合
    private ArrayList<Doginfo> doginfoList = new ArrayList<Doginfo>();
    //自定义recyclerveiw的适配器
    private CollectRecycleAdapter mCollectRecyclerAdapter;
    private String EXTRA_REMINDER_ID="Member_ID";
    ArrayList<Doginfo> mTest;
    private DictionaryDB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dictional,container, false);
        initData();
        try {
            initRecyclerView();
        }catch (Exception e){}
        return view;
    }

    private void initData() {
        db = new DictionaryDB(getContext());
        db.openDatabase();
        mTest = db.getTypeMember("其他");
        if (mTest.isEmpty()) {
            view.findViewById(R.id.nodatatext).setVisibility(view.VISIBLE);
//            Toast.makeText(getContext(), "沒有資料",
//                    Toast.LENGTH_SHORT).show();
        }
    }
    private void initRecyclerView() {
        mCollectRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        mCollectRecyclerAdapter = new CollectRecycleAdapter(getActivity(), mTest);
        mCollectRecyclerView.setAdapter(mCollectRecyclerAdapter);
//        mCollectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mCollectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), mCollectRecyclerView.VERTICAL, false));
        mCollectRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mCollectRecyclerAdapter.setOnItemClickListener(new CollectRecycleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Doginfo data) {
                Intent intent = new Intent(getActivity(), DictionaryDetailPage.class);
                Bundle bundle =new Bundle();
                bundle.putString(EXTRA_REMINDER_ID,Integer.toString(data.getId()));
                intent.putExtras(bundle);
                startActivity(intent);
//                Toast.makeText(getActivity(),"即將進入標題"+data.getTitle(),Toast.LENGTH_SHORT).show();
            }
        });
    }



}
