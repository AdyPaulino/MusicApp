package com.altb.musicapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.altb.musicapp.Activities.MainActivity;
import com.altb.musicapp.Adapter.SongsListAdapter;
import com.altb.musicapp.Model.Album;
import com.altb.musicapp.Model.Artist;
import com.altb.musicapp.Model.Genre;
import com.altb.musicapp.Model.MusicData;
import com.altb.musicapp.R;
import com.altb.musicapp.Services.DataService;
import com.altb.musicapp.Services.PlayerService;

import java.io.Serializable;
import java.util.List;

/**
 * Created By Lalit Bagga
 * <p/>
 * This Fragment is used to get the MUSIC Description  data and show IT
 */
public class DetailFragment extends Fragment {


    private static final String ARTIST_NAME = "artistname";
    private static final String ALBUM_NAME = "albumname";
    private static final String IMAGE_URL = "imageurl";
    private static final String MUSIC_DATA = "musicData";
    private static final String PLAYER_SERVICE = "playerService";

    private String mArtist;
    private String mAlbum;
    private String mImageUrl;

    private MusicData musicData;
    private PlayerService playerService;

    private ListView listView;


    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(MusicData musicData) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(MUSIC_DATA, (Serializable) musicData);

        if (musicData instanceof Album) {
            Album album = (Album) musicData;
            args.putString(ARTIST_NAME, album.getArtist().getName());
            args.putString(ALBUM_NAME, album.getName());
            args.putString(IMAGE_URL, album.getCoverAlbum());
        } else if (musicData instanceof Artist) {
            Artist artist = (Artist) musicData;
            args.putString(ARTIST_NAME, artist.getName());
            args.putString(ALBUM_NAME, "");
            args.putString(IMAGE_URL, artist.getPicture());
        } else if (musicData instanceof Genre) {
            Genre genre = (Genre) musicData;
            args.putString(ARTIST_NAME, genre.getName());
            args.putString(ALBUM_NAME, "");
//            args.putString(IMAGE_URL, artist.getPicture());
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                actionBar.hide();

         /*This method will show small player layout at bottom if flag is true*/
        MainActivity.showPlayer();

        if (getArguments() != null) {
            mArtist = getArguments().getString(ARTIST_NAME);
            mAlbum = getArguments().getString(ALBUM_NAME);
            mImageUrl = getArguments().getString(IMAGE_URL);
            musicData = (MusicData) getArguments().getSerializable(MUSIC_DATA);
            playerService = ((MainActivity)getContext()).getPlayerService();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_album, container, false);
        MainActivity.getMainActivity().toggleNavBar();
        ImageView blurImage = (ImageView) v.findViewById(R.id.blurImageView);
        ImageView profileImage = (ImageView) v.findViewById(R.id.profile_image);
        Button playAll = (Button) v.findViewById(R.id.playAll);
        listView = (ListView) v.findViewById(R.id.listView1);

        if(mImageUrl != null) {
            int resources = profileImage.getResources().getIdentifier(mImageUrl, null,
                    profileImage.getContext().getPackageName());
            profileImage.setImageResource(resources);
            blurImage.setImageResource(resources);
        }

        if (musicData instanceof Album) {
            final List<MusicData> songList = getAllSongsByAlbum(getActivity(), musicData);

            playAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //get all songs from all albuns to play
                    List<MusicData> songs = getAllSongsByArtist(getActivity(), musicData);

                    MainActivity.getMainActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                            PlayerFragment.newInstance(-1, songs)).commit();
                }
            });

            SongsListAdapter songsListAdapter = new SongsListAdapter(getActivity(), songList, musicData);
            listView.setAdapter(songsListAdapter);
        } else if (musicData instanceof Artist){
            final List<MusicData> albumsByArtistList = getAllAlbumsByArtist(getActivity(), musicData);
            ((Artist) musicData).setAlbumList(albumsByArtistList);

            playAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //get all songs from all albuns to play
                    List<MusicData> songs = getAllSongsByArtist(getActivity(), musicData);

                    MainActivity.getMainActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                            PlayerFragment.newInstance(-1, songs)).commit();
                }
            });

            SongsListAdapter songsListAdapter = new SongsListAdapter(getActivity(), albumsByArtistList, musicData);
            listView.setAdapter(songsListAdapter);
        } else if (musicData instanceof Genre) {
            final List<MusicData> songList = getAllSongsByGenre(getActivity(), musicData);

            playAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //get all songs from all albuns to play
                    List<MusicData> songs = getAllSongsByArtist(getActivity(), musicData);

                    MainActivity.getMainActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                            PlayerFragment.newInstance(-1, songs)).commit();
                }
            });

            SongsListAdapter songsListAdapter = new SongsListAdapter(getActivity(), songList, musicData);
            listView.setAdapter(songsListAdapter);
        }
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                    actionBar.show();
                    getActivity().onBackPressed();

                    return true;
                }
                return false;
            }
        } );


        return v;
    }


    /**
     * Get the list of all songs.
     * To reuse in other fragments
     *
     * @return list of songs
     */
    public List<MusicData> getAllSongs(Context context) {
        return DataService.getInstance().getAllSongsData(context, null);
    }

    /**
     * Get the list of all songs from a particular album
     *
     * @return list of songs
     */
    public List<MusicData> getAllSongsByAlbum(Context context, MusicData album) {
        return DataService.getInstance().getAllSongsData(context, album);
    }

    /**
     * Get the list of all songs from a particular artist
     *
     * @param context
     * @param artist
     * @return list of songs
     */
    public List<MusicData> getAllSongsByArtist(Context context, MusicData artist) {
        return DataService.getInstance().getAllSongsData(context, artist);
    }

    /**
     * Get the list of all albums from a particular artist
     *
     * @param context
     * @param artist
     * @return list of albums
     */
    public List<MusicData> getAllAlbumsByArtist(Context context, MusicData artist) {
        return DataService.getInstance().getAllAlbumData(context, musicData);
    }

    /**
     * Get the list of all songs in a particular genre
     *
     * @return list of songs
     */
    public List<MusicData> getAllSongsByGenre(Context context, MusicData genre) {
        return DataService.getInstance().getAllSongsData(context, genre);
    }

}
