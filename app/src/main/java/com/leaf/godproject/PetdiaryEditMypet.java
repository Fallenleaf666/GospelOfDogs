package com.leaf.godproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leaf.godproject.nouse.ImageDeal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PetdiaryEditMypet extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mFirebaseAuth;
    TextView title,content,random,show;
    Button uploadbt;
    ProgressBar loadingProgressBar ;
    ImageButton dimg;
    TextView dname,dblood,dbirth,dtype,dgender,dmixtype;
    Button updatedogbt;
    RelativeLayout rlview;
    private FirebaseUser user;
    private final static int REQUEST_EXTERNAL_STORAGE=444;
    private final static int REQUEST_CAMERA_STORAGE=558;
    private final static int SELECT_PHOTO = 23456;
    private final static int TAKE_PHOTO = 880;
    private final static int CUT_OK = 466;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    public static final String EXTRA_DOG_ID = "DOG_ID";
    private String mDOGID;
    private boolean imgischange;

    private File tempFile,tempFile2;
    private Uri imageUri,imageUri2;
    private ImageDeal mImageDeal;


    private String filename; //圖片名稱

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpet2);

        String path = Environment.getExternalStorageDirectory().getPath();
        tempFile = new File(path, "/temp.jpg");
        tempFile2 = new File(path, "/temp2.jpg");
        imageUri = Uri.fromFile(tempFile);
        imageUri2 = Uri.fromFile(tempFile);
        mImageDeal=new ImageDeal();

        imgischange=false;
        mDOGID = getIntent().getStringExtra(EXTRA_DOG_ID);
//        Toast.makeText(getApplicationContext(),"id"+mDOGID, Toast.LENGTH_LONG).show();
        mFirebaseAuth= FirebaseAuth.getInstance();
        initView();
        setView();

    }

    private void setView() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        mFirebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user!=null){
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            DocumentReference editdog = db
                    .collection("Users").document(user.getUid()).collection("MYDOGS").document(mDOGID);
            editdog.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        dname.setText(documentSnapshot.getString("dNAME"));
                                                        dblood.setText(documentSnapshot.getString("dBLOOD"));
                                                        dbirth.setText(documentSnapshot.getString("dBIRTH"));
                                                        dtype.setText(documentSnapshot.getString("dTYPE"));
                                                        dmixtype.setText(documentSnapshot.getString("dMIXTYPE"));
                                                        dgender.setText(documentSnapshot.getString("dGENDER"));
                                                        Setdogimg();
                                                    }
            });
        }else{ Toast.makeText(getApplicationContext(),"請確認登入狀態", Toast.LENGTH_LONG).show();}
    }


    private void Setdogimg() {
        mFirebaseAuth= FirebaseAuth.getInstance();
        user=mFirebaseAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://key-utility-254506.appspot.com");
        StorageReference mstorageRef = storage.getReference();
        final StorageReference dogheadRef = mstorageRef.child("Users/"+user.getUid()+"/"+mDOGID+"/doghead.jpg");
        if(dogheadRef == null){
            return;
        } else{
//            final long ONE_MEGABYTE = 1024 * 1024;
            final long ONE_MEGABYTE = 1024 * 1024*5;
            dogheadRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Glide.with(getBaseContext())
                            .load(bytes)
                            .into(dimg);
                    rlview.setVisibility(View.VISIBLE);
                    loadingProgressBar.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    loadingProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void initView() {
        dname=(TextView)findViewById(R.id.dognametext);
        dblood=(TextView)findViewById(R.id.set_dogblood);
        dimg=(ImageButton)findViewById(R.id.dogimg);
        dbirth=(TextView)findViewById(R.id.set_dogbirth);
        dtype=(TextView)findViewById(R.id.set_dogtype);
        dmixtype=(TextView)findViewById(R.id.set_dogmixtype);
        dgender=(TextView)findViewById(R.id.set_doggender);
        updatedogbt = findViewById(R.id.ADDDOG);
        loadingProgressBar = findViewById(R.id.loading3);
        updatedogbt.setText("更新資料");
        rlview=findViewById(R.id.RLVIEW);
        rlview.setVisibility(View.GONE);
        updatedogbt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==updatedogbt){
            mFirebaseAuth= FirebaseAuth.getInstance();
            user=mFirebaseAuth.getCurrentUser();
            if(user!=null){
                FirebaseFirestore db=FirebaseFirestore.getInstance();

                loadingProgressBar.setVisibility(View.VISIBLE);

                Map<String,Object> dog=new HashMap<>();
                dog.put("dNAME",dname.getText().toString());
                dog.put("dBLOOD",dblood.getText().toString());
                dog.put("dBIRTH",dbirth.getText().toString());
                dog.put("dGENDER",dgender.getText().toString());
                dog.put("dTYPE",dtype.getText().toString());
                dog.put("dMIXTYPE",dmixtype.getText().toString());
//                dog.put("dIMG",dimg.getText().toString());
                db.collection("Users").document(user.getUid())
                        .collection("MYDOGS").document(mDOGID)
                        .update(dog)
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(PetdiaryEditMypet.this, "更新成功", Toast.LENGTH_SHORT).show();
                                if(imgischange){
                                Uploadimg(mDOGID);
                                }else{finish();}
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PetdiaryEditMypet.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                loadingProgressBar.setVisibility(View.GONE);
                            }
                        });
            }else{ Toast.makeText(getApplicationContext(),"請確認登入狀態", Toast.LENGTH_LONG).show();
                loadingProgressBar.setVisibility(View.GONE);}

        }
    }


    public void Uploadimg(String id){
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://key-utility-254506.appspot.com");
        StorageReference mstorageRef = storage.getReference();
        StorageReference dogheadRef = mstorageRef.child("Users/"+user.getUid()+"/"+id+"/doghead.jpg");
        dimg.setDrawingCacheEnabled(true);
        dimg.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) dimg.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = dogheadRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(),"圖片上傳失敗", Toast.LENGTH_LONG).show();
