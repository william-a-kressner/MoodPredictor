package com.example.moodpredictor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

public class MoodView extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final String CLIENT_ID = "97ed99b3b2bc461da6bfe088bbd4d8c9";
    private static final String REDIRECT_URI = "http://com.example.moodpredictor/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_view);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(currentUser.getUid()).child("Listening Data");
        // test: 1hzVfz
        // wak: aLMxk...
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
                        connected();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.d("William", "Spotify failed to connect.");
                    }
                });

    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                        // Store the song in the user's listening data object
                        writeNewListeningData(track);
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

    private void writeNewListeningData(Track track){
        ListeningData ld = new ListeningData(track);
        myRef.child(track.uri).setValue(ld);
    }
}
