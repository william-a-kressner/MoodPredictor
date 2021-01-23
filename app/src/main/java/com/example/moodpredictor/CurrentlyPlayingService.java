package com.example.moodpredictor;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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

    public Song getCurrentSong() {
        return currentSong;
    }

    private Song currentSong;

    public CurrentlyPlayingService(Context context){
        sharedPreferences = context.getSharedPreferences("SPOTIFY",0);
        queue = Volley.newRequestQueue(context);
    }

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
}
