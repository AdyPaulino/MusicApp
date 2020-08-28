package com.altb.musicapp.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.altb.musicapp.Adapter.PlaylistAdapter;
import com.altb.musicapp.Adapter.SongsListAdapter;
import com.altb.musicapp.Model.MusicData;
import com.altb.musicapp.R;
import com.altb.musicapp.Services.DataService;
import com.altb.musicapp.Services.PlayerService;
import com.altb.musicapp.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by Lalit Bagga
 * A Class is used to add plalist
 */
public class Playlist extends Fragment {

    private ListView listView;
    private ArrayList<String> playlistNameList;
    ArrayAdapter<String> stringArrayAdapter;
    ListView showAllSongs;

    public Playlist() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_playlist, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.center_actionbar);
        View actionBarView = actionBar.getCustomView();
        TextView view = (TextView) actionBarView.findViewById(R.id.actionTitle);
        final ImageView add = (ImageView) actionBarView.findViewById(R.id.imageView);

        add.setVisibility(View.VISIBLE);
        view.setText("My Playlist");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.show();

        listView = (ListView) v.findViewById(R.id.listView);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Enter a name for this new playlist");
        final View dialoglayout = inflater.inflate(R.layout.add_playlist_alert_dialog, null);
        final EditText playlistText = (EditText) dialoglayout.findViewById(R.id.addPlayList);

        playlistNameList = new ArrayList<>();
        playlistNameList.add("Favourite");
        playlistNameList.add("Top 10 ");

        final PlaylistAdapter playlistAdapter = new PlaylistAdapter(getActivity(), R.layout.content_detail_playlist_view, playlistNameList);
        alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playListName = playlistText.getText().toString();
                playlistNameList.add(playListName);
                playlistAdapter.notifyDataSetChanged();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        /*on Add pressed show dialog to add playlist*/
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBuilder.setView(dialoglayout);
                alertDialogBuilder.show();
                add.setVisibility(View.GONE);
            }
        });


        listView.setAdapter(playlistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    ShowMusic music = new ShowMusic();
                    Bundle args = new Bundle();
                    args.putInt(Constants.MUSIC, 0);
                    args.putBoolean("PLAYLIST", true);
                    music.setArguments(args);
                    fm.beginTransaction().replace(R.id.main_container, music).addToBackStack(null).commit();
                }
            }
        });

        /*on long click add show number of songs and hide that view*/
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final AlertDialog.Builder alertDialogBuilders = new AlertDialog.Builder(getContext());
                final View dialoglayouts = inflater.inflate(R.layout.show_all_songs_playlist, null);
                alertDialogBuilders.setTitle("Add Song");
                final AlertDialog alert = alertDialogBuilders.create();
                showAllSongs = (ListView) dialoglayouts.findViewById(R.id.listView);

                SongsListAdapter songsListAdapter = new SongsListAdapter(getActivity(), DataService.getInstance()
                        .getAllArtistData(getContext()), new MusicData());
                showAllSongs.setAdapter(songsListAdapter);

                alert.setView(dialoglayouts);
                alert.show();
                return false;
            }
        });


        return v;
    }

}
