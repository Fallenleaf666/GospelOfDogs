package com.leaf.godproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.leaf.godproject.Model.Dog;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    public  static final int FUNC_ADDPET=55688;

    private Uri mImageUri;
    String miUrlOk = "";
    private StorageTask uploadTask;
    StorageReference storageRef;

    ImageView close, image_added;
    TextView post,petphoto;
    EditText description;

    String selectpetid="";

    private List<Dog> dogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initview();
//圖片擺放位置
        storageRef = FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage_10();
            }
        });


        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);
    }

    private void initview() {
        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        petphoto = findViewById(R.id.selectitem);


        myPets();
    }

    private void myPets() {
        dogList= new ArrayList<>();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Dogs").
                document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("AllDogs");
        reference.orderBy("creattime").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
            dogList.clear();
            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                Dog dog =  documentSnapshot.toObject(Dog.class);
            dogList.add(dog);
            }
//                Collections.reverse(dogList);
                petphoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String[] items = new String[dogList.size()+2];
//                        Log.d("YourTag", "監聽內items列表長度"+items.length);
                        items[0] = "預設";
                        for (int i=1;i<items.length-1;i++){
                            if(dogList.get(i-1).getName().equals("")){
                                items[i] = "未命名";
                            }
                            else{items[i] = dogList.get(i-1).getName();}
                            }
                        items[items.length-1] = "新增寵物相簿";
                        Log.d("新增寵物", "items列表長度"+items.length);
                        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                        builder.setTitle("選擇相簿");
                        Log.d("選擇相簿", "items列表長度"+items.length);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if(item==items.length-1){
                                    Intent intent = new Intent(PostActivity.this, AddMypet.class);
                                    startActivityForResult(intent,FUNC_ADDPET);
                                }
                                else if(item==0){
                                    petphoto.setText(items[item]);
                                    selectpetid="";
                                }
                                else if(item>0&&item<items.length-1){
                                    petphoto.setText(items[item]);
                                selectpetid=dogList.get(item-1).getId();
                                }
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
            }
        }
        );
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage_10(){
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
                        miUrlOk = downloadUri.toString();

                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        CollectionReference mydogs = db
                                .collection("Posts");
                        String postid=db.collection("Posts").document().getId();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", miUrlOk);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("creattime", System.currentTimeMillis());
                        hashMap.put("petid",selectpetid);
//                                    Toast.makeText(getApplicationContext(),"上傳成功", Toast.LENGTH_LONG).show();
                        db.collection("Posts").document(postid).set(hashMap)
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(PostActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PostActivity.this, "上傳失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });


                        pd.dismiss();

                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                        finish();

                    } else {
                        Toast.makeText(PostActivity.this, "上傳失敗", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(PostActivity.this, "尚未選取圖片", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            image_added.setImageURI(mImageUri);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CANCELED) {
//            Toast.makeText(this, "已取消選取!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
        else if(requestCode==FUNC_ADDPET&& resultCode == RESULT_OK)
        {
            myPets();
        }
        else {
            Toast.makeText(this, "程序發生一些錯誤，請重新再來!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }
}
