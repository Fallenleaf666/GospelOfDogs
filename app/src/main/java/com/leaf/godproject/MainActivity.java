package com.leaf.godproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.leaf.godproject.Fragments.*;
import com.leaf.godproject.Notification.Token;
//test2
//test4
//test5
//test8
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView email,uid;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    Button logoutButton;
    ProgressBar loadingProgressBar ;
    public  static final int FUNC_LOGIN=1;
    private FirebaseUser user;
    private GoogleSignInAccount acct;

    private long firstTime=0;

    FirebaseUser firebaseUser;

    Fragment selectedfragment = null;
    BottomNavigationView bottom_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

//        AsyncTask1 x= new AsyncTask1();
//        x.execute();

        initView();

//        bottom_navigation = findViewById(R.id.bottom_navigation);
//        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        mFirebaseAuth= FirebaseAuth.getInstance();
        user=mFirebaseAuth.getCurrentUser();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {

                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        if( task.getResult() == null)
                            return;
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        // Log and toast
                        Log.i("MainActivity","token "+token);

//        update token
                        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        if (firebaseUser != null){
                        final DocumentReference docRef = db.collection("Tokens").document(firebaseUser.getUid());
                        Token token1 = new Token(token);
                        docRef.set(token1);
                        }
                    }
                });

//        if(user==null){
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this, LoginActivity.class);
//            startActivityForResult(intent,FUNC_LOGIN);
//        }else{ Toast.makeText(getApplicationContext(),"歡迎回來", Toast.LENGTH_LONG).show();}

        updateUI();

        mFirebaseAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    Log.d("登入狀態改變","登入"+user.getUid());
//                    logoutButton.setText(R.string.logoutstring);
                }
                else{ Log.d("登入狀態改變","已登出");
//                    logoutButton.setText(R.string.loginstring);
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "log out", Toast.LENGTH_LONG).show();
//                    updateUI();
                }
            }
        };


        Bundle intent = getIntent().getExtras();
//        if (intent.getString("notype")!=null){
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    new PostDetailFragment()).commit();
//        }
//        還沒測試過
        if (intent != null && intent.getString("publisherid")!=null){
            String publisher = intent.getString("publisherid");

//            看不懂
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

//設定初始fragment
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                new HomeFragment()).commit();

    }

    private void initView() {
        loadingProgressBar = findViewById(R.id.loading2);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

    }


    @Override
    public void onClick(View v) {
//        if(v==logoutButton){
//        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FUNC_LOGIN)
        {
            if(resultCode==RESULT_OK){
//                updateUI();
            }else{
//                updateUI();
            }
        }
    }

    private void updateUI() {
        user=mFirebaseAuth.getCurrentUser();
        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
//        if (acct != null) {
//            String personName = acct.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();
//            email.setText(personEmail);
//            uid.setText(personId);
//        }
//        else if(user != null) {

    }

    @Override
    protected void onStart() {

        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }
    @Override
    protected void onStop() {
        if(mFirebaseAuthListener!=null){
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);}
        super.onStop();
    }
    @Override
    protected void onDestroy() {
//        mFirebaseAuth.signOut();
        super.onDestroy();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedfragment = new HomeFragment();
                            break;
                        case R.id.nav_heart:
                            selectedfragment = new NotificationFragment();
                            break;
                        case R.id.nav_add:
                            selectedfragment = null;
                            if(firebaseUser!=null){
                            startActivity(new Intent(MainActivity.this, PostActivity.class));
                            }else{
                                Toast.makeText(getApplicationContext(),"需先登入才能進行發布", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case R.id.nav_search:
//                            selectedfragment = new SearchFragment();
                            selectedfragment = new ToolboxFragment();
                            break;
                        case R.id.nav_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            String uid=FirebaseAuth.getInstance().getCurrentUser()!=null?FirebaseAuth.getInstance().getCurrentUser().getUid():null;
                            editor.putString("profileid", uid);
                            editor.apply();
                            selectedfragment = new ProfileFragment();
                            break;
                    }
                    if (selectedfragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedfragment).commit();
                    }

                    return true;
                }
            };

//按上一頁回到上個fragment
    @Override
    public void onBackPressed() {
        int count =getFragmentManager().getBackStackEntryCount();
        if(count==0){
            super.onBackPressed();
    }
        else{
            getFragmentManager().popBackStack();
        }
    }

//    private class AsyncTask1 extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//            Glide.get(getApplicationContext());
//            return null;
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime=System.currentTimeMillis();
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(secondTime-firstTime<2000){
                System.exit(0);
//                finish();
            }else{
                Toast.makeText(getApplicationContext(),"在按一次返回鍵退出", Toast.LENGTH_LONG).show();
                firstTime=System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
