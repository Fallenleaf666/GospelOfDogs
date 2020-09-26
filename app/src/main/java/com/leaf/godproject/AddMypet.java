package com.leaf.godproject;

import android.app.Activity;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddMypet extends AppCompatActivity implements View.OnClickListener{

    private Toolbar mToolbar;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    CircleImageView dimg;
    TextView dname,dblood,dbirth,dtype,dgender,dmixtype;
    TextView adddogbt;
    boolean imgchange;

    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpet2);

        mFirebaseAuth= FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        imgchange=false;

        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        initView();

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference users = db.collection("Users").document(firebaseUser.getUid());


    }


    @Override
    public void onClick(View v) {
        if(v==adddogbt){
            String dogid=FirebaseFirestore.getInstance().collection("Dogs").document(firebaseUser.getUid())
                    .collection("AllDogs").document().getId();
            if(imgchange){uploadImage(dogid);}
            else{
                onlyuploaddog(dogid);
            }
        }
    }

    private void onlyuploaddog(String dogid) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("上傳中");
        pd.show();

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Dogs").document(firebaseUser.getUid())
                .collection("AllDogs");

        Map<String,Object> dog=new HashMap<>();
        dog.put("id",dogid);
        dog.put("master",firebaseUser.getUid().toString());
        dog.put("name",dname.getText().toString());
        dog.put("blood",dblood.getText().toString());
//        dog.put("imgurl","https://firebasestorage.googleapis.com/v0/b/key-utility-254506.appspot.com/o/uploads%2Fdog_head.png?alt=media&token=4cba1b2f-e304-4819-b6a9-22da45c344b5");
        dog.put("imgurl","https://firebasestorage.googleapis.com/v0/b/key-utility-254506.appspot.com/o/defalt%2Fdoghead.png?alt=media&token=8728745a-4d7d-4609-a155-0b91f2846c73");
        dog.put("birth",dbirth.getText().toString());
        dog.put("gender",dgender.getText().toString());
        dog.put("type",dtype.getText().toString());
        dog.put("mixtype",dmixtype.getText().toString());
        dog.put("creattime",System.currentTimeMillis());

        reference.document(dogid).set(dog).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(AddMypet.this, "新增成功", Toast.LENGTH_SHORT).show();
            }
        });

        pd.dismiss();

        setResult(Activity.RESULT_OK);
        finish();

    }

    private void initView() {
        dname=(TextView)findViewById(R.id.dognametext);
        dblood=(TextView)findViewById(R.id.set_dogblood);
        dimg=findViewById(R.id.dogimg);
        dbirth=(TextView)findViewById(R.id.set_dogbirth);
        dtype=(TextView)findViewById(R.id.set_dogtype);
        dmixtype=(TextView)findViewById(R.id.set_dogmixtype);
        dgender=(TextView)findViewById(R.id.set_doggender);
        adddogbt = (TextView)findViewById(R.id.adddogbt);

        adddogbt.setOnClickListener(this);
        adddogbt.setText("新增");

        SetActbar();
    }

    private void uploadImage(final String dogid){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("上傳中");
        pd.show();
        if (mImageUri != null){
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
                        dog.put("id",dogid);
                        dog.put("master",firebaseUser.getUid().toString());
                        dog.put("name",dname.getText().toString());
                        dog.put("blood",dblood.getText().toString());
                        if(miUrlOk!=null){
                        dog.put("imgurl",miUrlOk);
                        }else {
                        dog.put("imgurl","https://firebasestorage.googleapis.com/v0/b/key-utility-254506.appspot.com/o/uploads%2Fdog_head.png?alt=media&token=4cba1b2f-e304-4819-b6a9-22da45c344b5");
                        }
                        dog.put("birth",dbirth.getText().toString());
                        dog.put("gender",dgender.getText().toString());
                        dog.put("type",dtype.getText().toString());
                        dog.put("mixtype",dmixtype.getText().toString());
                        dog.put("creattime",System.currentTimeMillis());

                        reference.document(dogid).set(dog).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(AddMypet.this, "新增成功", Toast.LENGTH_SHORT).show();
                            }
                        });


                        pd.dismiss();
//                        回傳dogid,dogname
                        setResult(Activity.RESULT_OK);
                        finish();

                    } else {
                        Toast.makeText(AddMypet.this, "失敗", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddMypet.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(AddMypet.this, "尚未選取圖片", Toast.LENGTH_SHORT).show();
        }
    }


    public void selectimg(View v){
        CropImage.activity()
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(AddMypet.this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            Glide.with(getApplicationContext()).load(mImageUri).into(dimg);
            imgchange=true;
//            uploadImage();
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CANCELED) {
        }
        else {
            Toast.makeText(this, "發生了一點錯誤!", Toast.LENGTH_SHORT).show();
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
                dgender.setText(items[item]);
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
                if(item==4){dtype.setText("其他");}
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
                dtype.setText(items[item]);
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
                if(item==4){dmixtype.setText("其他"); }
                else if(item==5){dmixtype.setText("無");}
                else {selectmixtype2(items[item]); }
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
                dmixtype.setText(items[item]);
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
                AddMypet.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = year + "/" + month + "/" + day ;
                        dbirth.setText(date);
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
                dblood.setText(items[item]);
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
