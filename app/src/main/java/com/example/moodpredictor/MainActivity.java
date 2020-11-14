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

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final String CLIENT_ID = "97ed99b3b2bc461da6bfe088bbd4d8c9";
    private static final String REDIRECT_URI = "http://com.example.moodpredictor/callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Log.d("William", "Found a user");
            //We can use the app: we're signed in
        }
        else{
            //We're not signed in: take us to the screen.
            Log.d("William", "Didn't find a user");
            Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            startActivity(intent);
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI).showAuthView(true).build();
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("William", "Connected!");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.d("William", "Spotify failed to connect.");
                    }
                });

    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}
