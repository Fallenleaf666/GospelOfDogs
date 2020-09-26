package com.leaf.godproject.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.leaf.godproject.MainActivity;
import com.leaf.godproject.PostDetailActivity;
import com.leaf.godproject.ProfileActivity;

public class MyFirebaseService extends FirebaseMessagingService {
    Bitmap largeIcon;
    RemoteMessage rm;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        rm=remoteMessage;
//        Log.i("MyFirebaseService","開始接收訊息");

//        Toast.makeText(getApplicationContext(), "開始接收訊息", Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), ""+remoteMessage.getData().get("body"), Toast.LENGTH_SHORT).show();

//        if (remoteMessage.getNotification() != null) {
//            Log.i("MyFirebaseService","title "+remoteMessage.getNotification().getTitle());
//            Log.i("MyFirebaseService","body "+remoteMessage.getNotification().getBody());
//            sendNotification(remoteMessage.getNotification().getTitle(),
//                    remoteMessage.getNotification().getBody());
//        }
        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

//        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
//        String currentUser = preferences.getString("currentuser", "none");

        SharedPreferences prefs=getSharedPreferences("PREFS", MODE_PRIVATE);
        boolean opennoty = prefs.getBoolean("notystat", true);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && sented.equals(firebaseUser.getUid())&&opennoty){
//            if (!currentUser.equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    sendOreoNotification(remoteMessage);
//                    sendNotification2(remoteMessage);

                    sendNotification2();
//                    Toast.makeText(getApplicationContext(), "收到通訊", Toast.LENGTH_SHORT).show();
                } else {
//                    sendNotification2(remoteMessage);
                    sendNotification2();
//                    Toast.makeText(getApplicationContext(), "收到通訊", Toast.LENGTH_SHORT).show();
                }
//            }
        }
    }

    private void sendOreoNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);
        int i = 0;
        if (j > 0){
            i = j;
        }
        oreoNotification.getManager().notify(i, builder.build());
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String imgurl = remoteMessage.getData().get("imgurl");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        largeIcon=null;
        Glide.with(getApplicationContext()).asBitmap().load(imgurl).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        largeIcon =resource;
                    }
                });

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0){
            i = j;
        }

        noti.notify(i, builder.build());
    }

    private void sendNotification2() {
        if(rm.getData().get("ispost").equals("1"))
        {
            new ImgAsyncTask().execute();
        }
        else{
            new ImgAsyncTask().execute();
//            new NoimgAsyncTask().execute();
        }
    }

//    private void sendNotification(String messageTitle,String messageBody) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        String channelId = "default_notification_channel_id";
//        Uri defaultSoundUri =RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setContentTitle(messageTitle)
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
////                    NotificationManager.IMPORTANCE_DEFAULT);
//                    NotificationManager.IMPORTANCE_MAX);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0, notificationBuilder.build());
//    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i("MyFirebaseService","token "+s);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Log.i("MyFirebaseService","refreshToken "+s);
        if (firebaseUser != null){
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("Tokens").document(firebaseUser.getUid());
        Token token = new Token(refreshToken);
        docRef.set(token);
    }



    private class ImgAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... url) {
            String imgurl = rm.getData().get("imgurl");
                largeIcon=null;
            try {
                largeIcon=Glide.with(getApplicationContext()).asBitmap().load(imgurl).submit(360,480).get();
            }
            catch (Exception e){
//                Log.d("OKhttp回傳檔案","失敗");
            }
            return largeIcon;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            String user = rm.getData().get("user");
            String icon = rm.getData().get("icon");
            String title = rm.getData().get("title");
            String body = rm.getData().get("body");

            RemoteMessage.Notification notification = rm.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
//            Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);

            PendingIntent pendingIntent=null;

            if(rm.getData().get("ispost").equals("1"))
            {
                Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                TaskStackBuilder stackBuilder=TaskStackBuilder.create(getApplicationContext());
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(intent);

                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("postid", rm.getData().get("postid"));
                editor.apply();

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
                pendingIntent = stackBuilder.getPendingIntent(j, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            else{
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                TaskStackBuilder stackBuilder=TaskStackBuilder.create(getApplicationContext());
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(intent);

                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid",rm.getData().get("user"));
                editor.apply();

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = stackBuilder.getPendingIntent(j, PendingIntent.FLAG_UPDATE_CURRENT);
            }



//            Bundle bundle = new Bundle();
//            bundle.putString("userid", user);
//            bundle.putString("notype", "like");
//            intent.putExtras(bundle);


            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            largeIcon =bitmap;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(Integer.parseInt(icon))
                    .setLargeIcon(bitmap)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);
            NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            int i = 0;
            if (j > 0){
                i = j;
            }
            noti.notify(i, builder.build());

        }
    }

    private class NoimgAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... Void) {

            String user = rm.getData().get("user");
            String icon = rm.getData().get("icon");
            String title = rm.getData().get("title");
            String body = rm.getData().get("body");

            RemoteMessage.Notification notification = rm.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
//            Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
            Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
            TaskStackBuilder stackBuilder=TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(intent);

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//            editor.putString("postid", post.getPostid());
            editor.apply();

//            Bundle bundle = new Bundle();
//            bundle.putString("userid", user);
//            bundle.putString("notype", "like");
//            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(j, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(Integer.parseInt(icon))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);
            NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            int i = 0;
            if (j > 0){
                i = j;
            }
            noti.notify(i, builder.build());
            return null;
        }


    }
}