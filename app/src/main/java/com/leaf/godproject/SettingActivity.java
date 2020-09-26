package com.leaf.godproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {

    TextView logout;
    FirebaseAuth firebaseAuth;
    private TextView mnotystattext;
    private boolean mnotystat;
    private TextView set_email;
    SharedPreferences.Editor editor ;
    SharedPreferences prefs;
    Switch mnotystat_switch;
    RelativeLayout repass;
    String email;
    boolean haveuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("設定");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mnotystattext = (TextView) findViewById(R.id.set_notystat);
        mnotystat_switch=(Switch)findViewById(R.id.notystat_switch);
        set_email=(TextView) findViewById(R.id.set_email);
        repass=(RelativeLayout) findViewById(R.id.password);

        logout = findViewById(R.id.logout);

        firebaseAuth=FirebaseAuth.getInstance();
        haveuser=(FirebaseAuth.getInstance().getCurrentUser())!=null;
        email=firebaseAuth.getCurrentUser().getEmail();
        if(email!=null){
        set_email.setText(firebaseAuth.getCurrentUser().getEmail());
        }else{
            set_email.setText("查無信箱");
        }
        prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
        try {
        mnotystat = prefs.getBoolean("notystat", true);
        }catch (Exception e){}

        if(mnotystat){
            mnotystattext.setText("開啟");
            mnotystat_switch.setChecked(true);
        }
        else{
            mnotystattext.setText("關閉");
            mnotystat_switch.setChecked(false);
        }

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            logout.setText("登入");
        }

        repass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("更改密碼").
                        setMessage("是否確定要更改現在的密碼\n系統將會寄送更改密碼的信件").
                        setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "已送出，請檢察信箱是否有新的郵件", Toast.LENGTH_SHORT).show();
                                Sendemail();
                            }
                        }).
                        setNegativeButton("否",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        });

    }


    public void onSwitchNotystat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mnotystat = true;
            mnotystattext.setText("開啟" );
            prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
            editor = prefs.edit().putBoolean("notystat", mnotystat);
            editor.apply();
            Toast.makeText(this, "推播提醒開啟", Toast.LENGTH_SHORT).show();
        } else {
            mnotystat = false;
            mnotystattext.setText("關閉");
            prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
            editor = prefs.edit().putBoolean("notystat", mnotystat);
            editor.apply();
            Toast.makeText(this, "推播提醒關閉", Toast.LENGTH_SHORT).show();
        }
    }

    private void Sendemail() {
        if (haveuser) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(SettingActivity.this, "An email has been sent to you.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
  }
    }
}
