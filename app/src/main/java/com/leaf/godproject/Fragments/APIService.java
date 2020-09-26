package com.leaf.godproject.Fragments;



import com.leaf.godproject.Notification.MyResponse;
import com.leaf.godproject.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAXXpILlo:APA91bHKkEtLeQdMYyOaGs9scugO67gdebiIaTGyXJbN9S8Q4p4RhpAHUwqnIjOmrhWhUUrpNJV0Mtz0Ug8O00SnUA5zCt4LZQx3DIcxbHgw9RvVY6Ss__ADQeQ3VWAmU7c_7W_JicnL"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}