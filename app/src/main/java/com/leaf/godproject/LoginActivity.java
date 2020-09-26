package com.leaf.godproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    public static final String TAG = "LoginActivity";
    TextView canser;
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton,erollButton,forget;
    ProgressBar loadingProgressBar ;
    GoogleSignInClient mGoogleSignInClient;
    private SignInButton signIn;
    public static final int RC_SIGN_IN = 0;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth= FirebaseAuth.getInstance();

//        接受提醒訊息
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            // Create channel to show notifications.
            String channelId  = "default_notification_channel_id";
            String channelName = "default_notification_channel_name";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_MAX));
//                    channelName, NotificationManager.IMPORTANCE_LOW));
        }







//        mFirebaseAuthListener=new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user=firebaseAuth.getCurrentUser();
//                if(user!=null){ Log.d("登入狀態改變","登入"+user.getUid()); }
//                else{ Log.d("登入狀態改變","已登出"); }
//            }
//        };

        if(mFirebaseAuth.getCurrentUser()!=null){
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
//            Toast.makeText(getApplicationContext(),"歡迎回來", Toast.LENGTH_LONG).show();
            finish();
        }
        initView();

        boolean remember =getSharedPreferences(
                "logindata",MODE_PRIVATE).getBoolean("REMEMBER_USEREMAIL",false);
        if(remember){
            usernameEditText.setText(getSharedPreferences("logindata",MODE_PRIVATE).
                    getString("USEREMAIL",""));
        }
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(!isUserNameValid(usernameEditText.getText().toString())){
                    usernameEditText.setError("信箱格式錯誤");
                }
                if(!isPasswordValid(passwordEditText.getText().toString())){
                    passwordEditText.setError("密碼必須大於6碼");
                }
                if(isUserNameValid(usernameEditText.getText().toString())&&
                        isPasswordValid(passwordEditText.getText().toString())){
                    loginButton.setEnabled(true);
                } else{loginButton.setEnabled(false);}
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

    }

    private void initView() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        erollButton = findViewById(R.id.eroll);
        forget= findViewById(R.id.forget);
        canser = findViewById(R.id.canser);
        loadingProgressBar = findViewById(R.id.loading);
        signIn = findViewById(R.id.sign_in_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        erollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, EnrollActivity.class);
                startActivity(intent);
//                setResult(Activity.RESULT_CANCELED);
            }
        });

        canser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ForgetpwActivity.class);
                startActivity(intent);
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
// Build a GoogleSignInClient with the options specified by gso.
                 mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                 signIn.setOnClickListener(new View.OnClickListener() {
             @Override
            public void onClick(View view) { signIn(); }
         });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                loadingProgressBar.setVisibility(View.VISIBLE);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                showLoginFailed();
                Log.w(TAG, "Google 登入失敗", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
                 Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
                 AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                         .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                                         if (task.isSuccessful()) {
                                                 // Sign in success, update UI with the signed-in user's information
                                                 Log.d(TAG, "signInWithCredential:success");
                                                 FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                                 checkUserdata(user.getUid());
//                                                 updateUI(user);
//                                             setResult(Activity.RESULT_OK);
//                                             Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                             startActivity(intent);
//                                             finish();
                                             } else {
                                                 // If sign in fails, display a message to the user.
                                                 Log.w(TAG, "signInWithCredential:failure", task.getException());
                                             loadingProgressBar.setVisibility(View.INVISIBLE);
                                                 Toast.makeText(LoginActivity.this,"無法登入GOOGLE",Toast.LENGTH_LONG).show();
                                                 updateUI(null);
                                             }
                                     }
                 });
             }
    private void updateUI(FirebaseUser user) {
                 GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                 if (acct != null) {
                         String personName = acct.getDisplayName();
                         String personId = acct.getId();
//                         Toast.makeText(this, "Name of the user :" + personName + " user id is : " + personId, Toast.LENGTH_SHORT).show();
                     }
             }


    private void updateUiWithUser() {
//        String welcome = getString(R.string.welcome)  ;
        String welcome = "登入成功";
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed() { Toast.makeText(getBaseContext(),R.string.login_failed,Toast.LENGTH_SHORT).show(); }

    public void login(String username,String password) {
        // TODO: revoke authentication
        mFirebaseAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            updateUiWithUser();
                            FirebaseUser user=mFirebaseAuth.getCurrentUser();
                            if(user!=null){
                                boolean remember =getSharedPreferences(
                                        "logindata",MODE_PRIVATE).getBoolean("REMEMBER_USEREMAIL",false);
                                boolean emailchange=getSharedPreferences("logindata",MODE_PRIVATE).
                                        getString("USEREMAIL","").equals(user.getEmail());
                                if(!remember||!emailchange){
                                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("記住信箱").
                                        setMessage(R.string.rememberemailpw).
                                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FirebaseUser user=mFirebaseAuth.getCurrentUser();
                                                getSharedPreferences("logindata",MODE_PRIVATE).edit().putString("USEREMAIL",user.getEmail()).apply();
                                                getSharedPreferences("logindata",MODE_PRIVATE).edit().putBoolean("REMEMBER_USEREMAIL",true).apply();
                                                setResult(Activity.RESULT_OK);
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).
                                        setNegativeButton("NO",new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                getSharedPreferences("logindata",MODE_PRIVATE).edit().putString("USEREMAIL","").apply();
                                                getSharedPreferences("logindata",MODE_PRIVATE).edit().putBoolean("REMEMBER_USEREMAIL",false).apply();
                                                setResult(Activity.RESULT_OK);
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).show();
                                }
                                else{
                                    setResult(Activity.RESULT_OK);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
//                            setResult(Activity.RESULT_OK);
//                            finish();
                        }
                        else if(!task.isSuccessful()){ showLoginFailed(); }
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private boolean isUserNameValid(String username) {
        if (username == null) { return false; }
        else return username.contains("@");
    }
    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }


    private  void CreatUserdata(String id){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id",id);
                    hashMap.put("fullname","");
                    hashMap.put("username","");
                    hashMap.put("bio","");
                    hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/key-utility-254506.appspot.com/o/uploads%2Fuserhead.png?alt=media&token=9ac54952-563e-442a-94d0-6b9e5ddc83a6");
                    FirebaseFirestore.getInstance().collection("Users").document(id)
                            .set(hashMap).
                            addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    setResult(Activity.RESULT_OK);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });


    }


    private void  checkUserdata(final String id){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference users = db.collection("Users").document(id);
        users.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot != null && documentSnapshot.exists()){
                        setResult(Activity.RESULT_OK);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        CreatUserdata(id);
                    }
                }
            }
        });
    }


}
