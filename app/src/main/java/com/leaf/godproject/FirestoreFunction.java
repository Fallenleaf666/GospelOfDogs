package com.leaf.godproject;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.leaf.godproject.Model.Post;

import java.util.HashMap;
import java.util.Map;

public class FirestoreFunction {

    static void  AddDocInCollection(){
        Map<String,Object> fo=new HashMap<>();
        FirebaseFirestore.getInstance().collection("Likes").document("")
                .collection("Likers").document("").set(fo).
                addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                    }
                });

    }


    static void  FindDocexistInCollection(){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Posts");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.isSuccessful() ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    Post post =  documentSnapshot.toObject(Post.class);
//                    for (String id : followingList){
//                        if (post.getPublisher().equals(id)){
//
//                        }
//                    }
                }
            }
        }
        );
    }


    static void  FindSingleDocexist(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference users = db.collection("Follow").document(firebaseUser.getUid()).
                collection("following").document("");

        users.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists()){
                    }
                    else{ }
                }
            }
        });
    }

    static String getFileExtension(Uri uri){
        Activity s=null;
        ContentResolver cR =s.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }





    static void ListenCollection(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Likes").
                document("Likes").collection("Likers");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.exists()){
//                        Message message = doc.toObject(Message.class);
//                        messageList.add(message);
//                        mAdapter.notifyDataSetChanged();
                    }
                }

                //                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
//                    switch (dc.getType()) {
//                        case ADDED:
//                            Log.d("TAG", "New Msg: " + dc.getDocument().toObject(Message.class));
//                            break;
//                        case MODIFIED:
//                            Log.d("TAG", "Modified Msg: " + dc.getDocument().toObject(Message.class));
//                            break;
//                        case REMOVED:
//                            Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(Message.class));
//                            break;
//                    }
//                }
                Log.d("YourTag", "messageList: " );
            }
        });
    }

    static void ListenCollection2(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Likes").
                document("Likes").collection("Likers");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>()  {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("YourTag", "Listen failed.", e);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.exists()){
//                        mAdapter.notifyDataSetChanged();
                    }
                }
                Log.d("YourTag", "messageList: " );
            }
        });

    }


    static void ListenDocument(){
//        FirebaseFirestore db=FirebaseFirestore.getInstance();
//        final DocumentReference docRef = db.collection("Follow").document(firebaseUser.getUid())
//                .collection("following").document(profileid);
//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if (documentSnapshot.exists()){
////                    edit_profile.setText("following");
//                } else{
////                    edit_profile.setText("follow");
//                }
//            }
//        });
    }


    static void storageimg(){
        Uri mImageUri=null;
        StorageReference storageRef=null;
        if (mImageUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            StorageTask uploadTask;
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
                        String miUrlOk = "";
                        miUrlOk = downloadUri.toString();

                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        CollectionReference mydogs = db
                                .collection("Posts");
                        String postid=db.collection("Posts").document().getId();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        db.collection("Posts").document(postid).set(hashMap)
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });


                    }
}
        });
        }
    }
}