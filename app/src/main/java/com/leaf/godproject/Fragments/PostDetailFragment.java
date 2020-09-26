package com.leaf.godproject.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.leaf.godproject.Adapter.PostAdapter;
import com.leaf.godproject.Model.Post;
import com.leaf.godproject.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class PostDetailFragment extends Fragment {

    String postid;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        postid = prefs.getString("postid", "none");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        readPost();

        return view;
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

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
//
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                postList.clear();
//                Post post = dataSnapshot.getValue(Post.class);
//                postList.add(post);
//
//                postAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public void onDestroy() {
//        清空
        SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("postid", "");
        editor.apply();
        super.onDestroy();
    }


}