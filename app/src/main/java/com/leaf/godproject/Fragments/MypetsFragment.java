package com.leaf.godproject.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.leaf.godproject.nouse.Dog;
import com.leaf.godproject.nouse.DogAdapter;
import com.leaf.godproject.nouse.PetdiaryAddMypet;
import com.leaf.godproject.nouse.PetdiaryDogHome;
import com.leaf.godproject.R;

import java.util.ArrayList;
import java.util.List;

public class MypetsFragment extends Fragment {

    FirebaseAuth mFirebaseAuth;
    TextView title,content,random,show,mNoDogView;
    ProgressBar mydogprogressBar ;
    RecyclerView allpetview;
    FloatingActionButton addfab;
    List<Dog> dDog = null;
    DogAdapter dogAdapter;
    View view;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_petdiarymypet, container, false);

        mFirebaseAuth= FirebaseAuth.getInstance();
        initView();
        return view;
    }
    private void initView() {
        addfab=(FloatingActionButton)view.findViewById(R.id.AddPetFAB);
        mNoDogView = (TextView) view.findViewById(R.id.no_dog_text);
        mydogprogressBar=(ProgressBar)view.findViewById(R.id.MydogprogressBar);
        allpetview=(RecyclerView)view.findViewById(R.id.MypetView);

        addfab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setClass(getContext(), PetdiaryAddMypet.class);
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        dDog = new ArrayList<Dog>();
        dDog.clear();
        mydogprogressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = getFirebaseUser();
        if(user!=null){
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            CollectionReference mydogs = db
                    .collection("Users").document(user.getUid()).collection("MYDOGS");
//            Query query=mydogs.limit(20);
            mydogs.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        Dog dog = new Dog();
                        dog.setName(documentSnapshot.getString("dNAME"));
                        dog.setID(documentSnapshot.getString("dID"));
                        dog.setBirth(documentSnapshot.getString("dBIRTH"));
                        dog.setGender(documentSnapshot.getString("dGENDER"));
                        dog.setImg(documentSnapshot.getString("dIMG"));
                        dog.setType(documentSnapshot.getString("dTYPE"));
                        dog.setMixType((documentSnapshot.getString("dMIXTYPE")));
                        dog.setBlood(documentSnapshot.getString("dBLOOD"));

                        Log.d("文黨", ""+documentSnapshot);
                        dDog.add(dog);
                    }
                    if (dDog.isEmpty()) {
                        mNoDogView.setVisibility(View.VISIBLE);
                        mydogprogressBar.setVisibility(View.GONE);}

                        dogAdapter = new DogAdapter(getContext(),dDog);
                        allpetview.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                        allpetview.setAdapter(dogAdapter);
                        dogAdapter.setOnItemClickListener(new DogAdapter.OnItemClickListener() {
                            @Override
                            public void OnItemClick(View view, Dog data) {
                                Intent intent = new Intent(getContext(), PetdiaryDogHome.class);
                                Bundle bundle =new Bundle();
                                bundle.putString("EXTRA_DOG_ID",data.getID());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                Toast.makeText(getContext(), "進入id"+data.getName()+"頁面", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mydogprogressBar.setVisibility(View.GONE);
                    }
                });
            }else{ Toast.makeText(getContext(),"請確認登入狀態", Toast.LENGTH_LONG).show();}
    }

    private FirebaseUser getFirebaseUser() {
        mFirebaseAuth= FirebaseAuth.getInstance();
        return mFirebaseAuth.getCurrentUser();
    }

    @Override
    public void onResume() {
        initRecyclerView();
        super.onResume();
    }
}
