package com.example.moodpredictor;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class CurrentlyPlayingService {
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public CurrentlyPlayingService(Context context){
        sharedPreferences = context.getSharedPreferences("SPOTIFY",0);
        queue = Volley.newRequestQueue(context);
    }

    public String getCurrentlyPlayingTrack(){
        return "foo";
    }
}
