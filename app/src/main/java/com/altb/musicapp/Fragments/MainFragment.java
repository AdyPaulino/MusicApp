package com.altb.musicapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.altb.musicapp.Activities.MainActivity;
import com.altb.musicapp.R;

/*This fragment is used to call other four fragments
 *
  * Created by Lalit Bagga
  * */
public class MainFragment extends Fragment {
    private View v;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {

        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_main, container, false);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.center_actionbar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Home");
        actionBar.show();

        /*This method will show small player layout at bottom if flag is true*/
        MainActivity.showPlayer();
       /*Setting fragments to their respective container */
        MusicCard musicCardFragment;
        MusicCard musicCardFragment2;
        MusicCard musicCardFragment3;
        MusicCard musicCardFragment4;
        MusicCard recentFragment;
        FragmentManager fm = getActivity().getSupportFragmentManager();

        musicCardFragment = MusicCard.newInstance(MusicCard.ARTIST);
        fm.beginTransaction().add(R.id.fArtist, musicCardFragment).commit();

        musicCardFragment2 = MusicCard.newInstance(MusicCard.ALBUMS);
        fm.beginTransaction().add(R.id.albums, musicCardFragment2).commit();

        musicCardFragment3 = MusicCard.newInstance(MusicCard.SONGS);
        fm.beginTransaction().add(R.id.songs, musicCardFragment3).commit();

        musicCardFragment4 = MusicCard.newInstance(MusicCard.GENRES);
        fm.beginTransaction().add(R.id.genres, musicCardFragment4).commit();

        recentFragment = MusicCard.newInstance(MusicCard.RECENT);
        fm.beginTransaction().add(R.id.recentPlayed, recentFragment).commit();
        return v;
    }

}
