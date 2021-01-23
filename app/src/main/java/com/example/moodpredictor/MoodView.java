package com.example.moodpredictor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class MoodView extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final String CLIENT_ID = "97ed99b3b2bc461da6bfe088bbd4d8c9";
    private static final String REDIRECT_URI = "http://com.example.moodpredictor/callback";
    private static final int REQUEST_CODE = 1337;
    private String authToken;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private CurrentlyPlayingService currentlyPlayingService;

    private SpotifyAppRemote mSpotifyAppRemote;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private Song currentSong;

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

        currentlyPlayingService = new CurrentlyPlayingService(getApplicationContext());

        sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);



        // test: 1hzVfz
        // wak: aLMxk...
    }

    public void getCurrentSong(View view){
        currentlyPlayingService.getCurrentlyPlayingTrack(() -> {
            Log.d("William", "This is the callback");
        });
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
                    break;
                case ERROR:
                    Log.d("William", "auth error");
                    break;
                default:
                    Log.d("William", "Uh-oh");

            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
