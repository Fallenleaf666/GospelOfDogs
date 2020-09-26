package com.leaf.godproject.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.Query;
//import com.leaf.godproject.CommentsActivity;
//import com.leaf.godproject.FollowersActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.leaf.godproject.CommentsActivity;
import com.leaf.godproject.FollowersActivity;
import com.leaf.godproject.Fragments.APIService;
import com.leaf.godproject.Model.Dog;
import com.leaf.godproject.Model.Post;
import com.leaf.godproject.Model.User;
import com.leaf.godproject.Notification.Client;
import com.leaf.godproject.Notification.Data;
import com.leaf.godproject.Notification.MyResponse;
import com.leaf.godproject.Notification.Sender;
import com.leaf.godproject.Notification.Token;
import com.leaf.godproject.ProfileActivity;
import com.leaf.godproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    APIService apiService;
    List<Dog> dogList;

    private FirebaseUser firebaseUser;
//    private ListenerRegistration registration1,registration2;

    public PostAdapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPosts.get(position);

//        建立通訊
//        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        Glide.with(mContext).load(post.getPostimage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(holder.post_image);

        if (post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
//
        publisherInfo(holder.image_profile, holder.username, holder.publisher, post.getPublisher());
        if(firebaseUser!=null){
        isLiked(post.getPostid(), holder.like);
        isSaved(post.getPostid(), holder.save);
        }
        nrLikes(holder.likes, post.getPostid());
        getCommetns(post.getPostid(), holder.comments);

        if(firebaseUser!=null){
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like.getTag().equals("like")) {

                    Map<String,Object> fo=new HashMap<>();
                    FirebaseFirestore.getInstance().collection("Likes").document(post.getPostid())
                            .collection("Likers").document(firebaseUser.getUid()).set(fo);

                    if(!firebaseUser.getUid().equals(post.getPublisher())){
                    addNotification(post.getPublisher(), post.getPostid());
                    }
                } else {
                    FirebaseFirestore.getInstance().collection("Likes").document(post.getPostid())
                            .collection("Likers").document(firebaseUser.getUid()).delete();
                }
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save.getTag().equals("save")){
                    Map<String,Object> fo=new HashMap<>();
                    fo.put("creattime", System.currentTimeMillis());
                    FirebaseFirestore.getInstance().collection("Saves").document(firebaseUser.getUid()).
                            collection("Saved").document(post.getPostid()).set(fo);
//                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
//                            .child(post.getPostid()).setValue(true);
                } else {
                    FirebaseFirestore.getInstance().collection("Saves").document(firebaseUser.getUid()).
                            collection("Saved").document(post.getPostid()).delete();
//                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
//                            .child(post.getPostid()).removeValue();
                }
            }
        });

        }

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ProfileFragment()).commit();

//                FragmentManager fragmentmanager=((FragmentActivity)mContext).getSupportFragmentManager();
//                fragmentmanager.beginTransaction()
//                        .replace(R.id.fragment_container, new ProfileFragment())
//                        .addToBackStack(fragmentmanager.getClass().getName())
//                        .commit();

                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);

            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();
//原版'
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ProfileFragment()).commit();

//                FragmentManager fragmentmanager=((FragmentActivity)mContext).getSupportFragmentManager();
//                fragmentmanager.beginTransaction()
//                        .replace(R.id.fragment_container, new ProfileFragment())
//                        .addToBackStack(fragmentmanager.getClass().getName())
//                        .commit();

                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);

            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();
//原版
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ProfileFragment()).commit();
//
//                FragmentManager fragmentmanager=((FragmentActivity)mContext).getSupportFragmentManager();
//                fragmentmanager.beginTransaction()
//                        .replace(R.id.fragment_container, new ProfileFragment())
//                        .addToBackStack(fragmentmanager.getClass().getName())
//                        .commit();

                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//                editor.putString("postid", post.getPostid());
//                editor.apply();
//原本
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new PostDetailFragment()).commit();
//後來改的
//                SharedPreferences prefs2 = mContext.getSharedPreferences("PREFS", MODE_PRIVATE);
//                String thispostid = prefs2.getString("postid", "none");
//                if(!thispostid.equals(post.getPostid())){
//                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//                    editor.putString("postid", post.getPostid());
//                    editor.apply();

