package com.example.moodpredictor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class CurrentlyPlayingService {
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private String endpoint = "https://api.spotify.com/v1/me/player/currently-playing";

    private Handler handler;
    Runnable runnable;

    //private String token;

    public Song getCurrentSong() {
        return currentSong;
    }

    private Song currentSong;


    public CurrentlyPlayingService(Context context){
        sharedPreferences = context.getSharedPreferences("SPOTIFY",0);
        queue = Volley.newRequestQueue(context);
    }

    public void init(){
        handler = new Handler();
        runnable = () -> {
            handler.postDelayed(runnable, 3000);
            Log.d("William", "This is happening every 3 seconds");
            getCurrentlyPlayingTrack(() -> Log.d("William", "Foo"));
        };
        handler.postDelayed(runnable, 0);
    }

    /*public CurrentlyPlayingService() {
        handler = new Handler();
        test = () -> {
            Log.d("William", "Doing stuff every 3 seconds");
            handler.postDelayed(test, 3000);
        };
        handler.postDelayed(test, 0);
        queue = Volley.newRequestQueue(this);
    }*/


    public void getCurrentlyPlayingTrack(final VolleyCallBack volleyCallBack){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, endpoint, null, response -> {
            Gson gson = new Gson();
            currentSong = gson.fromJson(response.toString(), Song.class);
            Log.d("William", "The currently playing song is " + currentSong);
            volleyCallBack.onSuccess();
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }


    /*
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
     */
}
