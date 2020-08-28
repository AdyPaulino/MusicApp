package com.altb.musicapp.Fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.altb.musicapp.Activities.MainActivity;
import com.altb.musicapp.Adapter.MusicAdapter;
import com.altb.musicapp.R;
import com.altb.musicapp.Services.DataService;

/*This fragment will get data from data service and push to adapter and will set to recycler view
* to show music
*
* Created by Lalit Bagga
* */
public class MusicCard extends Fragment {

    private View v;
    private static final String ARG_TYPE = "TYPE";
    private int cardType;

    public static final int ARTIST = 0;
    public static final int ALBUMS = 1;
    public static final int SONGS = 2;
    public static final int RECENT = 3;
    public static final int GENRES = 4;


    public MusicCard() {
        // Required empty public constructor
    }

    //This will be called which is having id to which fragnment to run
    public static MusicCard newInstance(int type) {
        MusicCard fragment = new MusicCard();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    //Getting all ids on create
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardType = getArguments().getInt(ARG_TYPE);
        }
         /*This method will show small player layout at bottom if flag is true*/
        MainActivity.showPlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_artist_home, container, false);

        /*Referencing recycler view*/
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

                /*Setting layout to HORIZONTAL*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);

        MusicAdapter musicAdapter;
        if (cardType == ARTIST) {
            musicAdapter = new MusicAdapter(DataService.getInstance().getAllArtistData(getActivity()));
            int artistSize = musicAdapter.getItemCount();
            /*Set scroll position accoring to number of artist or albumbs*/
            layoutManager.scrollToPositionWithOffset((artistSize - 1), 20);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new HorizontalSpaceItemDecorator(1));
            recyclerView.setAdapter(musicAdapter);

        } else if (cardType == ALBUMS) {
            musicAdapter = new MusicAdapter(DataService.getInstance().getAllAlbumData(getActivity(), null));
            int albumSize = musicAdapter.getItemCount();
            /*Set scroll position accoring to number of albumbs*/
            layoutManager.scrollToPositionWithOffset((albumSize - 1), 20);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new HorizontalSpaceItemDecorator(1));
            recyclerView.setAdapter(musicAdapter);
        } else if (cardType == SONGS) {
            musicAdapter = new MusicAdapter(DataService.getInstance().getAllSongsData(getActivity(), null));
            int allSongsSize = musicAdapter.getItemCount();

            /*Set scroll position accoring to number of songs s*/
            layoutManager.scrollToPositionWithOffset((allSongsSize - 1), 20);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new HorizontalSpaceItemDecorator(1));
            recyclerView.setAdapter(musicAdapter);
        } else if (cardType == GENRES) {
            musicAdapter = new MusicAdapter(DataService.getInstance().getAllGenresData(getActivity(), null));
            int allGenresSize = musicAdapter.getItemCount();

            /*Set scroll position accoring to number of songs s*/
            layoutManager.scrollToPositionWithOffset((allGenresSize - 1), 20);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new HorizontalSpaceItemDecorator(1));
            recyclerView.setAdapter(musicAdapter);
        } else {
            musicAdapter = new MusicAdapter(DataService.getInstance().getRecentSongsData());
        }
        return v;
    }
}

/*Class is used to provide space between cards*/
class HorizontalSpaceItemDecorator extends RecyclerView.ItemDecoration {
    private final int spacer;

    public HorizontalSpaceItemDecorator(int spacer) {
        this.spacer = spacer;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.right = spacer;
    }
}
