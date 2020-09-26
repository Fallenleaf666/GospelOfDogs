package com.leaf.godproject.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.leaf.godproject.LoginActivity;
import com.leaf.godproject.R;


public class OptionsFragment extends Fragment {

    TextView logout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_options, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("隱私");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        logout = view.findViewById(R.id.logout);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            logout.setText("登入");
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(logout.getText().toString().equals("登出")){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getContext(), LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    getActivity().finish();
                }
                else{
                    startActivity(new Intent(getContext(), LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        });
        return view;
    }
}

