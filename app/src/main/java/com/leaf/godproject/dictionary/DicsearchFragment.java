package com.leaf.godproject.dictionary;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class DicsearchFragment extends Fragment {
    private static  final String TAG = "全部";
    private View view;//定义view用来设置fragment的layout
    public RecyclerView mCollectRecyclerView;//定义RecyclerView
    private ArrayList<Doginfo> doginfoList = new ArrayList<Doginfo>();
    private CollectRecycleAdapter mCollectRecyclerAdapter;
    private String EXTRA_REMINDER_ID="Member_ID";
    ArrayList<Doginfo> mTest;
    private DictionaryDB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dictional, container, false);
        initData();
        initRecyclerView();
        return view;
    }

    private void initData() {
//        context不確定
        db = new DictionaryDB(getContext());
        db.openDatabase();
        mTest = db.getAllMember();
        if (mTest.isEmpty()) {
//            mNoReminderView.setVisibility(View.VISIBLE);
            Log.d(TAG,"findviews");
//            Toast.makeText(getContext(), "沒有資料",
//                    Toast.LENGTH_SHORT).show();
        }
//        doginfoList.add(new Doginfo(0,"教育","怎麼養狗","用食物","false"));
    }
    private void initRecyclerView() {
        //获取RecyclerView
        mCollectRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        //创建adapter
//        mCollectRecyclerAdapter = new CollectRecycleAdapter(getActivity(), doginfoList);
        mCollectRecyclerAdapter = new CollectRecycleAdapter(getActivity(), mTest);

        //给RecyclerView设置adaptergetMemberCount
        mCollectRecyclerView.setAdapter(mCollectRecyclerAdapter);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        mCollectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), mCollectRecyclerView.VERTICAL, false));
//        mCollectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), StaggeredGridLayoutManager.VERTICAL, false));
//        mCollectRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        //设置item的分割线
        mCollectRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        mCollectRecyclerAdapter.setOnItemClickListener(new CollectRecycleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Doginfo data) {
                //此处进行监听事件的业务处理
                Intent intent = new Intent(getActivity(), DictionaryDetailPage.class);
                Bundle bundle =new Bundle();
                bundle.putString(EXTRA_REMINDER_ID,Integer.toString(data.getId()));
//                bundle.putString("KEY_TYPE",data.getType());
//                bundle.putString("KEY_TITLE",data.getTitle());
//                bundle.putString("KEY_CONTENT",data.getContent());
                intent.putExtras(bundle);
                startActivity(intent);
//                Toast.makeText(getActivity(),"即將進入標題"+data.getTitle(),Toast.LENGTH_SHORT).show();
            }
        });
    }



}
