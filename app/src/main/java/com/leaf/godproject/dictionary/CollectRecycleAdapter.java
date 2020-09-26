package com.leaf.godproject.dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.leaf.godproject.R;

import java.util.ArrayList;

public class CollectRecycleAdapter extends RecyclerView.Adapter<CollectRecycleAdapter.myViewHodler> {
    private Context context;
    private ArrayList<Doginfo> doginfoList;

    //创建构造函数
    public CollectRecycleAdapter(Context context, ArrayList<Doginfo> doginfoList) {
        //将传递过来的数据，赋值给本地变量
        this.context = context;//上下文
        this.doginfoList = doginfoList;//实体类数据ArrayList
    }
//创建viewhodler，相当于listview中getview中的创建view和viewhodler
    @Override
    public myViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建自定义布局
//        View itemView = View.inflate(context, R.layout.diction_item, null);
//        很重要的寫法，可以保持item格式不變化
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diction_item,parent,false);
        return new myViewHodler(itemView);
    }

     //綁定數據和view
    @Override
    public void onBindViewHolder(myViewHodler holder, int position) {
        //根據位置找
        Doginfo data = doginfoList.get(position);
        holder.mItemMemberImg.setImageResource(R.drawable.ddog);
        holder.mItemMemberType.setText(data.getType());
        holder.mItemMemberTitle.setText(data.getTitle());
    }

    @Override
    public int getItemCount() {
        return doginfoList.size();
    }

    //自定义viewhodler
    class myViewHodler extends RecyclerView.ViewHolder {
        private ImageView mItemMemberImg;
        private TextView mItemMemberType;
        private TextView mItemMemberTitle;

        public myViewHodler(View itemView) {
            super(itemView);
            mItemMemberImg = (ImageView) itemView.findViewById(R.id.imageId);
            mItemMemberType = (TextView) itemView.findViewById(R.id.dictypetext);
            mItemMemberTitle = (TextView) itemView.findViewById(R.id.dtitletext);
            //点击事件放在adapter中使用，也可以写个接口在activity中调用
            //方法一：在adapter中设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, doginfoList.get(getLayoutPosition()));
                    }
                }
            });

        }
    }

    /**
     * 设置item的监听事件的接口
     */
    public interface OnItemClickListener {
        /**
         * 接口中的点击每一项的实现方法，参数自己定义
         *
         * @param view 点击的item的视图
         * @param data 点击的item的数据
         */
        public void OnItemClick(View view, Doginfo data);
    }

    //需要外部访问，所以需要设置set方法，方便调用
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