//                    取消跳頁
//                    FragmentManager fragmentmanager=((FragmentActivity)mContext).getSupportFragmentManager();
//                    fragmentmanager.beginTransaction()
//                            .replace(R.id.fragment_container, new PostDetailFragment())
//                            .addToBackStack(fragmentmanager.getClass().getName())
//                            .commit();
//                }
            }
        });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title", "likes");
                mContext.startActivity(intent);
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edit:
                                editPost(post.getPostid());
                                return true;
                            case R.id.trans:
                                transPetdiary(post.getPostid(),post.getPetid());
                                return true;
                            case R.id.delete:
                                final String id = post.getPostid();

                                FirebaseFirestore.getInstance().collection("Posts").
                                        document(post.getPostid()).delete();
                                deleteNotifications(id, firebaseUser.getUid());
                                return true;
                            case R.id.report:
                                sendErrorNotifiaction(firebaseUser.getUid(),post.getPostimage(),post.getPostid());
                                Toast.makeText(mContext, "我們已經收到回報訊息，將會審核這篇文章是否有不當訊息", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if (!post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.trans).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }else if(post.getPublisher().equals(firebaseUser.getUid()))
                {
//                    使用者不用看到自己的檢舉
                    popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                }
                popupMenu.show();
            }
        });

        SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm");
//        注意import
//        String date=format.format(new Date(post.getCreattime()*1000L));
        String date=format.format(new Date(post.getCreattime()));
        holder.creatdate.setText(date);

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {


        public ImageView image_profile, post_image, like, comment, save, more;
        public TextView username, likes, publisher, description, comments,creatdate;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
            more = itemView.findViewById(R.id.more);
            creatdate= itemView.findViewById(R.id.creatdate);
        }
    }

    private void addNotification(final String userid,final String postid){

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "喜歡你的日誌");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("creattime", System.currentTimeMillis());

//        reference.push().setValue(hashMap);

        String docid=FirebaseFirestore.getInstance().collection("Notifications").document(userid)
                .collection("notifications").document().getId();
        FirebaseFirestore.getInstance().collection("Notifications").document(userid)
                .collection("notifications").document(docid).set(hashMap);

//        可以精簡化，考慮把暱稱存在app裡面
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference users = db.collection("Users").document(firebaseUser.getUid());
        users.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot != null && documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);
                        final String uname=user.getUsername();

                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        DocumentReference reference = db.collection("Posts").
                                document(postid);
                        reference.addSnapshotListener(new EventListener<DocumentSnapshot>()  {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if(documentSnapshot != null && documentSnapshot.exists()){
                                    Post post = documentSnapshot.toObject(Post.class);
                                    sendNotifiaction(userid,uname,"喜歡你的日誌",post.getPostimage(),post.getPostid());
                                }
                            }
                        });



                    }
                }
            }
        });
    }

    private void deleteNotifications(final String postid, String userid){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Notifications").
                document(userid).collection("notifications");
//        原版
//        reference.whereEqualTo("postid",postid)
//                .addSnapshotListener(new EventListener<QuerySnapshot>()  {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("YourTag", "Listen failed.", e);
//                    return;
//                }
//                for (QueryDocumentSnapshot docsnap : queryDocumentSnapshots) {
//                    Notification doc=docsnap.toObject(Notification.class);
////                    if (doc.getPostid().equals(postid)&&doc.getPostid()!=null){
////                        docsnap.getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
////                            @Override
////                            public void onComplete(@NonNull Task<Void> task) {
////                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
////                            }
////                        });
////                    }
//                    docsnap.getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
////                            Toast.makeText(mContext, "已刪除!", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//                //                            刪除後回到上一頁
//                Toast.makeText(mContext, "已刪除!", Toast.LENGTH_SHORT).show();
//                Activity activity =(Activity)mContext;
//                activity.finish();
//                Log.d("YourTag", "messageList: " );
//            }
//        });
//        改版
        reference.whereEqualTo("postid",postid)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                if(querySnapshot!=null){
                        for (DocumentSnapshot docsnap : querySnapshot.getDocuments()) {
                            docsnap.getReference().delete();
                        }
                }
                        Toast.makeText(mContext, "已刪除!", Toast.LENGTH_SHORT).show();
                        Activity activity =(Activity)mContext;
                        activity.finish();
                        Log.d("YourTag", "完成刪除日誌與提醒" );
            }
                });

    }


    private void sendNotifiaction(final String receiver, final String username, final String message,final String imgurl,final String pid){

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
                        Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username+" "+message, "Gospel Of Dog",
                                receiver,imgurl,pid,1);
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

    private void nrLikes(final TextView likes, String postId){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Likes").
                document(postId).collection("Likers");
//監聽Likes集合變化
        reference.addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                likes.setText(queryDocumentSnapshots.size()+" likes");
            }
        });
        Log.d("PostAdapter", "已經取得日誌愛心數量" );
        //取得Likes數量
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                likes.setText(querySnapshot.size()+" likes");
            }
        });
    }

    private void getCommetns(String postId, final TextView comments){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Comments").
                document(postId).collection("AllComments");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                comments.setText("觀看全部"+queryDocumentSnapshots.size()+" 則評論");
                Log.d("PostAdapter", "已經取得全部評論" );
            }
        });
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, final String userid){

        FirebaseFirestore.getInstance().collection("Users").document(userid)
        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    User user = documentSnapshot.toObject(User.class);
                    Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                    username.setText(user.getUsername());
                    publisher.setText(user.getUsername());
                }
            }
        });
    }

    private void isLiked(final String postid, final ImageView imageView){
        FirebaseFirestore db=FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("Likes").document(postid)
                .collection("Likers").document(firebaseUser.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot != null && documentSnapshot.exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }
                else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }
        });
