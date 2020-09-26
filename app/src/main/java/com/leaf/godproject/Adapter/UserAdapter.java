package com.leaf.godproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.leaf.godproject.Fragments.APIService;
import com.leaf.godproject.Model.User;
import com.leaf.godproject.Notification.Client;
import com.leaf.godproject.Notification.Data;
import com.leaf.godproject.Notification.MyResponse;
import com.leaf.godproject.Notification.Sender;
import com.leaf.godproject.Notification.Token;
import com.leaf.godproject.ProfileActivity;
import com.leaf.godproject.R;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;
    APIService apiService;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<User> users, boolean isFragment){
        mContext = context;
        mUsers = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ImageViewHolder holder, final int position) {
//跑圈圈
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        isFollowing(user.getId(), holder.btn_follow);


        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());
        Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);

        if (user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }


//        看不董
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

//                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new ProfileFragment()).commit();

                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra("publisherid", user.getId());
                    mContext.startActivity(intent);

                } else {
//                    Intent intent = new Intent(mContext, MainActivity.class);
//                    intent.putExtra("publisherid", user.getId());
//                    mContext.startActivity(intent);
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra("publisherid", user.getId());
                    mContext.startActivity(intent);
                }
            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db=FirebaseFirestore.getInstance();
//                if (holder.btn_follow.getText().toString().equals("follow")) {
                if (holder.btn_follow.getText().toString().equals("追蹤")) {
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                            .child("following").child(user.getId()).setValue(true);
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
//                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    Map<String,Object> fo=new HashMap<>();
                    db.collection("Follow").document(firebaseUser.getUid()).
                            collection("following").document(user.getId()).set(fo);
                    db.collection("Follow").document(user.getId()).
                            collection("followers").document(firebaseUser.getUid()).set(fo);
//                    .
//                            addOnSuccessListener(new OnSuccessListener() {
//                                @Override
//                                public void onSuccess(Object o) {
//                                    holder.btn_follow.setText("following");
//                                }
//                            });
//                            collection("follows").document(firebaseUser.getUid());

                    addNotification(user.getId());
                } else {
                    db.collection("Follow").document(firebaseUser.getUid()).
                            collection("following").document(user.getId()).delete();
                    db.collection("Follow").document(user.getId()).
                            collection("followers").document(firebaseUser.getUid()).delete();
//                            .addOnSuccessListener(new OnSuccessListener() {
//                                        @Override
//                                        public void onSuccess(Object o) {
//                                            holder.btn_follow.setText("follow");
////                            跑圈圈
//                                        }
//                                    });
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                            .child("following").child(user.getId()).removeValue();
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
//                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }

        });
    }

    private void addNotification(final String userid){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        FirebaseFirestore db=FirebaseFirestore.getInstance();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "開始追蹤你");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);
        hashMap.put("creattime", System.currentTimeMillis());
//        reference.push().setValue(hashMap);
        String docid=db.collection("Notifications").document(userid)
                .collection("notifications").document().getId();
        db.collection("Notifications").document(userid)
                .collection("notifications").document(docid).set(hashMap);

        DocumentReference reference = db.collection("Users").
                document(firebaseUser.getUid());

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot=task.getResult();
                if(documentSnapshot != null && documentSnapshot.exists()){
                User user = documentSnapshot.toObject(User.class);
                sendNotifiaction(userid,firebaseUser.getUid(),user.getUsername()+"開始追蹤你",user.getImageurl());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;

        public ImageViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userid, final Button button){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Follow").document(firebaseUser.getUid())
                .collection("following").document(userid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot != null && documentSnapshot.exists()){
//                    button.setText("following");
                    button.setText("追蹤中");
                }
                else{
//                    button.setText("follow");
                    button.setText("追蹤");
                }
            }
        });



//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        FirebaseFirestore db=FirebaseFirestore.getInstance();
//        DocumentReference users = db.collection("Follow").document(firebaseUser.getUid()).
//                collection("following").document(userid);
////        CollectionReference users = db.collection("Follow").document(firebaseUser.getUid()).collection("following");
//        users.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                DocumentSnapshot documentSnapshot=task.getResult();
//                    if(documentSnapshot.exists()){ button.setText("following"); }
//                    else{ button.setText("follow"); }
//                }
//            }
//
//        });
    }


    private void sendNotifiaction(final String receiver, final String username, final String message,final String imgurl){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference users = db.collection("Tokens").document(receiver);

        users.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot != null && documentSnapshot.exists()){
                        Token token = documentSnapshot.toObject(Token.class);
                        Data data = new Data(username, R.mipmap.ic_launcher, message, "Gospel Of Dog",
                                receiver,imgurl,"",0);
//                        Toast.makeText(mContext, "傳輸給"+token.getToken(), Toast.LENGTH_SHORT).show();
                        Sender sender = new Sender(data, token.getToken());
                        //        建立通訊
                        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
                        apiService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200){
                                            if (response.body().success != 1){
//                                                Toast.makeText(mContext, "通訊失敗", Toast.LENGTH_SHORT).show();
                                                Log.i("MyFirebaseService","通訊失敗");
                                            }
                                            else{
//                                                Toast.makeText(mContext, "通訊成功", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) { }
                                });
                    }
                }
            }
        });
    }
}