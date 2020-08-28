package com.altb.musicapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.altb.musicapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Lalit Bagga on 2016-08-13.
 *
 * This adapter is used in MyLibrary fragnment. It shows only Four value
 */
public class FieldAdapter extends ArrayAdapter<String> {
    private String[] fieldName;
    private int[] imageId;
    private Context context;

    public FieldAdapter(Context context, int resource, String[] fieldName, int[] imageId) {
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
        TextView fields = (TextView) rowView.findViewById(R.id.artistName);
        TextView textView1 = (TextView) rowView.findViewById(R.id.songName);
        CircleImageView imageView = (CircleImageView) rowView.findViewById(R.id.profile_image);

        textView1.setVisibility(View.GONE);
        fields.setText(fieldName[position]);
        fields.setPadding(0,30,0,0);
        imageView.setBorderColor(Color.parseColor("#D33737"));
        imageView.setImageResource(imageId[position]);

        return rowView;

    }

    @Override
    public int getCount() {
        return fieldName.length;
    }
}
