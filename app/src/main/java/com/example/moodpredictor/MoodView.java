package com.example.moodpredictor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

    private Handler mHandler;
    private int mInterval = 5000;
    //private Runnable changeDetector;

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
        mHandler = new Handler();
        startListening();
        // test: 1hzVfz
        // wak: aLMxk...
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopListening();
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
                        //SongChangeListener songChangeListener = new SongChangeListener(mSpotifyAppRemote);
                        //songChangeListener.run();
                        connected();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.d("William", "Spotify failed to connect.");
                    }
                });

    }

    Runnable changeDetector = new Runnable() {
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
            }
            finally {
                mHandler.postDelayed(changeDetector, mInterval);
            }
        }
    };

    public void startListening(){
        changeDetector.run();
    }

    public void stopListening(){
        mHandler.removeCallbacks(changeDetector);
    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    newTrack = track;
                    Log.d("MainActivity", track.name + " by " + track.artist.name);
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
