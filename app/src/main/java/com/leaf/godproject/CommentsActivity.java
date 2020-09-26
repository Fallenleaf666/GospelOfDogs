package com.leaf.godproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.leaf.godproject.Adapter.CommentAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.leaf.godproject.Adapter.CommentAdapter;
import com.leaf.godproject.Fragments.APIService;
import com.leaf.godproject.Model.Comment;
import com.leaf.godproject.Model.Post;
import com.leaf.godproject.Model.User;
import com.leaf.godproject.Notification.Client;
import com.leaf.godproject.Notification.Data;
import com.leaf.godproject.Notification.MyResponse;
import com.leaf.godproject.Notification.Sender;
import com.leaf.godproject.Notification.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    APIService apiService;

    EditText addcomment;
    ImageView image_profile;
    TextView post;

    String postid;
    String publisherid;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

//        新增時間屬性排序

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("評論");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postid);
        recyclerView.setAdapter(commentAdapter);

        post = findViewById(R.id.post);
        addcomment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addcomment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this, "請填入內容", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });

        getImage();
        readComments();

    }

    private void addComment(){

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

//        String commentid = reference.push().getKey();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        String commentid=db.collection("Comments").document(postid)
                .collection("AllComments").document().getId();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addcomment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("commentid", commentid);
        hashMap.put("creattime", System.currentTimeMillis());
        FirebaseFirestore.getInstance().collection("Comments").document(postid)
                .collection("AllComments").document(commentid).set(hashMap).
                addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
//                        自己家的
//                        readComments();
//                        Toast.makeText(CommentsActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
                    }
                });

//        reference.child(commentid).setValue(hashMap);
        if(!firebaseUser.getUid().equals(publisherid)){
            addNotification();
        }
        addcomment.setText("");
    }

    private void addNotification(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        FirebaseFirestore db=FirebaseFirestore.getInstance();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "評論了你的貼文: "+addcomment.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);
        hashMap.put("creattime", System.currentTimeMillis());

//        reference.push().setValue(hashMap);

        String docid=db.collection("Notifications").document(publisherid)
                .collection("notifications").document().getId();
        db.collection("Notifications").document(publisherid)
                .collection("notifications").document(docid).set(hashMap);

        DocumentReference reference = db.collection("Users").
                document(firebaseUser.getUid());

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
                                sendNotifiaction(publisherid,firebaseUser.getUid(),uname+" 在你的日誌留言",post.getPostimage());
                            }
                        }
                    });
                }
            }
        });


    }

    private void getImage(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference users = db.collection("Users").document(firebaseUser.getUid());
//firestore接收完成
//        可以改成oncomplete
        users.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot != null && documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    try {
                        Glide.with(CommentsActivity.this).load(user.getImageurl()).into(image_profile);
                    }catch (Exception e1){ }
//                    Glide.with(CommentsActivity.this).load(user.getImageurl()).into(image_profile);
                }
            }
        });


//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void readComments(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Comments").
                document(postid).collection("AllComments");
        reference.orderBy("creattime").addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                commentList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Comment comment = doc.toObject(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
                Log.d("YourTag", "messageList: " );
            }
        });



//        store版本
//        FirebaseFirestore db=FirebaseFirestore.getInstance();
//        CollectionReference reference = db.collection("Comments").document(postid)
//                .collection("AllComments");
//        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
//                commentList.clear();
//                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
//                    Comment comment = documentSnapshot.toObject(Comment.class);
//                    commentList.add(comment);
//                }
//                commentAdapter.notifyDataSetChanged();
//            }
//        }
//        );
//firebase版本
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                commentList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Comment comment = snapshot.getValue(Comment.class);
//                    commentList.add(comment);
//                }
//                commentAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }


    private void sendNotifiaction(final String receiver, final String username, final String message,final String imgurl){

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
                                receiver,imgurl,postid,1);
//                        Toast.makeText(getApplicationContext(), "傳輸給"+token.getToken(), Toast.LENGTH_SHORT).show();
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
//                                            else{Toast.makeText(getApplicationContext(), "通訊成功", Toast.LENGTH_SHORT).show();}
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
