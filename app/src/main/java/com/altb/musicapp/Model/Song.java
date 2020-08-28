package com.altb.musicapp.Model;

import java.io.Serializable;

/**
 * Class that describes the music, the song object
 * Created by ady on 2016-07-28.
 */
public class Song extends MusicData {

    private String path;
    private String duration;
    //a song belongs to an album
    private Album album;
    private Genre genre;
    private String lyrics;
    //count the number of times that song was played
    private int timesPlayed;
    private boolean isDeleted;
    private String composer;

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    /**
     * Increase in one by one to help with 'Top 10 songs' list, for instance
     */
    public void increaseTimesPlayed() {
        this.timesPlayed++;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getTitle() {
        return getName();
    }

    public void setTitle(String title) {
        setName(title);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }
}
