package com.leaf.godproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetpwActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mFirebaseAuth;
    EditText usernameEditText;
    Button submitButton;
    ProgressBar loadingProgressBar ;
    TextView canser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpw);

        mFirebaseAuth=FirebaseAuth.getInstance();
        initView();
    }

    private void initView() {

        usernameEditText = findViewById(R.id.eusername);
        submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(this);
        loadingProgressBar = findViewById(R.id.creatloading);
        canser = findViewById(R.id.canser);
        canser.setOnClickListener(this);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(!isUserNameValid(usernameEditText.getText().toString())){
                    usernameEditText.setError("信箱格式錯誤");
                }

                if(isUserNameValid(usernameEditText.getText().toString())){
                    submitButton.setEnabled(true);
                }else{submitButton.setEnabled(false);}
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);

    }


    @Override
    public void onClick(View v) {
        if(v==submitButton){
            loadingProgressBar.setVisibility(View.VISIBLE);
//            檢查USER中是否有該USER或者有其他METHOD
            Sendemail();
        }
        else if(v==canser){
            setResult(Activity.RESULT_CANCELED);
            finish(); }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) { return false; }
        else if (username.contains("@")) { return true; }
        else { return false; }
    }

    private void Sendemail() {
            FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(usernameEditText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetpwActivity.this, "信件已寄送，請檢查信箱中的信件", Toast.LENGTH_SHORT).show();
                                Log.i("email",usernameEditText.getText().toString());
                            } else {
//                                Toast.makeText(ForgetpwActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(ForgetpwActivity.this, "此信箱並未註冊", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }

//    private  void CreaUser(String email,String pw){
//        mFirebaseAuth.createUserWithEmailAndPassword(email,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                String message =task.isComplete() ? "註冊成功":"註冊失敗";
//                if(task.isSuccessful()){
//                    String id=task.getResult().getUser().getUid();
//                    HashMap<String, Object> hashMap = new HashMap<>();
//                    hashMap.put("id",id);
//                    hashMap.put("fullname","");
//                    hashMap.put("username","");
//                    hashMap.put("bio","");
//                    hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/key-utility-254506.appspot.com/o/uploads%2Fuserhead.png?alt=media&token=9ac54952-563e-442a-94d0-6b9e5ddc83a6");
//                    FirebaseFirestore.getInstance().collection("Users").document(id)
//                            .set(hashMap).
//                            addOnSuccessListener(new OnSuccessListener() {
//                                @Override
//                                public void onSuccess(Object o) {
//
//                                    Toast.makeText(EnrollActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//                Toast.makeText(getBaseContext(),message,Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });
//
//    }
}
