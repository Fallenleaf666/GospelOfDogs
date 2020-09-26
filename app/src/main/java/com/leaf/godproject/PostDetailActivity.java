package com.leaf.godproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.leaf.godproject.Adapter.PostAdapter;
import com.leaf.godproject.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    String postid;

    private Toolbar mToolbar;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        SetActbar();

        SharedPreferences prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
        postid = prefs.getString("postid", "none");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        readPost();

    }

    private void readPost(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Posts").document(postid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                postList.clear();
                if(documentSnapshot != null && documentSnapshot.exists()){
                    Post post = documentSnapshot.toObject(Post.class);
                    postList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }
        });
//            if(postList.size()==0){
//                Toast.makeText(getBaseContext(), "此篇日誌已經不存在!", Toast.LENGTH_SHORT).show();
//            }
    }

    private void SetActbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("搜尋獸醫");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
