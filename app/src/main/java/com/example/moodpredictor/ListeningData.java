package com.example.moodpredictor;

import com.spotify.protocol.types.Track;

import java.util.ArrayList;

public class ListeningData {
    public String album;
    public String track;
    public String artist;


    public ListeningData(){

    }

    public ListeningData(Track track){
        this.album = track.album.name;
        this.track = track.name;
        this.artist = track.artist.name;
    }

}
