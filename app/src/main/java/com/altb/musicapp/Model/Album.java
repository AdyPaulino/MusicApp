package com.altb.musicapp.Model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Lalit Bagga on 16-07-30.
 * <p>
 * Used to Get Album data
 */
public class Album extends MusicData {
    private Artist artist;
    private String coverAlbum;

    private Bitmap coverAlbumBitmap;
    private List<Song> songList;

    public Album(){

    }

    public Album(Artist artist, String coverAlbum, String name, long id) {
        this.artist = artist;
        this.coverAlbum = coverAlbum;
        setName(name);
        setID(id);
    }

    public Artist getArtist() {
        return artist;
    }

    public String getCoverAlbum() {
        return DRAWABLE + coverAlbum;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public Bitmap getCoverAlbumBitmap() {
        return coverAlbumBitmap;
    }

    public void setCoverAlbumBitmap(Bitmap coverAlbumBitmap) {
        this.coverAlbumBitmap = coverAlbumBitmap;
    }


}
