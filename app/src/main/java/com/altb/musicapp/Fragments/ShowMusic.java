package com.altb.musicapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.altb.musicapp.Adapter.SongsListAdapter;
import com.altb.musicapp.Model.Album;
import com.altb.musicapp.Model.Artist;
import com.altb.musicapp.Model.MusicData;
import com.altb.musicapp.Model.Song;
import com.altb.musicapp.R;
import com.altb.musicapp.Services.DataService;
import com.altb.musicapp.Utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*Created By Lalit Bagg
*
*
* Class is used to show all music related information
* */
public class ShowMusic extends Fragment {
    private ListView listView;
    private SongsListAdapter songsListAdapter;


    public ShowMusic() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_show_music, container, false);
        int position = getArguments().getInt(Constants.MUSIC);
        List<MusicData> listMusicData = (List<MusicData>) getArguments().getSerializable(Constants.MUSIC_DATA);
        boolean recentPlayed = getArguments().getBoolean(Constants.RECENT_PLAYED);

        boolean isPlaylist = getArguments().containsKey("PLAYLIST");

        listView = (ListView) v.findViewById(R.id.listView);
        Button shuffleAll = (Button) v.findViewById(R.id.playAll);

        List<MusicData> musicDataToShow = new ArrayList<>();


        DetailFragment detailFragment = new DetailFragment();
        if (position == 0) {
              /*Add All listMusicData to songslist adapter ..*/
            if (recentPlayed) {
                if (listMusicData != null && !listMusicData.isEmpty()) {
                    musicDataToShow = listMusicData;
                }
            } else {
                musicDataToShow = detailFragment.getAllSongs(getContext());
            }

            if (!isPlaylist) {
                songsListAdapter = new SongsListAdapter(getActivity(), musicDataToShow, null);
            }
            else {
                songsListAdapter = new SongsListAdapter(getActivity(), musicDataToShow, null, true);
            }
            listView.setAdapter(songsListAdapter);
        } else if (position == 1) {
            /*Add All Artist*/
            if (recentPlayed) {
                if (listMusicData != null && !listMusicData.isEmpty()) {
                    for (MusicData data : listMusicData) {
                        Artist artist = ((Song) data).getAlbum().getArtist();
                        if (!musicDataToShow.contains(artist)) {
                            musicDataToShow.add(artist);
                        }
                    }
                }
            } else {
                musicDataToShow = DataService.getInstance().getAllArtistData(getActivity());
            }
            songsListAdapter = new SongsListAdapter(getActivity(), musicDataToShow, new Artist(""));
            listView.setAdapter(songsListAdapter);
        } else if (position == 2) {
            /*Add All Albums*/
            if (recentPlayed) {
                if (listMusicData != null && !listMusicData.isEmpty()) {
                    for (MusicData data : listMusicData) {
                        Album album = (Album) ((Song) data).getAlbum();
                        if (!musicDataToShow.contains(album)) {
                            musicDataToShow.add(album);
                        }
                    }
                }
            } else {
                musicDataToShow = DataService.getInstance().getAllAlbumData(getActivity(), null);
            }
            songsListAdapter = new SongsListAdapter(getActivity(), musicDataToShow, new Album());
            listView.setAdapter(songsListAdapter);
        } else {
            /*show Playlist*/
        }


        /*Shuffle and play all music*/
        final List<MusicData> list = musicDataToShow;
        shuffleAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MusicData musicData = null;
                Collections.shuffle(list);
                if (list != null && !list.isEmpty()) {
                    musicData = list.get(0);
                }
                if (musicData instanceof Song) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                            PlayerFragment.newInstance(-1, list)).addToBackStack(null).commit();
                } else {
                    songsListAdapter = new SongsListAdapter(getActivity(), list, null);
                    listView.setAdapter(songsListAdapter);
                }
            }
        });
        return v;
    }

}
