package com.leaf.godproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.leaf.godproject.Adapter.DogAdapter;
import com.leaf.godproject.Adapter.MyFotosAdapter;
import com.leaf.godproject.Fragments.APIService;
import com.leaf.godproject.Model.Dog;
import com.leaf.godproject.Model.Post;
import com.leaf.godproject.Model.User;
import com.leaf.godproject.Notification.Client;
import com.leaf.godproject.Notification.Data;
import com.leaf.godproject.Notification.MyResponse;
import com.leaf.godproject.Notification.Sender;
import com.leaf.godproject.Notification.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    ImageView image_profile, options;
    TextView posts, followers, following, fullname, bio, username;
    Button edit_profile;

    APIService apiService;

    private List<String> mySaves;

    FirebaseUser firebaseUser;
    String profileid;

    private RecyclerView recyclerView;
    private MyFotosAdapter myFotosAdapter;
    private List<Post> postList;

    private RecyclerView recyclerView_pets;
    private DogAdapter dogAdapter;
    private List<Dog> dogList;


    private RecyclerView recyclerView_saves;
    private MyFotosAdapter myFotosAdapter_saves;
    private List<Post> postList_saves;

    ImageButton my_fotos, saved_fotos,my_pet;

    FloatingActionButton addfab;

    Drawable followbg;
    Drawable followingbg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_profile3);
//        setContentView(R.layout.fragment_profile4);
        setContentView(R.layout.fragment_profile5);

        followbg=getResources().getDrawable(R.drawable.button_following);
        followingbg=getResources().getDrawable(R.drawable.button_follow);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
    if(getIntent().getStringExtra("publisherid")!=null){
        profileid=getIntent().getStringExtra("publisherid");
    }

        SetActbar();

        image_profile = findViewById(R.id.image_profile);
        posts = findViewById(R.id.posts);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        fullname = findViewById(R.id.fullname);
        bio = findViewById(R.id.bio);
        edit_profile = findViewById(R.id.edit_profile);
        username = findViewById(R.id.username);
        my_fotos = findViewById(R.id.my_fotos);
        saved_fotos = findViewById(R.id.saved_fotos);
        my_pet= findViewById(R.id.my_pets);
//        options = findViewById(R.id.options);

        addfab=findViewById(R.id.ADDDOG);
        addfab.hide();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        myFotosAdapter = new MyFotosAdapter(this, postList);
        recyclerView.setAdapter(myFotosAdapter);


        recyclerView_pets = findViewById(R.id.recycler_view_mypet);
        recyclerView_pets.setHasFixedSize(true);
        LinearLayoutManager mLayoutManagers2 = new GridLayoutManager(this, 1);
        recyclerView_pets.setLayoutManager(mLayoutManagers2);
        dogList = new ArrayList<>();
        dogAdapter = new DogAdapter(this,dogList);
        recyclerView_pets.setAdapter(dogAdapter);


        recyclerView_saves = findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager mLayoutManagers = new GridLayoutManager(this, 3);
        recyclerView_saves.setLayoutManager(mLayoutManagers);
        postList_saves = new ArrayList<>();
        myFotosAdapter_saves = new MyFotosAdapter(this, postList_saves);
        recyclerView_saves.setAdapter(myFotosAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);

        recyclerView_pets.setVisibility(View.GONE);
        recyclerView_saves.setVisibility(View.GONE);

//        addfab.setVisibility(View.GONE);
        recyclerView_pets.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                if(velocityY<0){
                    if(profileid.equals(firebaseUser.getUid())){
                        addfab.show();
                    }
                }
                else if(velocityY>0){
                    addfab.hide();
                }
                return false;
            }
        });


        userInfo();
        getFollowers();
        getNrPosts();
        myFotos();
        mySaves();
        myPets();


        if (profileid.equals(firebaseUser.getUid())){
//            edit_profile.setText("Edit Profile");
            edit_profile.setText("編輯");
        } else {
            checkFollow();
            saved_fotos.setVisibility(View.GONE);
        }

        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, AddMypet.class));
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                FirebaseFirestore db=FirebaseFirestore.getInstance();

//                if (btn.equals("Edit Profile")){
                if (btn.equals("編輯")){

                    startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));

//                } else if (btn.equals("follow")){
                } else if (btn.equals("追蹤")){
                    Map<String,Object> fo=new HashMap<>();
                    db.collection("Follow").document(firebaseUser.getUid()).
                            collection("following").document(profileid).set(fo);
                    db.collection("Follow").document(profileid).
//                            collection("follows").document(firebaseUser.getUid()).set(fo);
                            collection("followers").document(firebaseUser.getUid()).set(fo);
                    addNotification();
//                } else if (btn.equals("following")){
                } else if (btn.equals("追蹤中")){

                    db.collection("Follow").document(firebaseUser.getUid()).
                            collection("following").document(profileid).delete();
                    db.collection("Follow").document(profileid).
//                            collection("follows").document(firebaseUser.getUid()).delete();
                            collection("followers").document(firebaseUser.getUid()).delete();
                }
            }
        });

