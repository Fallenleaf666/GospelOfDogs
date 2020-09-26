package com.leaf.godproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.leaf.godproject.Model.Story;
import com.leaf.godproject.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long pressTime = 0L;
    long limit = 500L;

    StoriesProgressView storiesProgressView;
    ImageView image, story_photo;
    TextView story_username;

    //
    LinearLayout r_seen;
    TextView seen_number;
    ImageView story_delete;
    //

    List<String> images;
    List<String> storyids;
    String userid;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        storiesProgressView = findViewById(R.id.stories);
        image = findViewById(R.id.image);
        story_photo = findViewById(R.id.story_photo);
        story_username = findViewById(R.id.story_username);

        //
        r_seen = findViewById(R.id.r_seen);
        seen_number = findViewById(R.id.seen_number);
        story_delete = findViewById(R.id.story_delete);

        r_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);
        //

        userid = getIntent().getStringExtra("userid");

        //
        if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            r_seen.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);
        }
        //

        getStories(userid);
        userInfo(userid);

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);


        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

        //
        r_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoryActivity.this, FollowersActivity.class);
                intent.putExtra("id", userid);
                intent.putExtra("storyid", storyids.get(counter));
                intent.putExtra("title", "views");
                startActivity(intent);
            }
        });

        story_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                DocumentReference users = db.collection("Story").document(userid).
                        collection("Stories").document(storyids.get(counter));

                users.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(StoryActivity.this, "刪除成功!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
//                        .child(userid).child(storyids.get(counter));
//                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()){
//                            Toast.makeText(StoryActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//                });
            }
        });
        //
    }

    @Override
    public void onNext() {

        try{
        Glide.with(getApplicationContext()).load(images.get(++counter)).into(image);
        }catch (Exception e){}
        //
        addView(storyids.get(counter));
        seenNumber(storyids.get(counter));
        //
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        try{
        Glide.with(getApplicationContext()).load(images.get(--counter)).into(image);
        }catch (Exception e){}
        //
        seenNumber(storyids.get(counter));
        //
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String userid){
        images = new ArrayList<>();
        storyids = new ArrayList<>();

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Story").
                document(userid).collection("Stories");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                images.clear();
                storyids.clear();
                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    Story story = documentSnapshot.toObject(Story.class);
                    long timecurrent = System.currentTimeMillis();
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                        images.add(story.getImageurl());
                        storyids.add(story.getStoryid());
                    }
                }
                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);

                try{
                Glide.with(getApplicationContext()).load(images.get(counter)).into(image);
                }catch (Exception e){}
                //
                addView(storyids.get(counter));
                seenNumber(storyids.get(counter));

                Log.d("StoryActivity", "getStories" );

            }
                                              });
            //        firebase初版
//        FirebaseFirestore db=FirebaseFirestore.getInstance();
//        CollectionReference reference = db.collection("Story").
//                document(userid).collection("Stories");
//        reference.addSnapshotListener(new EventListener<QuerySnapshot>()  {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("YourTag", "Listen failed.", e);
//                    return;
//                }
//                images.clear();
//                storyids.clear();
//                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
//                    Story story = doc.toObject(Story.class);
//                    long timecurrent = System.currentTimeMillis();
//                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
//                        images.add(story.getImageurl());
//                        storyids.add(story.getStoryid());
//                    }
//                }
//
//                storiesProgressView.setStoriesCount(images.size());
//                storiesProgressView.setStoryDuration(5000L);
//                storiesProgressView.setStoriesListener(StoryActivity.this);
//                storiesProgressView.startStories(counter);
//
//                Glide.with(getApplicationContext()).load(images.get(counter)).into(image);
//                //
//                addView(storyids.get(counter));
//                seenNumber(storyids.get(counter));
//
//                Log.d("YourTag", "messageList: " );
//            }
//        });

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
//                .child(userid);
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                images.clear();
//                storyids.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Story story = snapshot.getValue(Story.class);
//                    long timecurrent = System.currentTimeMillis();
//                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
//                        images.add(story.getImageurl());
//                        storyids.add(story.getStoryid());
//                    }
//                }
//
//                storiesProgressView.setStoriesCount(images.size());
//                storiesProgressView.setStoryDuration(5000L);
//                storiesProgressView.setStoriesListener(StoryActivity.this);
//                storiesProgressView.startStories(counter);
//
//                Glide.with(getApplicationContext()).load(images.get(counter)).into(image);
//                //
//                addView(storyids.get(counter));
//                seenNumber(storyids.get(counter));
//                //
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void userInfo(String userid){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("Users").document(userid);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    try{
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(story_photo);
                    }catch (Exception e2){}
                    story_username.setText(user.getUsername());
                }
            }
        });

//        firebase
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
//                .child(userid);
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                Glide.with(getApplicationContext()).load(user.getImageurl()).into(story_photo);
//                story_username.setText(user.getUsername());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    //
    private void addView(String storyid){
        Map<String,Object> fo=new HashMap<>();
        FirebaseFirestore.getInstance().collection("Story").document(userid)
                .collection("Stories").document(storyid)
                .collection("views")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(fo);


//        FirebaseDatabase.getInstance().getReference().child("Story").child(userid)
//                .child(storyid).child("views")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .setValue(true);
    }

    private void seenNumber(String storyid){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Story").document(userid)
                .collection("Stories").document(storyid)
                .collection("views");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                seen_number.setText(""+queryDocumentSnapshots.size());
                Log.d("YourTag", "messageList: " );
            }
        });

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
//                .child(userid).child(storyid).child("views");
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                seen_number.setText(""+dataSnapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
    //
}
