package com.example.moodpredictor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MoodView extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final String CLIENT_ID = "97ed99b3b2bc461da6bfe088bbd4d8c9";
    private static final String REDIRECT_URI = "http://com.example.moodpredictor/callback";
    private static final int REQUEST_CODE = 1337;
    private String authToken;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    private SpotifyAppRemote mSpotifyAppRemote;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private Handler mHandler;
    private int mInterval = 5000;

    private Track currentTrack;
    private Track newTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_view);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(currentUser.getUid()).child("Listening Data");

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-currently-playing"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);


        mHandler = new Handler();
        //startListening();
        // test: 1hzVfz
        // wak: aLMxk...
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE){
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()){
                case TOKEN:
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    Log.d("William", "Got an auth token!");
                    editor.apply();

                    get(()->{
                        Log.d("William", "get request is happening");
                    });

                    break;
                case ERROR:
                    Log.d("William", "auth error");
                    break;
                default:
                    Log.d("William", "Uh-oh");

            }
        }

    }

    public void get(final VolleyCallBack callBack){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://api.spotify.com/v1/me", null, response -> {
            Log.d("William", response.toString());
            callBack.onSuccess();
        }, error -> get(()->{

        })){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }

        };
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopListening();
    }


    @Override
    protected void onStart() {
        super.onStart();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("William", "Connected!");
                    }

                    public void onFailure(Throwable throwable) {
                        Log.d("William", "Spotify failed to connect.");
                    }
                });

    }

    /*Runnable changeDetector = new Runnable() {
        @Override
        public void run(){
            try {
                Log.d("William", "**RUNNING**");
                if (currentTrack != newTrack){
                    if (currentTrack != null)
                        Log.d("SongChangeListener", "Detected a change in the song! Updating the current track from " +
                                currentTrack.toString() +
                                " to " + newTrack.toString());
                    else
                        Log.d("SongChangeListener", "Detected a change in the song! Updating the current track from " +
                                "null" +
                                " to " + newTrack.toString());
                    currentTrack = newTrack;
                }
            } finally {
                mHandler.postDelayed(changeDetector, mInterval);
            }
        }
    };

    public void startListening(){
        changeDetector.run();
    }

    public void stopListening(){
        mHandler.removeCallbacks(changeDetector);
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }


    public void signOut(View view){
        FirebaseAuth.getInstance().signOut();
        Context context = getApplicationContext();
        CharSequence text = "Successfully signed out.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        Intent intent = new Intent(context, SplashScreenActivity.class);
        startActivity(intent);
    }


}