//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        FirebaseFirestore db=FirebaseFirestore.getInstance();
//
//        DocumentReference reference = db.collection("Likes").document(postid).
//                collection("Likers").document(firebaseUser.getUid());
//
//        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot documentSnapshot=task.getResult();
//                    if(documentSnapshot.exists()){
//                        imageView.setImageResource(R.drawable.ic_liked);
//                        imageView.setTag("liked");
//                    }
//                    else{
//                        imageView.setImageResource(R.drawable.ic_like);
//                        imageView.setTag("like");
//                    }
//                }
//            }
//        });

    }

    private void isSaved(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db=FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("Saves").document(firebaseUser.getUid())
                .collection("Saved").document(postid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()){
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                } else{
                    imageView.setImageResource(R.drawable.ic_savee_black);
                    imageView.setTag("save");
                }
            }
        });
    }

    private void editPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("編輯日誌");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, editText);

        alertDialog.setPositiveButton("更新",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("description", editText.getText().toString());

                        FirebaseFirestore.getInstance().collection("Posts").document(postid)
                                .update(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                            }
                                        });
                    }
                });
        alertDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialog.show();
    }

    private void getText(String postid, final EditText editText){
                FirebaseFirestore db=FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Posts").document(postid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    editText.setText((documentSnapshot.toObject(Post.class)).getDescription());
                }
            }
        });
    }




    private void sendErrorNotifiaction( final String username,final String imgurl,final String pid){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference users = db.collection("Tokens").document("RRcgMaDBFnNhU47CQbLrSywEDao1");

        users.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot != null && documentSnapshot.exists()){
                        Token token = documentSnapshot.toObject(Token.class);
                        Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, "檢舉 : 用戶id "+username+"檢舉了這篇日誌", "Gospel Of Dog",
                                "RRcgMaDBFnNhU47CQbLrSywEDao1",imgurl,pid,1);
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

    private void transPetdiary(final String pid,final String petid) {
        dogList= new ArrayList<>();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Dogs").
                document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("AllDogs");
        reference.orderBy("creattime").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                    dogList.clear();
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        Dog dog =  documentSnapshot.toObject(Dog.class);
                        dogList.add(dog);
                    }
                    final String[] items = new String[dogList.size()+1];
                    items[0] = "預設";
                    for (int i=1;i<items.length;i++){
                        if(dogList.get(i-1).getName().equals("")){ items[i] = "未命名"; }
                        else{items[i] = dogList.get(i-1).getName();}
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("目標相簿");
                    Log.d("選擇相簿", "items列表長度"+items.length);
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            if(item==0){
                                if(petid.equals("")){
                                Toast.makeText(mContext, "這是同一個相簿", Toast.LENGTH_SHORT).show();
                                }else{
                                    hashMap.put("petid","");
                                    FirebaseFirestore.getInstance().collection("Posts").document(pid)
                                            .update(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            Toast.makeText(mContext, "移動成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            else {
                                if(petid.equals(dogList.get(item-1).getId())){
                                    Toast.makeText(mContext, "這是同一個相簿", Toast.LENGTH_SHORT).show();
                                }else{
                                hashMap.put("petid", dogList.get(item-1).getId());
                                FirebaseFirestore.getInstance().collection("Posts").document(pid)
                                        .update(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                Toast.makeText(mContext, "移動成功", Toast.LENGTH_SHORT).show();
                                                    }
                                        });
                                }
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    }
                }
        );
    }
}