//                TODO 刪除剛剛建立的狗資料
                loadingProgressBar.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"圖片上傳成功", Toast.LENGTH_LONG).show();
//                loadingProgressBar.setVisibility(View.GONE);
                finish();
            }
        });
    }


    public void selectimg(View v){
        final String[] items = new String[2];
        items[0] = "拍照";
        items[1] = "圖庫中選擇照片";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("照片來源");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item==1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                                checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
                            return;
                        }
                        else{
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            photoPickerIntent.putExtra("return-data", true);
                            photoPickerIntent.putExtra("crop", "true");
                            photoPickerIntent.putExtra("circleCrop", "true");
                            //设置宽高比例
                            photoPickerIntent.putExtra("aspectX", 1);
                            photoPickerIntent.putExtra("aspectY", 1);
                            //设置裁剪图片宽高、
                            photoPickerIntent.putExtra("outputX", 450);
                            photoPickerIntent.putExtra("outputY", 450);
                            startActivityForResult(photoPickerIntent,SELECT_PHOTO);
                        }
                    }
                }
//                拍照
                else if(item==0){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                            requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA_STORAGE);
                            return;
                        }
                        else{
                            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                            Date date = new Date(System.currentTimeMillis());
                            filename = format.format(date);
                            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                            File outputImage = new File(path,filename+".jpg");
                            try {
                                if(outputImage.exists())
                                {
                                    outputImage.delete();
                                }
                                outputImage.createNewFile();
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                            //將File物件轉換為Uri並啟動照相程式
                            imageUri = Uri.fromFile(outputImage);
                            Intent intentCamera = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
                            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定圖片輸出地址

                            startActivityForResult(intentCamera,TAKE_PHOTO);
                        }
                    }
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
            public void onClick(DialogInterface dialog, int item) { selecttype2(items[item]); }
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
            public void onClick(DialogInterface dialog, int item) { selectmixtype2(items[item]); }
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
                PetdiaryEditMypet.this,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
                Bitmap photo = data.getParcelableExtra("data");
                if(photo!=null) { dimg.setImageBitmap(ImageDeal.toRoundBitmap(photo));
                    imgischange=true;}
        }
        else if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK ) {
            try{Intent intent = new Intent("com.android.camera.action.CROP"); //剪裁
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("scale", true);
                intent.putExtra("return-data", false);
                intent.putExtra("circleCrop", "true");
                //设置宽高比例
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                //设置裁剪图片宽高
                intent.putExtra("outputX", 450);
                intent.putExtra("outputY", 450);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri2);
                Toast.makeText(PetdiaryEditMypet.this, "剪裁图片", Toast.LENGTH_SHORT).show();
                //广播刷新相册
                Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intentBc.setData(imageUri2);
                getApplication().sendBroadcast(intentBc);
                startActivityForResult(intent, CUT_OK); //设置裁剪参数显示图片至ImageView
            } catch (Exception e) {
                e.printStackTrace();
            }
//            intent.putExtra("return-data", false);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, takeImage);

        }
        else if (requestCode == CUT_OK && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(imageUri2));
                dimg.setImageBitmap(ImageDeal.toRoundBitmap(bitmap));
                imgischange=true;
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }

        } else { Toast.makeText(getApplicationContext(),"裁剪失敗", Toast.LENGTH_LONG).show(); }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case(REQUEST_EXTERNAL_STORAGE):
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                } else{Toast.makeText(getApplicationContext(),"手機權限未開啟無法存取圖片", Toast.LENGTH_LONG).show();}
            case(REQUEST_CAMERA_STORAGE):
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = new Date(System.currentTimeMillis());
                    filename = format.format(date);
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File outputImage = new File(path,filename+".jpg");
                    try {
                        if(outputImage.exists()) { outputImage.delete(); }
                        outputImage.createNewFile();
                    } catch(IOException e) { e.printStackTrace(); }
                    //將File物件轉換為Uri並啟動照相程式
                    imageUri = Uri.fromFile(outputImage);
                    Intent intentCamera = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定圖片輸出地址

                    startActivityForResult(intentCamera,TAKE_PHOTO);
                }else{Toast.makeText(getApplicationContext(),"手機權限未開啟無法啟用相機", Toast.LENGTH_LONG).show();}

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        },500);

    }

}