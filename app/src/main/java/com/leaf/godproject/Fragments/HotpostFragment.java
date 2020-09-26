package com.leaf.godproject.Fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.leaf.godproject.Adapter.MyFotosAdapter;

import com.leaf.godproject.Model.Post;

import com.leaf.godproject.R;
import com.leaf.godproject.Waitcircle.LoadingView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class HotpostFragment extends Fragment {
    ImageView image_profile, options;
    TextView posts, followers, following, fullname, bio, username;
    Button edit_profile;


    Button myfollowbt,newpostbt;
    FirebaseUser firebaseUser;
    String profileid,Myid;

    private RecyclerView recyclerView;
    private MyFotosAdapter myFotosAdapter;
    private List<Post> prepostList;
    private List<Post> postList;
    private List<Hp> hpList;
    private List<String> postidList;

    LoadingView lv;
    ProgressBar progress_circular;

    View view;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hotpost, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Myid=firebaseUser.getUid();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        initView();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        myFotosAdapter = new MyFotosAdapter(getContext(), postList);
        recyclerView.setAdapter(myFotosAdapter);

        postidList=new ArrayList<>();
        prepostList=new ArrayList<>();
        hpList=new ArrayList<>();

        AllpostInCollection();

        myfollowbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
            }
        });
        newpostbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NewpostFragment()).commit();
            }
        });
        return view;
    }

    private void initView() {
        progress_circular = view.findViewById(R.id.progress_circular);
        image_profile = view.findViewById(R.id.image_profile);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        edit_profile = view.findViewById(R.id.edit_profile);
        username = view.findViewById(R.id.username);
        options = view.findViewById(R.id.options);
        recyclerView = view.findViewById(R.id.recycler_view);
        myfollowbt = view.findViewById(R.id.MYFOLLOWBT);
        newpostbt  = view.findViewById(R.id.NEWPOSTBT);

        lv= view.findViewById(R.id.LV);
    }

    private void hotFotos(){
        postList.clear();
        for (Hp hp : hpList){
            String id=hp.getId();
        for (Post post : prepostList){
            if(id.equals(post.getPostid())){
                postList.add(post);
            }
        }
        }
        myFotosAdapter.notifyDataSetChanged();
//        progress_circular.setVisibility(View.INVISIBLE);
        lv.setVisibility(View.INVISIBLE);
    }
    private void myFotos(){
        Log.i("myFotos", "開始");
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        prepostList.clear();
        CollectionReference reference = db.collection("Posts");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    if(documentSnapshot != null && documentSnapshot.exists()){
                        Post post =  documentSnapshot.toObject(Post.class);
                        if(!post.getPublisher().equals(Myid)){ prepostList.add(post);
                        }
                    }
                }
                hotFotos();
                }
            });
    }
//取得所有postid的like數
    private void ManyLikes(){
        Log.i("ManyLikes", "開始");
        hpList.clear();
        for (int i=0;i<postidList.size();i++){
            final int j=i;
            final String id =postidList.get(i);
            FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Likes").
                document(id).collection("Likers");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                Hp temphp=new Hp(id,querySnapshot.size());
                hpList.add(temphp);
                if(j==postidList.size()-1){
                    MostLikeslist();
                    Log.i("hpList", ""+hpList);
//                    for (int k=0;k<hpList.size();k++){
//                        Log.i("hpList", ""+k+1+" "+hpList.get(k).getLikes());
//                    }
                    myFotos();
                }
            }
        });
        }
    }
//取得所有postid
    private void  AllpostInCollection(){
        Log.i("AllpostInCollection", "開始");
        postidList.clear();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
//        CollectionReference reference = db.collection("Likes");
        CollectionReference reference = db.collection("Posts");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    postidList.add(documentSnapshot.getId());
                }
//                for (int i=0;i<postidList.size();i++){
//                Log.i("postidList", ""+postidList.get(i));
//                }
                ManyLikes();
            }
        });
    }
//排序
    private void MostLikeslist(){
        Log.i("MostLikeslist", "開始");
        Log.i("AllpostInCollection", "排序前"+hpList);
        Collections.sort(hpList, new Comparator<Hp>() {
            @Override
            public int compare(Hp o1, Hp o2) {
                Integer id1=o1.getLikes();
                Integer id2=o2.getLikes();
                return id2.compareTo(id1);
            }
        });
    }

    class Hp{
        private String id;
        private int likes;

        Hp(String id, int likes) {
            this.id = id;
            this.likes = likes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }
    }
}
