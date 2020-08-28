package com.altb.musicapp.Model;

import java.io.Serializable;

/**
 * This is a parent class, so all classes can extend this.
 * Applying polymorphism pattern.
 * Created by ady on 2016-07-31.
 */
public class MusicData implements Serializable {

    final String DRAWABLE = "drawable/";

    /**
     * For all undefined names
     */
    public final static String UNKNOWN = "Unknown";

    protected long ID = 0;

    private String name;

    private boolean selected;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSelected(boolean isSelected) {
        this.selected = isSelected;
    }

    public boolean getSelected() {
        return selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MusicData musicData = (MusicData) o;

        return name.equals(musicData.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
