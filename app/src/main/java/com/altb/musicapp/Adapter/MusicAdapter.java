package com.altb.musicapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.altb.musicapp.Activities.MainActivity;
import com.altb.musicapp.Model.MusicData;
import com.altb.musicapp.R;
import com.altb.musicapp.ViewHolder.HomeViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by Lalit Bagga on 16-07-30.
 * <p/>
 * This adapter will get data and pass to UI and also set On Click listener which is at Home
 * Activity
 */
public class MusicAdapter extends RecyclerView.Adapter<HomeViewHolder> {

    private List<MusicData> dataList;

    public MusicAdapter(List<MusicData> dataList) {
        if (dataList != null) {
            // reverse in order to make the list alphabetical
            //Collections.reverse(dataList);
        }
        this.dataList = dataList;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_home, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, final int position) {
        final MusicData data = dataList.get(position);
        holder.updateUI(data);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getMainActivity().loadDetailScreen(data, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