//        options.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(ProfileActivity.this, OptionsActivity.class));
//            }
//        });

        my_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                addfab.setVisibility(View.GONE);
                addfab.hide();
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_pets.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });


        my_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                addfab.setVisibility(View.VISIBLE);
                if(profileid.equals(firebaseUser.getUid())){
                    addfab.show();
                }
                recyclerView.setVisibility(View.GONE);
                recyclerView_pets.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });

        saved_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                addfab.setVisibility(View.GONE);
                addfab.hide();
                recyclerView.setVisibility(View.GONE);
                recyclerView_pets.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });


        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });
    }

    //    還沒測試過
    private void addNotification(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        FirebaseFirestore db=FirebaseFirestore.getInstance();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

//        reference.push().setValue(hashMap);

        String docid=db.collection("Notifications").document(profileid)
                .collection("notifications").getId();
        db.collection("Notifications").document(profileid)
                .collection("notifications").document(docid).set(hashMap);


        DocumentReference reference = db.collection("Users").
                document(firebaseUser.getUid());

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot=task.getResult();
                if(documentSnapshot != null && documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    sendNotifiaction(profileid,firebaseUser.getUid(),user.getUsername()+"開始追蹤你",user.getImageurl());
                }
            }
        });
    }

    private void userInfo(){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference users = db.collection("Users").document(profileid);
//firestore接收完成
        users.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if (getContext() == null){ return; }
                if(documentSnapshot != null && documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    try {
                    Glide.with(ProfileActivity.this).load(user.getImageurl()).into(image_profile);
                    }catch (Exception e2){}
//                    username.setText(user.getUsername()+" 的個人空間");
//                    fullname.setText(user.getFullname());
//                    bio.setText(user.getBio());
                    String uname= !user.getUsername().equals("") ?user.getUsername():"未命名";
//                        username.setText(user.getUsername()+" 的個人空間");
                    username.setText(uname+" 的個人空間");
                    String fname= !user.getFullname().equals("") ?user.getFullname():"未命名";
                    fullname.setText(fname);
                    String tbio= !user.getBio().equals("") ?user.getBio():"這人什麼都沒有留";
                    bio.setText(tbio);
                }
                else{ }
            }
        });
    }

    private void checkFollow(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Follow").document(firebaseUser.getUid())
                .collection("following").document(profileid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()){
                    edit_profile.setText("追蹤中");
                    edit_profile.setBackground(followbg);
                } else{
                    edit_profile.setText("追蹤");
                    edit_profile.setBackground(followingbg);
                }
            }
        });
    }

    private void getFollowers(){
//        firestore
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference1 = db.collection("Follow").
                document(profileid).collection("followers");
        reference1.addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                followers.setText(""+queryDocumentSnapshots.size());
                Log.d("YourTag", "messageList: " );
            }
        });

        CollectionReference reference2 = db.collection("Follow").
                document(profileid).collection("following");
        reference2.addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                following.setText(""+queryDocumentSnapshots.size());
                Log.d("YourTag", "messageList: " );
            }
        });
    }

    private void getNrPosts(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Posts");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                int i = 0;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Post post = doc.toObject(Post.class);
                    if (post.getPublisher().equals(profileid)){ i++; }
                }
                posts.setText(""+i);
                Log.d("YourTag", "messageList: " );
            }
        });
    }

    private void myFotos(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Posts");
        reference.orderBy("creattime").addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                postList.clear();

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Post post = doc.toObject(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
//                反轉
                Collections.reverse(postList);
                myFotosAdapter.notifyDataSetChanged();
                Log.d("YourTag", "messageList: " );
            }
        });
    }

    private void myPets(){


        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Dogs").
                document(profileid).collection("AllDogs");
//        Toast.makeText(this, ""+profileid, Toast.LENGTH_SHORT).show();
        Log.d("YourTag", ""+reference.toString());
//        reference.orderBy("creattime").addSnapshotListener(new EventListener<QuerySnapshot>()  {
        reference.orderBy("creattime").addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                dogList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Dog dog = doc.toObject(Dog.class);
//                    Toast.makeText(getContext(), ""+dog.getName(), Toast.LENGTH_SHORT).show();
                    if (dog.getMaster().equals(profileid)){
                        dogList.add(dog);
                    }
                }
//                Collections.reverse(dogList);
                dogAdapter.notifyDataSetChanged();
                Log.d("YourTag", "messageList: " );
            }
        });

    }


    private void mySaves(){
        mySaves = new ArrayList<>();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Saves").
                document(firebaseUser.getUid()).collection("Saved");
        reference.orderBy("creattime").addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                mySaves.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    mySaves.add(doc.getId());
                }
                Collections.reverse(mySaves);
                readSaves();
                Log.d("YourTag", "messageList: " );
            }
        });
    }


    private void readSaves(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Posts");
//        1版
//        reference.addSnapshotListener(new EventListener<QuerySnapshot>()  {
//                                          @Override
//                                          public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                                              if (e != null) {
//                                                  Log.w("YourTag", "Listen failed.", e);
//                                                  return;
//                                              }
//                                              for (String id : mySaves) {
//                                                  for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                                                      if (doc.getString("postid").equals(id)) {
//                                                          Post post = doc.toObject(Post.class);
//                                                          postList_saves.add(post);
//                                                      }
//                                                      Log.d("YourTag", "messageList: " );
//                                                  }
//                                              }
//                                              myFotosAdapter_saves.notifyDataSetChanged();
//                                          }
//                                      }
//        );
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                      QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                                                      postList_saves.clear();
                                                      if(querySnapshot!=null){
                                                          for (String id : mySaves) {
                                                              for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                                                  if (doc.getString("postid").equals(id)) {
                                                                      Post post = doc.toObject(Post.class);
                                                                      postList_saves.add(post);
//                            跳出
                                                                      break;
                                                                  }
                                                                  Log.d("YourTag", "messageList: " );
                                                              }
                                                          }
                                                      }
                                                      myFotosAdapter_saves.notifyDataSetChanged();
                                                  }
                                              }
        );
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

    private void SetActbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("搜尋獸醫");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        暫時無用
//        if(!profileid.equals(firebaseUser.getUid())){
//            ImageView logimg=findViewById(R.id.options);
//            logimg.setVisibility(View.GONE);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
