package com.leaf.godproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.leaf.godproject.Model.Comment;
import com.leaf.godproject.Model.User;
import com.leaf.godproject.ProfileActivity;
import com.leaf.godproject.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Comment> mComment;
    private String postid;

    private FirebaseUser firebaseUser;

    public CommentAdapter(Context context, List<Comment> comments, String postid){
        mContext = context;
        mComment = comments;
        this.postid = postid;
    }

    @NonNull
    @Override
    public CommentAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get(position);

        holder.comment.setText(comment.getComment());
        getUserInfo(holder.image_profile, holder.username, comment.getPublisher());

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(mContext, MainActivity.class);
//                intent.putExtra("publisherid", comment.getPublisher());
//                mContext.startActivity(intent);

                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                mContext.startActivity(intent);


            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext, MainActivity.class);
//                intent.putExtra("publisherid", comment.getPublisher());
//                mContext.startActivity(intent);

                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                mContext.startActivity(intent);


            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (comment.getPublisher().equals(firebaseUser.getUid())) {

                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setTitle("確定要刪除嗎?");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseFirestore.getInstance().collection("Comments").
                                                document(postid).collection("AllComments")
                                                .document(comment.getCommentid()).delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(mContext, "已刪除!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile;
        public TextView username, comment;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("Users").document(publisherid);

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
//                        Toast.makeText(mContext, "取得成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
