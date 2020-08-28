package com.altb.musicapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.altb.musicapp.Model.Album;
import com.altb.musicapp.R;

import java.util.ArrayList;

/**
 * Created by lalit Bagga on 2016-08-14.
 */
public class PlaylistAdapter extends ArrayAdapter<Album> {

    private ArrayList<String> playListName;
    private Activity context;


    public PlaylistAdapter(Activity context, int resource,ArrayList<String> playListName) {
        super(context, resource);
        this.playListName = playListName;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.content_detail_playlist_view, null, true);
        TextView tPlaylistName = (TextView)rowView.findViewById(R.id.playlistName);
        ImageView playlistImage = (ImageView) rowView.findViewById(R.id.profile_image);

        tPlaylistName.setText(playListName.get(position));

        return rowView;
    }

    @Override
    public int getCount() {
        return playListName.size();
    }
}
