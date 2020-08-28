package com.altb.musicapp.Model;

/**
 * Created by adypaulino on 2016-08-06.
 */
public class Genre extends MusicData {

    public Genre(String name){
        setName(name);
    }

    public Genre(String name, long id){
        setName(name);
        setID(id);
    }
}
