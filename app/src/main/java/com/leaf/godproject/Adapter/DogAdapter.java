package com.leaf.godproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.leaf.godproject.DogProfile;
import com.leaf.godproject.EditMypet;
import com.leaf.godproject.Model.Dog;
import com.leaf.godproject.Model.Notification;
import com.leaf.godproject.R;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class DogAdapter  extends RecyclerView.Adapter<DogAdapter.ViewHolder>{
    private Context mContext;
    private List<Dog> mDog;
    private FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private String dogid;

    public DogAdapter(Context context, List<Dog> dogs){
        mContext = context;
        mDog = dogs;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mydog_recycle_items, parent, false);
        return new DogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogAdapter.ViewHolder holder, int position) {
        final Dog dog = mDog.get(position);


        if(!dog.getMaster().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.imgbt.setVisibility(View.GONE);
        }

        Glide.with(mContext).load(dog.getImgurl())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(holder.dIMG);

        String infoname=(!dog.getName().equals("") ?dog.getName():"未命名");
        holder.dNAME.setText(infoname);

        if(!dog.getMixtype().equals("無")){
            String type=dog.getType()+" 混 "+dog.getMixtype();
            holder.dTYPE.setText(type);
        }else{
            String type=!dog.getType().equals("") ?dog.getType():"未知品種";
            holder.dTYPE.setText(type);}

        String info=(!dog.getGender().equals("") ?dog.getGender():"未知性別")+ " "+
                (!dog.getBirth().equals("") ?dog.getBirth():"未知生日");
        holder.dDOGINFO.setText(info);

        if(dog.getGender().equals("公")){
            holder.dog_gender.setImageResource(R.drawable.female);
        }
        else if(dog.getGender().equals("母")){
            holder.dog_gender.setImageResource(R.drawable.male);
        }

holder.imgbt.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        dogid=dog.getId();
        showPopup(view);
    }
});
holder.cardView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
//        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//        editor.putString("DOG_ID", dog.getId());
//        editor.apply();
//        Intent intent = new Intent(mContext, DogProfile.class);
//        intent.putExtra("DOG_ID",dog.getId());
//        Toast.makeText(mContext, "即將進入"+dog.getId(), Toast.LENGTH_SHORT).show();
//        mContext.startActivity(intent);

        //        換fragment
        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("DOG_ID", dog.getId());
        editor.apply();
//        FragmentManager fragmentmanager=((FragmentActivity)mContext).getSupportFragmentManager();
//        fragmentmanager.beginTransaction()
//                .replace(R.id.fragment_container, new DogProfileFragment())
//                .addToBackStack(fragmentmanager.getClass().getName())
//                .commit();

        Intent intent = new Intent(mContext, DogProfile.class);
        mContext.startActivity(intent);
    }
});


        mFirebaseAuth= FirebaseAuth.getInstance();
        user=mFirebaseAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();

    }
    @Override
    public int getItemCount() { return mDog.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView dIMG;
        ImageButton imgbt;
        ImageView dog_gender;
        TextView dNAME,dTYPE,dDOGINFO;
        CardView cardView ;

        ViewHolder(View itemView) {
            super(itemView);
            cardView=(CardView)itemView.findViewById(R.id.cardview);
            dNAME = (TextView) itemView.findViewById(R.id.recycle_dogname);
            dTYPE = (TextView) itemView.findViewById(R.id.recycle_dogtype);
            dDOGINFO = (TextView) itemView.findViewById(R.id.recycle_doginfo);
            dIMG=(CircleImageView)itemView.findViewById(R.id.dog_image);
            imgbt=(ImageButton)itemView.findViewById(R.id.editbt);
            dog_gender=(ImageView) itemView.findViewById(R.id.dogsex);
        }
    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.mydogeditmanu, popup.getMenu());
        popup.setOnMenuItemClickListener(new DogAdapter.onMenuItemClick());
        popup.show();
    }

    private class onMenuItemClick implements PopupMenu.OnMenuItemClickListener{
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.editdog:
                    editItemSelected(dogid);
                    return true;
                case R.id.deletedog:
                    removeItemSelected(dogid);
                    return true;
                default:
                    return false;
            }
        }
    }

    private void editItemSelected(String dogid) {
//        換activity
        Intent intent = new Intent(mContext, EditMypet.class);
        intent.putExtra("DOG_ID",dogid);
//        Toast.makeText(mContext, "即將編輯", Toast.LENGTH_SHORT).show();
        mContext.startActivity(intent);

    }

    private void removeItemSelected(final String dogid) {
        FirebaseFirestore.getInstance().collection("Dogs").
                document(user.getUid()).collection("AllDogs")
                .document(dogid).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(mContext, "已刪除!", Toast.LENGTH_SHORT).show();
                            deleteRelativiyfoto(dogid);
                        }
                    }
                });
    }

    void deleteRelativiyfoto(String dogid){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Posts");
        reference.whereEqualTo("petid",dogid).addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    final String pid = doc.getString("postid");
                    FirebaseFirestore.getInstance().collection("Posts").
                            document(pid).delete().addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                        deleteNotifications(pid, firebaseUser.getUid());
                                    }
                                }
                            });
                }
            }
        });
    }

    private void deleteNotifications(final String postid, String userid){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Notifications").
                document(userid).collection("notifications");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                for (QueryDocumentSnapshot docsnap : queryDocumentSnapshots) {
                    Notification doc=docsnap.toObject(Notification.class);
                    if (doc.getPostid().equals(postid)&&doc.getPostid()!=null){
                        docsnap.getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                Toast.makeText(mContext, "已刪除", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                Log.d("YourTag", "messageList: " );
            }
        });

    }
}
