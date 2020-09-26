package com.leaf.godproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class OptionsActivity extends AppCompatActivity {

    TextView logout,setbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("隱私");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        logout = findViewById(R.id.logout);
        setbt=findViewById(R.id.SETBT);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            logout.setText("登入");
        }

        setbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this, SettingActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(logout.getText().toString().equals("登出")){


                    FirebaseAuth.getInstance().signOut();
//                    startActivity(new Intent(OptionsActivity.this, LoginActivity.class)
//                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    startActivity(new Intent(OptionsActivity.this, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                    finish();
                }
                else{
//                    startActivity(new Intent(OptionsActivity.this, LoginActivity.class)
//                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    startActivity(new Intent(OptionsActivity.this, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                    finish();
                }
//                startActivity(new Intent(OptionsActivity.this, StartActivity.class)

            }
        });
    }

}
