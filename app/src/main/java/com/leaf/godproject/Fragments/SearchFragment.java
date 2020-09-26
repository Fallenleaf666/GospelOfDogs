package com.leaf.godproject.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.leaf.godproject.Adapter.UserAdapter;
import com.leaf.godproject.Model.User;
import com.leaf.godproject.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    EditText search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_bar = view.findViewById(R.id.search_bar);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), userList, true);
        recyclerView.setAdapter(userAdapter);

        readUsers();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                searchUsers(charSequence.toString().toLowerCase());
//                if(charSequence.toString().equals("")){readUsers();}
////                else{searchUsers(charSequence.toString());}
//                else{searchUsers(search_bar.getText().toString());}
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(search_bar.getText().toString().equals("")){readUsers();}
                else{searchUsers(search_bar.getText().toString()); }
            }
        });
        return view;
    }

    private void searchUsers(String s){
//        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
//        .startAt(s)
//                .endAt(s+"\uf8ff");
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference users = db.collection("Users");
//        Query query=users.limit(20).startAt(s).endAt(s+"\uf8ff");
        Query query=users.orderBy("username").startAt(s).endAt(s+"\uf8ff");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                  @Override
                                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                      QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                                      if(querySnapshot!=null){
                                          userList.clear();
                                          for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                              User user = documentSnapshot.toObject(User.class);
//                                              Log.d("字串"+documentSnapshot.toString(),"已登出");
                                              userList.add(user);
                                          }
                                          userAdapter.notifyDataSetChanged();
//                                          Toast.makeText(getContext(),"查詢完成"+userList, Toast.LENGTH_LONG).show();
                                      }
                                      else{Toast.makeText(getContext(),"查詢失敗", Toast.LENGTH_LONG).show();}
                                      }
                                  });

//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    User user = snapshot.getValue(User.class);
//                    userList.add(user);
//                }
//
//                userAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (search_bar.getText().toString().equals("")) {
                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                if(querySnapshot!=null){
                    userList.clear();
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        User user = documentSnapshot.toObject(User.class);
                        userList.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }
            }
        });


//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (search_bar.getText().toString().equals("")) {
//                    userList.clear();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        User user = snapshot.getValue(User.class);
//
//                        userList.add(user);
//
//                    }
//
//                    userAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }
}
