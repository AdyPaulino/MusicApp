package com.altb.musicapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.altb.musicapp.Activities.MainActivity;
import com.altb.musicapp.Adapter.FieldAdapter;
import com.altb.musicapp.Adapter.RecentlyPlayedAdapter;
import com.altb.musicapp.Model.MusicData;
import com.altb.musicapp.R;
import com.altb.musicapp.Utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*Created By Lalit Bagga
*
* This Fragnment shows recent played songs and divide songs by albums,artist...
* */
public class MyLibrary extends Fragment {

    private String[] fieldsName = {"Songs", "Artists", "Albums", "Playlists"};
    private int[] imageId = {R.drawable.music, R.drawable.target, R.drawable.album, R.drawable.playlist};
    private ListView headerListView;


    public MyLibrary() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         /*This method will show small player layout at bottom if flag is true*/
        MainActivity.showPlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_library, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.center_actionbar);
        View actionBarView = actionBar.getCustomView();
        TextView view = (TextView) actionBarView.findViewById(R.id.actionTitle);
        view.setText("My Library");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.show();

        headerListView = (ListView) v.findViewById(R.id.listView);
        ListView recentPlayedListView = (ListView) v.findViewById(R.id.recentPlayedList);

        FieldAdapter fieldAdapter = new FieldAdapter(getContext(), R.layout.content_musicdetail_listview,
                fieldsName, imageId);
        RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(getContext(), R.layout.content_musicdetail_listview,
                fieldsName, imageId);

        headerListView.setAdapter(fieldAdapter);
        recentPlayedListView.setAdapter(recentlyPlayedAdapter);

        headerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                populateLists(position, false);
            }
        });

        recentPlayedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  /*Get that song and go to next fragment*/
                populateLists(position, true);
            }
        });

        return v;
    }

    private void populateLists(int position, boolean recentPlayed) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ShowMusic music = new ShowMusic();
        Bundle args = new Bundle();
                /*Get that field and go to next fragment and pass int value of that fragnment */
        List<MusicData> recentPlayedList = new ArrayList<>();
        recentPlayedList.addAll(Constants.SONGS_QUEUE_RECENT_PLAYED);
        args.putSerializable(Constants.MUSIC_DATA, (Serializable) recentPlayedList);
        args.putBoolean(Constants.RECENT_PLAYED, recentPlayed);

        switch (position) {
            case 0:
                        /*Songs*/
                args.putInt(Constants.MUSIC, 0);
                music.setArguments(args);
                fm.beginTransaction().replace(R.id.main_container, music).addToBackStack(null).commit();
                break;
            case 1:
                        /*Artist*/
                args.putInt(Constants.MUSIC, 1);
                music.setArguments(args);
                fm.beginTransaction().replace(R.id.main_container, music).addToBackStack(null).commit();
                break;
            case 2:
                        /*Album*/
                args.putInt(Constants.MUSIC, 2);
                music.setArguments(args);
                fm.beginTransaction().replace(R.id.main_container, music).addToBackStack(null).commit();
                break;
            case 3:
                        /*Playlist*/
                Playlist playlist = new Playlist();
                args.putInt(Constants.MUSIC, 3);
                music.setArguments(args);
                fm.beginTransaction().replace(R.id.main_container, playlist).addToBackStack(null).commit();
        }
    }

}
