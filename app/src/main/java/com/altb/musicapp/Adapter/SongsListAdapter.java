package com.altb.musicapp.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.altb.musicapp.Activities.MainActivity;
import com.altb.musicapp.Fragments.DetailFragment;
import com.altb.musicapp.Fragments.PlayerFragment;
import com.altb.musicapp.Model.Album;
import com.altb.musicapp.Model.Artist;
import com.altb.musicapp.Model.Genre;
import com.altb.musicapp.Model.MusicData;
import com.altb.musicapp.Model.Song;
import com.altb.musicapp.R;
import com.altb.musicapp.Services.PlayerService;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Lalit Bagga on 16-07-30.
 * <p/>
 * This adapter will be used to push data to list view
 */
public class SongsListAdapter extends ArrayAdapter<MusicData> {
    private List<MusicData> musicDataList;
    private Activity context;
    private Album album;
    private Artist artist;
    private Genre genre;
    private boolean isPlaylist;

    public SongsListAdapter(Activity context, List<MusicData> musicDataList, MusicData musicData) {
        super(context, R.layout.content_musicdetail_listview);
        this.musicDataList = musicDataList;
        this.context = context;
        if (musicData instanceof Album) {
            this.album = (Album) musicData;
        } else if (musicData instanceof Artist) {
            this.artist = (Artist) musicData;
        } else if (musicData instanceof Genre){
            this.genre = (Genre) musicData;
        }
    }

    public SongsListAdapter(Activity context, List<MusicData> musicDataList, MusicData musicData, boolean isPlaylist) {
        this(context, musicDataList, musicData);
        this.isPlaylist = isPlaylist;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.content_musicdetail_listview, null, true);
        CircleImageView circleImageView = (CircleImageView) rowView.findViewById(R.id.profile_image);
        TextView topText = (TextView) rowView.findViewById(R.id.songName);
        TextView bottomText = (TextView) rowView.findViewById(R.id.artistName);
        final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
        if (isPlaylist) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicDataList.get(position).setSelected(checkBox.isSelected());
                }
            });
        }

        final MusicData data = (MusicData) musicDataList.get(position);
        if (data instanceof Album) {
            final Album album = (Album) musicDataList.get(position);
            if (album.getCoverAlbumBitmap() != null){
                circleImageView.setImageBitmap(album.getCoverAlbumBitmap());
            } else {
                String uri = album.getCoverAlbum();
                int resources = circleImageView.getResources().getIdentifier(uri, null, circleImageView.getContext().getPackageName());
                circleImageView.setImageResource(resources);
            }
            topText.setText(album.getName());
            bottomText.setText(album.getArtist().getName());
        } else if (data instanceof Song) {
            Song song = (Song) data;
            if (song.getAlbum().getCoverAlbumBitmap() != null){
                circleImageView.setImageBitmap(song.getAlbum().getCoverAlbumBitmap());
            } else {
                String uri = song.getAlbum().getCoverAlbum();
                int resources = circleImageView.getResources().getIdentifier(uri, null, circleImageView.getContext().getPackageName());
                circleImageView.setImageResource(resources);
            }
            topText.setText(song.getTitle());
            bottomText.setText(song.getComposer());
        } else if (data instanceof Artist){
            Artist artist = (Artist) data;
            circleImageView.setImageBitmap(null);
            topText.setText(artist.getName());
            bottomText.setText("");
        } else if (data instanceof Genre) {
            Genre genre = (Genre) data;
            if (album.getCoverAlbumBitmap() != null){
                circleImageView.setImageBitmap(album.getCoverAlbumBitmap());
            } else {
                String uri = album.getCoverAlbum();
                int resources = circleImageView.getResources().getIdentifier(uri, null, circleImageView.getContext().getPackageName());
                circleImageView.setImageResource(resources);
            }
            topText.setText(genre.getName());
            bottomText.setText(String.valueOf(genre.getID()));
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SONGLISTADAPTER", "Clicked");
                if (data instanceof Song) {
                    if (isPlaylist) {
                        for (int i = musicDataList.size() - 1; i >= 0; i--) {
                            if (!musicDataList.get(i).getSelected()) {
                                musicDataList.remove(i);
                            }
                        }
                    }

                    MainActivity.getMainActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                            PlayerFragment.newInstance(position, musicDataList)).addToBackStack(null).commit();
                } else if (data instanceof Album) {
                    MainActivity.getMainActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                            DetailFragment.newInstance(data)).addToBackStack(null).commit();

                } else if (data instanceof Artist) {

                } else if (data instanceof Genre) {

                    MainActivity.getMainActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                            DetailFragment.newInstance(data)).addToBackStack(null).commit();
                }
            }
        });

        return rowView;
    }

    @Override
    public int getCount() {
        return musicDataList.size();
    }
}
