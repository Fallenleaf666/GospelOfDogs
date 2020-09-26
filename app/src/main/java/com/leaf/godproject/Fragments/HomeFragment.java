package com.leaf.godproject.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.leaf.godproject.Adapter.PostAdapter;
import com.leaf.godproject.Adapter.StoryAdapter;
import com.leaf.godproject.Model.Post;
import com.leaf.godproject.Model.Story;
import com.leaf.godproject.R;
import com.leaf.godproject.Waitcircle.LoadingView;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private TextView nopost,nopost2;
    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;



    private static final int GOTO_MAIN_ACTIVITY = 16;

    private List<String> followingList;

    Button hotpostbt,newpostpt;

    LoadingView lv;
    ProgressBar progress_circular;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        lv= view.findViewById(R.id.LV);
        progress_circular = view.findViewById(R.id.progress_circular);
        nopost=view.findViewById(R.id.nopost);
        nopost2=view.findViewById(R.id.nopost2);

        hotpostbt=view.findViewById(R.id.HOTPOSTBT);
        newpostpt=view.findViewById(R.id.NEWPOSTBT);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        recyclerView_story = view.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);
//
//
        try {
            checkFollowing();
        }catch (Exception e){}

        return view;
    }

    private void checkFollowing(){
        Log.e("Following","checkFollowing");
        followingList = new ArrayList<>();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Follow").
                document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("following");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                followingList.clear();
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    followingList.add(documentSnapshot.getId());
                }
                readPosts();
                readStory();
            }
        });

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .child("following");

//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                followingList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    followingList.add(snapshot.getKey());
//                }
//                readPosts();
//                readStory();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        hotpostbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HotpostFragment()).commit();
            }
        });
        newpostpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NewpostFragment()).commit();
            }
        });
    }

    private void readPosts(){
        Log.e("readPosts","開始readpost");
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Posts");
        reference.orderBy("creattime").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                postList.clear();
                assert querySnapshot != null;

//                if(followingList.size()!=0){
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    Post post =  documentSnapshot.toObject(Post.class);
                    for (String id : followingList){
                        if (post.getPublisher().equals(id)){
                            postList.add(post);
                        }
                    }
                postAdapter.notifyDataSetChanged();
                    lv.setVisibility(View.INVISIBLE);
                }
//                }else{
//                    lv.setVisibility(View.INVISIBLE);
//                }

                if(postList.size()==0){nopost2.setVisibility(View.VISIBLE);}
                if(postList.size()!=0){nopost.setVisibility(View.VISIBLE);}
            }
        }
);
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                postList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Post post = snapshot.getValue(Post.class);
//                    for (String id : followingList){
//                        if (post.getPublisher().equals(id)){
//                            postList.add(post);
//                        }
//                    }
//                }
//                postAdapter.notifyDataSetChanged();
//                progress_circular.setVisibility(View.GONE);
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void readStory(){
        Log.e("Story","開始readStory");
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Story");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                final long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("", 0, 0, "",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id : followingList) {
                    FirebaseFirestore db=FirebaseFirestore.getInstance();
                    CollectionReference reference = db.collection("Story").
                            document(id).collection("Stories");
                    reference.addSnapshotListener(new EventListener<QuerySnapshot>()  {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("YourTag", "Listen failed.", e);
                                return;
                            }
                            Story story = null;
                            int countStory = 0;
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if (doc.exists()){
                                    story = doc.toObject(Story.class);
                                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                                        countStory++;
                                    }
                                }
                            }
                            if (countStory > 0){
                                storyList.add(story);
                            }
//                            Log.d("YourTag", "messageList: " );
                        }
                    });
                }
                storyAdapter.notifyDataSetChanged();
            }
                                      });
    }




//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            if (msg.what == GOTO_MAIN_ACTIVITY) {
//
//            }
//        }
//    };

//        private class AsyncTask1 extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//            postAdapter.notifyDataSetChanged();
//            return null;
//        }
//    }

}
