package com.altb.musicapp.Model;

import java.util.List;

/**
 * Created by adypaulino on 2016-08-05.
 */
public class Artist extends MusicData {

    private List<MusicData> albumList;
    private String picture;

    public Artist(String name){
        setName(name);
    }

    public List<MusicData> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<MusicData> albumList) {
        this.albumList = albumList;
    }

    public String getPicture() {
        return DRAWABLE + picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
