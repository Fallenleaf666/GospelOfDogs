package com.leaf.godproject.nouse;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.leaf.godproject.R;

import java.util.HashMap;
import java.util.Map;

public class PetdiaryEditDiary extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mFirebaseAuth;
    TextView title,content,random,show;
    Button uploadbt;
    ProgressBar loadingProgressBar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_petdiary);

        mFirebaseAuth= FirebaseAuth.getInstance();
        initView();

    }

    private void initView() {

//        title=(TextView)findViewById(R.id.DNAME);
//        content=(TextView)findViewById(R.id.DBLOOD);
//        random=(TextView)findViewById(R.id.DIMG);
//        show=(TextView)findViewById(R.id.show);
        uploadbt = findViewById(R.id.ADDDOG);
        loadingProgressBar = findViewById(R.id.loading3);

        uploadbt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==uploadbt){
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            loadingProgressBar.setVisibility(View.VISIBLE);
            Map<String,Object> diary=new HashMap<>();
            diary.put("title",title.getText().toString());
            diary.put("content",content.getText().toString());
            diary.put("random",random.getText().toString());
            db.collection("Discusses").document("Discuss")
                    .collection("responses").document(title.getText().toString())
                    .set(diary)
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(PetdiaryEditDiary.this, "新增成功", Toast.LENGTH_SHORT).show();
                            loadingProgressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PetdiaryEditDiary.this, "新增失敗", Toast.LENGTH_SHORT).show();
                            loadingProgressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
