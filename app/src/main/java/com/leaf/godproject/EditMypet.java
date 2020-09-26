package com.leaf.godproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.leaf.godproject.Model.Dog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditMypet extends AppCompatActivity implements View.OnClickListener{

    private Toolbar mToolbar;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    CircleImageView image_dogprofile;
    TextView dogname,dogblood,dogbirth,dogtype,doggender,dogmixtype;
    TextView adddogbt,title;
    String dogid;
    boolean imgchange;

    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpet2);

        mFirebaseAuth= FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        imgchange=false;

        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        Intent intent = getIntent();
        dogid = intent.getStringExtra("DOG_ID");
//        Toast.makeText(EditMypet.this, "上傳成功"+dogid, Toast.LENGTH_SHORT).show();
        initView();
        getdoginfo();

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference users = db.collection("Users").document(firebaseUser.getUid());


    }

    private void getdoginfo() {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference users = db.collection("Dogs").document(firebaseUser.getUid())
                .collection("AllDogs").document(dogid);

        users.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists()){
                        Dog dog = documentSnapshot.toObject(Dog.class);

                        Glide.with(EditMypet.this).load(dog.getImgurl()).into(image_dogprofile);
                        dogname.setText(dog.getName());
                        dogtype.setText(dog.getType());
                        dogblood.setText(dog.getBlood());
                        dogbirth.setText(dog.getBirth());
                        dogmixtype.setText(dog.getMixtype());
                        doggender.setText(dog.getGender());
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if(v==adddogbt){
            uploadImage(dogid);
        }
    }

    private void initView() {
        dogname=(TextView)findViewById(R.id.dognametext);
        dogblood=(TextView)findViewById(R.id.set_dogblood);
        image_dogprofile=findViewById(R.id.dogimg);
        dogbirth=(TextView)findViewById(R.id.set_dogbirth);
        dogtype=(TextView)findViewById(R.id.set_dogtype);
        dogmixtype=(TextView)findViewById(R.id.set_dogmixtype);
        doggender=(TextView)findViewById(R.id.set_doggender);
        adddogbt = (TextView)findViewById(R.id.adddogbt);
        title=(TextView)findViewById(R.id.username);

        adddogbt.setOnClickListener(this);
        adddogbt.setText("更新");
        title.setText("更新寵物相簿");

        SetActbar();

    }

    private void uploadImage(final String dogid){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("上傳中");
        pd.show();
        if(!imgchange){
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            CollectionReference reference = db.collection("Dogs").document(firebaseUser.getUid())
                    .collection("AllDogs");

            Map<String,Object> dog=new HashMap<>();
            dog.put("name",dogname.getText().toString());
            dog.put("blood",dogblood.getText().toString());
            dog.put("birth",dogbirth.getText().toString());
            dog.put("gender",doggender.getText().toString());
            dog.put("type",dogtype.getText().toString());
            dog.put("mixtype",dogmixtype.getText().toString());

            reference.document(dogid).update(dog).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(EditMypet.this, "編輯成功", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    finish();
                }
            });
        }
        else if (imgchange&&mImageUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();

//                        long timeStampSec = System.currentTimeMillis()/1000;
//                        final String timestamp = String.format("%010d", timeStampSec);

                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        CollectionReference reference = db.collection("Dogs").document(firebaseUser.getUid())
                                .collection("AllDogs");

                        Map<String,Object> dog=new HashMap<>();
                        dog.put("name",dogname.getText().toString());
                        dog.put("blood",dogblood.getText().toString());
                        if (imgchange){ dog.put("imgurl",miUrlOk); }
                        dog.put("birth",dogbirth.getText().toString());
                        dog.put("gender",doggender.getText().toString());
                        dog.put("type",dogtype.getText().toString());
                        dog.put("mixtype",dogmixtype.getText().toString());

                        reference.document(dogid).update(dog).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(EditMypet.this, "編輯成功", Toast.LENGTH_SHORT).show();
                            }
                        });


                        pd.dismiss();
                        finish();

                    } else {
                        Toast.makeText(EditMypet.this, "失敗", Toast.LENGTH_SHORT).show();
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditMypet.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(EditMypet.this, "尚未選取圖片", Toast.LENGTH_SHORT).show();
        }
    }


    public void selectimg(View v){
        CropImage.activity()
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(EditMypet.this);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            Glide.with(getApplicationContext()).load(mImageUri).into(image_dogprofile);
            imgchange=true;
//            uploadImage();

        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CANCELED) {
        }

        else {
            Toast.makeText(this, "發生了一些錯誤!", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void selectgender(View v){
        final String[] items = new String[2];
        items[0] = "公";
        items[1] = "母";
        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("性別");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                doggender.setText(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selecttype(View v){
        final String[] items = new String[5];
        items[0] = "小型犬";
        items[1] = "中型犬";
        items[2] = "大型犬";
        items[3] = "超大型犬";
        items[4] = "其他";
        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("選擇犬種");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item==4){dogtype.setText("其他");}
                else{selecttype2(items[item]); }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selecttype2(String type){
        Resources res =getResources();
        String[] types=new  String[30];
        if(type.equals("小型犬")){ types = res.getStringArray(R.array.dog_type_small); }
        else if(type.equals("中型犬")){ types = res.getStringArray(R.array.dog_type_mid); }
        else if(type.equals("大型犬")){ types = res.getStringArray(R.array.dog_type_big); }
        else if(type.equals("超大型犬")){ types = res.getStringArray(R.array.dog_type_sbig); }
        final String[] items = new String[types.length];
        for(int i=0;i<types.length;i++){ items[i] = types[i]; }
        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("選擇犬種");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
//                mAlarmtype = items[item];
//                mAlarmtypeText.setText(mAlarmtype);
                dogtype.setText(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectmixtype(View v){
        final String[] items = new String[6];
        items[0] = "小型犬";
        items[1] = "中型犬";
        items[2] = "大型犬";
        items[3] = "超大型犬";
        items[4] = "其他";
        items[5] = "無";
        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("選擇犬種");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item==4){dogmixtype.setText("其他");}
                else if(item==5){dogmixtype.setText("無");}
                else{selectmixtype2(items[item]); }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectmixtype2(String type){
        Resources res =getResources();
        String[] types=new  String[30];
        if(type.equals("小型犬")){ types = res.getStringArray(R.array.dog_type_small); }
        else if(type.equals("中型犬")){ types = res.getStringArray(R.array.dog_type_mid); }
        else if(type.equals("大型犬")){ types = res.getStringArray(R.array.dog_type_big); }
        else if(type.equals("超大型犬")){ types = res.getStringArray(R.array.dog_type_sbig); }
        final String[] items = new String[types.length];
        for(int i=0;i<types.length;i++){ items[i] = types[i]; }
        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("選擇犬種");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                dogmixtype.setText(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    public void selectbirth(View v){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(
                EditMypet.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = year + "/" + month + "/" + day ;
                        dogbirth.setText(date);
                    }
                },
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public void selectblood(View v){
        final String[] items = new String[2];
        items[0] = "DEA1.1(+)";
        items[1] = "DEA1.1(-)";
        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("血型");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
//                doggender = items[item];
//                dgender.setText(doggender);
                dogblood.setText(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void SetActbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
