package com.leaf.godproject.nouse;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.leaf.godproject.PetdiaryEditMypet;
import com.leaf.godproject.R;

import java.util.List;

public class DogAdapter extends RecyclerView.Adapter<DogAdapter.ViewHolder> {
    private Context context;
    private List<Dog> dogList;
    FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    int p=0;
//    private OnItemClickHandler mClickHandler;

    public DogAdapter(Context context, List<Dog> dogList) {
        this.context = context;
        this.dogList = dogList;
    }

    @Override
    public DogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mydog_recycle_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final Dog dog = dogList.get(position);
//        holder.imageId.setImageResource(doginfo.getImage());
//            holder.dIMG.setImageResource(R.drawable.ddog);
        if(dog.getName()!=null){
            holder.dNAME.setText(dog.getName());
        }else{holder.dNAME.setText("無名");}

        if(!dog.getMixType().equals("無")){
            String type=dog.getType()+" 混 "+dog.getMixType();
            holder.dTYPE.setText(type);
        }else{
            String type=!dog.getType().equals("") ?dog.getType():"未知品種";
            holder.dTYPE.setText(type);}

//        holder.dDOGINFO.setText(dog.getGender()+" "+dog.getBirth());
        String info=(!dog.getGender().equals("") ?dog.getGender():"未知性別")+ " "+
                (!dog.getBirth().equals("") ?dog.getBirth():"未知生日");
        holder.dDOGINFO.setText(info);


        mFirebaseAuth= FirebaseAuth.getInstance();
        user=mFirebaseAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://key-utility-254506.appspot.com");
        StorageReference mstorageRef = storage.getReference();
        final StorageReference dogheadRef = mstorageRef.child("Users/"+user.getUid()+"/"+dog.getID()+"/doghead.jpg");
        if(dogheadRef == null){ return;
        } else{
            final long ONE_MEGABYTE = 1024 * 1024*5;
            dogheadRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Glide.with(context)
                            .load(bytes)
                            .into(holder.dIMG);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        }

    @Override
    public int getItemCount() { return dogList.size(); }

    //Adapter 需要一個 ViewHolder，只要實作它的 constructor 就好，保存起來的view會放在itemView裡面
    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView dIMG;
        ImageButton imgbt;
        TextView dNAME,dTYPE,dDOGINFO;
        ViewHolder(View itemView) {
            super(itemView);
//            dIMG= (ImageView) itemView.findViewById(R.id.imageId);
            dNAME = (TextView) itemView.findViewById(R.id.recycle_dogname);
            dTYPE = (TextView) itemView.findViewById(R.id.recycle_dogtype);
            dDOGINFO = (TextView) itemView.findViewById(R.id.recycle_doginfo);
            dIMG=(ImageView)itemView.findViewById(R.id.dog_image);
            imgbt=(ImageButton)itemView.findViewById(R.id.editbt);
            imgbt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    p = getAdapterPosition();
                    showPopup(v);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, dogList.get(getLayoutPosition()));
                    }
                }
            });

        }
    }


//    public void setItemCount(int count) {
//        dogList.clear();
//        dogList.addAll(generateData(count));
//        notifyDataSetChanged();
//    }

//    public void onDeleteItem(int count) {
//        dogList.clear();
//        dogList.addAll(generateData(count));
//    }

    public void removeItemSelected(final int selected) {
        if(Checkloginuser()){
            if (dogList.isEmpty()) return;
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            Toast.makeText(context, "即將刪除"+dogList.get(p).getName(), Toast.LENGTH_SHORT).show();
            db.collection("Users").document(user.getUid()).collection("MYDOGS").document(dogList.get(p).getID())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            //           TODO 刪除資料庫資料成功執行以下
                            dogList.remove(selected);
                            notifyItemRemoved(selected);
                            Toast.makeText(context, "刪除成功", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "刪除成功", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(context, "請確定連線及登入狀態"+p, Toast.LENGTH_SHORT).show();}
    }


    public boolean Checkloginuser() {
        mFirebaseAuth= FirebaseAuth.getInstance();
        user=mFirebaseAuth.getCurrentUser();
        if(user!=null){return true;}
        else{return false;}
    }


    public interface OnItemClickListener {
        public void OnItemClick(View view, Dog data);
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.mydogeditmanu, popup.getMenu());
        popup.setOnMenuItemClickListener(new onMenuItemClick());
        popup.show();
    }
    private class onMenuItemClick implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.editdog:
                    editItemSelected(dogList.get(p).getID());
                    Toast.makeText(context, "編輯"+dogList.get(p).getName(), Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.deletedog:
                    removeItemSelected(p);
                    Toast.makeText(context, "刪除"+p, Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }
        }
    }

    private void editItemSelected(String id) {
        Intent intent = new Intent();
        intent.putExtra("DOG_ID",id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context.getApplicationContext(), PetdiaryEditMypet.class);
        context.getApplicationContext().startActivity(intent);
    }

    public interface OnItemClickHandler {
        // 提供onItemClick方法作為點擊事件，括號內為接受的參數
        void onItemClick(String text);
        // 提供onItemRemove做為移除項目的事件
        void onItemRemove(int position, String text);
    }
}