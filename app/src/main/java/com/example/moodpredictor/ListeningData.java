package com.example.moodpredictor;

import com.spotify.protocol.types.Track;

import java.time.LocalDateTime;

public class ListeningData {
    public String album;
    public String track;
    public String artist;
    public TimeOfDay timeOfDay;
    public LocalDateTime localDateTime;

    public ListeningData(){

    }

    public ListeningData(Track track){
        this.album = track.album.name;
        this.track = track.name;
        this.artist = track.artist.name;
        this.localDateTime = LocalDateTime.now();
        int hour = LocalDateTime.now().getHour();
        if (4 <= hour && hour <10)
            this.timeOfDay = TimeOfDay.MORNING;
        else if (10<=hour && hour<16)
            this.timeOfDay = TimeOfDay.AFTERNOON;
        else if (16<=hour && hour < 22)
            this.timeOfDay = TimeOfDay.EVENING;
        else
            this.timeOfDay = TimeOfDay.NIGHT;
    }

}
