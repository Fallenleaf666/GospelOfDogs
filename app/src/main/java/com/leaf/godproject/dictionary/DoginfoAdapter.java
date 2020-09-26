package com.leaf.godproject.dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.leaf.godproject.R;

import java.util.List;

public class DoginfoAdapter extends RecyclerView.Adapter<DoginfoAdapter.ViewHolder> {
    private Context context;
    private List<Doginfo> doginfoList;

    public DoginfoAdapter(Context context, List<Doginfo> doginfoList) {
        this.context = context;
        this.doginfoList = doginfoList;
    }

    @Override
    public DoginfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.diction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DoginfoAdapter.ViewHolder holder, int position) {
        final Doginfo doginfo = doginfoList.get(position);
//        holder.imageId.setImageResource(doginfo.getImage());
        holder.imageId.setImageResource(R.drawable.ddog);
//        holder.textId.setText(String.valueOf(doginfo.getId()));
        holder.mtypetext.setText(String.valueOf(doginfo.getType()));
        holder.mtitletext.setText(String.valueOf(doginfo.getTitle()));
//        holder.textName.setText(doginfo.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ImageView imageView = new ImageView(context);
//                imageView.setImageResource(doginfo.getImage());
//                Toast toast = new Toast(context);
//                toast.setView(imageView);
//                Toast.makeText(context, "進入id"+holder.getItemId()+"頁面",
//                        Toast.LENGTH_SHORT).show();
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return doginfoList.size();
    }

    //Adapter 需要一個 ViewHolder，只要實作它的 constructor 就好，保存起來的view會放在itemView裡面
    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageId;
        TextView mtypetext, mtitletext;
        ViewHolder(View itemView) {
            super(itemView);
            imageId = (ImageView) itemView.findViewById(R.id.imageId);
            mtypetext = (TextView) itemView.findViewById(R.id.dictypetext);
            mtitletext = (TextView) itemView.findViewById(R.id.dtitletext);
        }
    }

}