package com.leaf.godproject.nouse;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leaf.godproject.R;

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

public class PetdiaryAddMypet extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mFirebaseAuth;
    ImageButton dimg;
    TextView dname,dblood,dbirth,dtype,dgender,dmixtype;
    Button adddogbt;
    ProgressBar loadingProgressBar ;
    private  byte[] imgbyte;
    private FirebaseUser user;
    private final static int SELECT_PHOTO = 12345;
    private final static int TAKE_PHOTO = 879;
    private final static int CUT_OK = 465;


    private final static int REQUEST_EXTERNAL_STORAGE=222;
    private final static int REQUEST_CAMERA_STORAGE=555;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private File tempFile,tempFile2;
    private Uri imageUri,imageUri2;
    private ImageDeal mImageDeal;


    private String filename; //圖片名稱
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_addpet);
        setContentView(R.layout.activity_addpet2);




        String path = Environment.getExternalStorageDirectory().getPath();
        tempFile = new File(path, "/temp.jpg");
        tempFile2 = new File(path, "/temp2.jpg");
        imageUri = Uri.fromFile(tempFile);
        imageUri2 = Uri.fromFile(tempFile);
        mImageDeal=new ImageDeal();


        imgbyte=new byte[200];
        mFirebaseAuth= FirebaseAuth.getInstance();
        initView();

    }

    private void initView() {
        dname=(TextView)findViewById(R.id.dognametext);
        dblood=(TextView)findViewById(R.id.set_dogblood);
        dimg=(ImageButton)findViewById(R.id.dogimg);
        dbirth=(TextView)findViewById(R.id.set_dogbirth);
        dtype=(TextView)findViewById(R.id.set_dogtype);
        dmixtype=(TextView)findViewById(R.id.set_dogmixtype);
        dgender=(TextView)findViewById(R.id.set_doggender);
        adddogbt = findViewById(R.id.ADDDOG);
        loadingProgressBar = findViewById(R.id.loading3);

        adddogbt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==adddogbt){
            mFirebaseAuth= FirebaseAuth.getInstance();
            user=mFirebaseAuth.getCurrentUser();
            if(user!=null){
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                CollectionReference mydogs = db
                        .collection("Users").document(user.getUid()).
                                collection("MYDOGS");


                loadingProgressBar.setVisibility(View.VISIBLE);

                Map<String,Object> dog=new HashMap<>();

                long timeStampSec = System.currentTimeMillis()/1000;
                final String timestamp = String.format("%010d", timeStampSec);

                dog.put("dID",timestamp);
                dog.put("dNAME",dname.getText().toString());
                dog.put("dBLOOD",dblood.getText().toString());
//                dog.put("dIMG",dimg.getText().toString());
                dog.put("dBIRTH",dbirth.getText().toString());
                dog.put("dGENDER",dgender.getText().toString());
                dog.put("dTYPE",dtype.getText().toString());
                dog.put("dMIXTYPE",dmixtype.getText().toString());

                db.collection("Users").document(user.getUid())
                        .collection("MYDOGS").document(timestamp)
                        .set(dog)
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(PetdiaryAddMypet.this, "狗狗資訊新增成功", Toast.LENGTH_SHORT).show();
//                                回傳成功
                                Uploadimg(timestamp);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PetdiaryAddMypet.this, "狗狗資訊新增失敗", Toast.LENGTH_SHORT).show();
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

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
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
//                選照片
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
//                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                        photoPickerIntent.setType("image/*");
//                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
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

//                            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, tempFile);
//                            startActivityForResult(intentCamera,TAKE_PHOTO);
                        }
                    }



//
//                    Intent intent = new Intent();
//                    // 開啟Pictures畫面Type設定為image
//                    intent.setType("image/*");
//                    // 使用Intent.ACTION_GET_CONTENT這個Action
//                    // 會開啟選取圖檔視窗讓您選取手機內圖檔
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    intent.setType("image/*");
//                    intent.putExtra("crop", "true");// crop=true 有這句才能叫出裁剪頁面.
//                    intent.putExtra("aspectX", 1);// 这兩項為裁剪框的比例.
//                    intent.putExtra("aspectY", 1);// x:y=1:1
//                    intent.putExtra("output", Uri.fromFile(tempFile));
//                    intent.putExtra("outputFormat", "JPEG");//返回格式
//
//                    // 取得相片後返回本畫面
//                    startActivityForResult(Intent.createChooser(intent, "選擇圖片"),TAKE_PHOTO);

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
                PetdiaryAddMypet.this,
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
        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.

