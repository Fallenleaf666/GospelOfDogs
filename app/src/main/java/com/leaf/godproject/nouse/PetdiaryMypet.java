package com.leaf.godproject.nouse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.leaf.godproject.R;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PetdiaryMypet extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mFirebaseAuth;
    TextView title,content,random,show,mNoDogView;
    ProgressBar mydogprogressBar ;
    RecyclerView allpetview;
    FloatingActionButton addfab;
    List<Dog> dDog = null;
    DogAdapter dogAdapter;
    private FirebaseUser user;
    private LinkedHashMap<Integer, String> IDmap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petdiarymypet);

        mFirebaseAuth= FirebaseAuth.getInstance();
        initView();


    }

    private void initView() {
        addfab=(FloatingActionButton)findViewById(R.id.AddPetFAB);
        mNoDogView = (TextView) findViewById(R.id.no_dog_text);
        mydogprogressBar=(ProgressBar)findViewById(R.id.MydogprogressBar);
        allpetview=(RecyclerView)findViewById(R.id.MypetView);
        addfab.setOnClickListener(this);
    }

    private void initRecyclerView() {
        dDog = new ArrayList<Dog>();
        dDog.clear();
//        dDog.add(new Dog("該該","5月6號","陽性","公","","米克斯"));
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
//                        Dog dog = documentSnapshot.toObject(Dog.class);
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
                    else{
                        for(int i=0;i<dDog.size();i++){
                            IDmap.clear();
                            IDmap.put(i, dDog.get(i).getID());
                        }
                        dogAdapter = new DogAdapter(getBaseContext(),dDog);
                        allpetview.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                        allpetview.setAdapter(dogAdapter);
                        dogAdapter.setOnItemClickListener(new DogAdapter.OnItemClickListener() {
                            @Override
                            public void OnItemClick(View view, Dog data) {
                                Intent intent = new Intent(PetdiaryMypet.this, PetdiaryDogHome.class);
                                Bundle bundle =new Bundle();
                                bundle.putString("EXTRA_DOG_ID",data.getID());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                Toast.makeText(getBaseContext(), "進入id"+data.getName()+"頁面", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mydogprogressBar.setVisibility(View.GONE);
                    }
                }
            });
        }else{ Toast.makeText(getApplicationContext(),"請確認登入狀態", Toast.LENGTH_LONG).show();}
    }

    private FirebaseUser getFirebaseUser() {
        mFirebaseAuth= FirebaseAuth.getInstance();
        return mFirebaseAuth.getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        if(v==addfab){
            Intent intent = new Intent();
            intent.setClass(PetdiaryMypet.this, PetdiaryAddMypet.class);
            startActivity(intent);
        }
    }
//    public void showPopup(View v) {
//        PopupMenu popup = new PopupMenu(this, v);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.mydogeditmanu, popup.getMenu());
//        popup.setOnMenuItemClickListener(new onMenuItemClick());
//        popup.show();
//    }

//    private class onMenuItemClick implements PopupMenu.OnMenuItemClickListener {
//        @Override
//        public boolean onMenuItemClick(MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.editdog:
//                    Toast.makeText(getBaseContext(), "編輯", Toast.LENGTH_SHORT).show();
//                    return true;
//                case R.id.deletedog:
//                    Toast.makeText(getBaseContext(), "刪除", Toast.LENGTH_SHORT).show();
//                    return true;
//                default:
//                    return false;
//            }
//        }
//    }


//
//    // On clicking a reminder item
//    private void selectReminder(int mClickID) {
//        String mStringClickID = Integer.toString(mClickID);
//
//        // Create intent to edit the reminder
//        // Put reminder id as extra
//        Intent i = new Intent(this, ReminderEditActivity.class);
//        i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
//        startActivityForResult(i, 1);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        後來補的
//        super.onActivityResult(requestCode, resultCode, data);
//        mAdapter.setItemCount(getDefaultItemCount());
//    }
//
//    @Override
//    public void onResume(){
//        super.onResume();
//
//        // To check is there are saved reminders
//        // If there are no reminders display a message asking the user to create reminders
//        List<Reminder> mTest = rb.getAllReminders();
//
//        if (mTest.isEmpty()) {
//            mNoReminderView.setVisibility(View.VISIBLE);
//        } else {
//            mNoReminderView.setVisibility(View.GONE);
//        }
//        mAdapter.setItemCount(getDefaultItemCount());
//    }

//改成for result
    @Override
    protected void onResume() {
        initRecyclerView();
        super.onResume();
    }
}
