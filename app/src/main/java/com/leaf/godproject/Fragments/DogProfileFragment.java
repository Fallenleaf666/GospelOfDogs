package com.leaf.godproject.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.leaf.godproject.Adapter.MyFotosAdapter;
import com.leaf.godproject.Model.Dog;
import com.leaf.godproject.Model.Post;
import com.leaf.godproject.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class DogProfileFragment extends Fragment {
    ImageView image_dogprofile,dog_gender;
    TextView dogname, dogtype, dogblood,dogbirth,titlename;
    Button edit_profile;
    String profileid,dogprofileid;

    private RecyclerView recyclerView;
    private MyFotosAdapter myFotosAdapter;
    private List<Post> postList;
    View view;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dogprofile, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);

        profileid = prefs.getString("profileid", "none");
        dogprofileid = prefs.getString("DOG_ID", "none");

        initview();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        myFotosAdapter = new MyFotosAdapter(getContext(), postList);
        recyclerView.setAdapter(myFotosAdapter);

        dogInfo();
        myPhotos();




        return view;
    }

    private void myPhotos() {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Posts");
        reference.orderBy("creattime").whereEqualTo("petid",dogprofileid).addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                postList.clear();

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Post post = doc.toObject(Post.class);
//                    if (post.getPublisher().equals(profileid)&&post.get相不)
                    {
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

    private void dogInfo() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference users = db.collection("Dogs").document(profileid)
                .collection("AllDogs").document(dogprofileid);
//firestore接收完成
        users.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (this == null){ return; }
                if(documentSnapshot.exists()){
                    Dog dog = documentSnapshot.toObject(Dog.class);

                    Glide.with(getContext()).load(dog.getImgurl()).into(image_dogprofile);
                    if(dog.getGender().equals("公")){
                        dog_gender.setImageResource(R.drawable.female);
                    }
                    else if(dog.getGender().equals("母")){
                        dog_gender.setImageResource(R.drawable.male);
                    }
                    String infoname=(!dog.getName().equals("") ?dog.getName():"未命名");
                    dogname.setText(infoname);
                    titlename.setText(infoname+" 的寵物日誌");

                    if(!dog.getMixtype().equals("無")){
                        String type=dog.getType()+" 混 "+dog.getMixtype();
                        dogtype.setText(type);
                    }else{
                        String type=!dog.getType().equals("") ?dog.getType():"未知品種";
                        dogtype.setText(type);}

                    String blood=!dog.getBlood().equals("") ?dog.getBlood():"未知血型";
                    dogblood.setText(blood);

                    String birth=!dog.getBirth().equals("") ?dog.getBirth():"未知生日";
                    dogbirth.setText(birth);
                }
            }
        });
    }

    private void initview() {
        image_dogprofile = view.findViewById(R.id.image_dog_profile);

        dogname = view.findViewById(R.id.dogname);
        dogtype = view.findViewById(R.id.dogtype);
        dogblood = view.findViewById(R.id.dogblood);
        dogbirth = view.findViewById(R.id.dogbirth);
        dog_gender=view.findViewById(R.id.doggender);
        titlename=view.findViewById(R.id.username);


        recyclerView = view.findViewById(R.id.recycler_view);
    }
}
