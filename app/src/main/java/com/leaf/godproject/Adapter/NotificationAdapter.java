package com.leaf.godproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.leaf.godproject.Model.Notification;
import com.leaf.godproject.Model.Post;
import com.leaf.godproject.Model.User;
import com.leaf.godproject.PostDetailActivity;
import com.leaf.godproject.ProfileActivity;
import com.leaf.godproject.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context context, List<Notification> notification){
        mContext = context;
        mNotification = notification;
    }

    @NonNull
    @Override
    public NotificationAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notificaion_item, parent, false);
        return new NotificationAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ImageViewHolder holder, final int position) {

        final Notification notification = mNotification.get(position);

        holder.text.setText(notification.getText());

        getUserInfo(holder.image_profile, holder.username, notification.getUserid());

        if (notification.isIspost()) {
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image, notification.getPostid());
        } else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isIspost()) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostid());
                    editor.apply();

                    mContext.startActivity(new Intent(mContext, PostDetailActivity.class));
//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new PostDetailFragment()).commit();
                } else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserid());
                    editor.apply();

                    mContext.startActivity(new Intent(mContext, ProfileActivity.class));
//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new ProfileFragment()).commit();
                }
            }
        });



    }
//
    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image;
        public TextView username, text;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("Users").
                document(publisherid);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>()  {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null ? documentSnapshot.exists() : false){
                User user = documentSnapshot.toObject(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
                }
            }
        });

//        firebase
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Users").child(publisherid);
//
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                Glide.with(mContext).load(user.getImageurl()).into(imageView);
//                username.setText(user.getUsername());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void getPostImage(final ImageView post_image, String postid){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("Posts").
                document(postid);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>()  {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if(documentSnapshot != null ? documentSnapshot.exists() : false){
                if(documentSnapshot != null && documentSnapshot.exists()){
                Post post = documentSnapshot.toObject(Post.class);
                Glide.with(mContext).load(post.getPostimage()).into(post_image);
                }
            }
        });

//        firebase
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Posts").child(postid);
//
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Post post = dataSnapshot.getValue(Post.class);
//                Glide.with(mContext).load(post.getPostimage()).into(post_image);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}