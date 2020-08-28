package com.altb.musicapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.altb.musicapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Lalit Bagga on 2016-08-13.
 *
 *
 * This adapter is used in MyLibrary fragment. It shows only recent played songs
 */
public class RecentlyPlayedAdapter extends ArrayAdapter<String> {
    String[] fieldName;
    int[] imageId;
    private Context context;

    public RecentlyPlayedAdapter(Context context, int resource,String[] fieldName, int[] imageId) {
        super(context, resource);
        this.fieldName = fieldName;
        this.imageId = imageId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.content_musicdetail_listview, parent, false);
        TextView artistName = (TextView) rowView.findViewById(R.id.artistName);
        TextView songName = (TextView) rowView.findViewById(R.id.songName);
        CircleImageView imageView = (CircleImageView) rowView.findViewById(R.id.profile_image);
        songName.setText(fieldName[position]);
        imageView.setImageResource(imageId[position]);
        artistName.setText("");

        return rowView;

    }

    @Override
    public int getCount() {
        return fieldName.length;
    }
}