//        相簿選好照片後
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            Bitmap photo = data.getParcelableExtra("data");
            if(photo!=null)
            {
//                dimg = ImageDeal.toRoundBitmap(photo);//裁剪成圆形
                dimg.setImageBitmap(ImageDeal.toRoundBitmap(photo));
            }

//            // Let's read picked image data - its URI
//            Uri pickedImage = data.getData();
//            // Let's read picked image path using content resolver
//            String[] filePath = { MediaStore.Images.Media.DATA };
//            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
//            cursor.moveToFirst();
//            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//
////            options.inJustDecodeBounds = false;
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
//            dimg.setImageBitmap(bitmap);
////            dimg.setImageURI(pickedImage);
////            imageView.setImageBitmap(bitmap);
//
//            // Do something with the bitmap
//            // At the end remember to close the cursor or you will end with the RuntimeException!
//            cursor.close();
        }
//拍完照片後
        else if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK ) {
            try{Intent intent = new Intent("com.android.camera.action.CROP"); //剪裁
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("scale", true);
                intent.putExtra("circleCrop", "true");
                //设置宽高比例
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                //设置裁剪图片宽高
                intent.putExtra("outputX", 450);
                intent.putExtra("outputY", 450);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri2);
                Toast.makeText(PetdiaryAddMypet.this, "剪裁图片", Toast.LENGTH_SHORT).show();
                //广播刷新相册
                Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intentBc.setData(imageUri2);
                getApplication().sendBroadcast(intentBc);
                startActivityForResult(intent, CUT_OK); //设置裁剪参数显示图片至ImageView
            } catch (Exception e) {
                e.printStackTrace();
            }
//            new CorpTask().execute();
//            Uri takeImage = data.getData();
//            Bitmap bitmap = BitmapFactory.decodeStream(
//                    getContentResolver().openInputStream(imageUri));
//            dimg.setImageBitmap(bitmap);

//            Intent intent = new Intent("com.android.camera.action.CROP");
//            intent.setDataAndType(takeImage, "image/*");
//            // 下面这个crop = true是设置在开启的Intent中设置显示的VIEW可裁剪
//            intent.putExtra("crop", "true");
//            intent.putExtra("circleCrop", "true");
//            // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            // outputX outputY 是裁剪图片宽高
//            intent.putExtra("outputX", 200);
//            intent.putExtra("outputY", 200);
//            intent.putExtra("return-data", false);//设置之后 onActivityResult 中data 就为null了
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, takeImage);//设置裁剪之后的图片保存位置
//            intent = Intent.createChooser(intent, "裁剪圖片");
//            startActivityForResult(intent, CUT_OK);
        }
        else if (requestCode == CUT_OK && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(imageUri2));
//                FileInputStream is = new FileInputStream(outputImage);
//                Bitmap bitmap = BitmapFactory.decodeStream(is);
//                dimg.setImageBitmap(bitmap);
                dimg.setImageBitmap(ImageDeal.toRoundBitmap(bitmap));

            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(),"裁剪失敗", Toast.LENGTH_LONG).show();
        }
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
                }else{Toast.makeText(getApplicationContext(),"手機權限未開啟無法存取圖片", Toast.LENGTH_LONG).show();}
            case(REQUEST_CAMERA_STORAGE):
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, tempFile);
//                    startActivityForResult(intentCamera,TAKE_PHOTO);
                }else{Toast.makeText(getApplicationContext(),"手機權限未開啟無法啟用相機", Toast.LENGTH_LONG).show();}
        }
    }



    private class CorpTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... Void) {
            try{Intent intent = new Intent("com.android.camera.action.CROP"); //剪裁
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("scale", true);
                //设置宽高比例
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                //设置裁剪图片宽高
                intent.putExtra("outputX", 450);
                intent.putExtra("outputY", 450);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                Toast.makeText(PetdiaryAddMypet.this, "剪裁图片", Toast.LENGTH_SHORT).show();
                //广播刷新相册
                Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intentBc.setData(imageUri);
                getApplication().sendBroadcast(intentBc);
                startActivityForResult(intent, CUT_OK); //设置裁剪参数显示图片至ImageView
            } catch (Exception e) {
                e.printStackTrace();
            }return null;
    }
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

}
