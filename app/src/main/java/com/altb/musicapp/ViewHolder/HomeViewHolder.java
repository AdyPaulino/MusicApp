package com.altb.musicapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.altb.musicapp.Model.Album;
import com.altb.musicapp.Model.Artist;
import com.altb.musicapp.Model.Genre;
import com.altb.musicapp.Model.MusicData;
import com.altb.musicapp.Model.Song;
import com.altb.musicapp.R;

/**
 * This class is viewHolder used to update get the reference and update the views
 * Created by Lalit Bagga on 16-07-30.
 */
public class HomeViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView topText;
    private TextView bottomText;

    public HomeViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.artist_image);
        topText = (TextView) itemView.findViewById(R.id.tArtist);
        bottomText = (TextView) itemView.findViewById(R.id.tAlbum);
    }

    //Method will update the views
    public void updateUI(MusicData data) {

        if (data != null) {
            if (data instanceof Album) {
                Album albumData = (Album) data;
                if (albumData.getCoverAlbumBitmap() != null) {
                    imageView.setImageBitmap(albumData.getCoverAlbumBitmap());
                } else {
                    String uri = albumData.getCoverAlbum();
                    int resources = imageView.getResources().getIdentifier(uri, null, imageView.getContext().getPackageName());
                    imageView.setImageResource(resources);
                }
                topText.setText(albumData.getName());
                bottomText.setText(albumData.getArtist().getName());
            } else if (data instanceof Song) {
                Song songData = (Song) data;
                if (songData.getAlbum().getCoverAlbumBitmap() != null) {
                    imageView.setImageBitmap(songData.getAlbum().getCoverAlbumBitmap());
                } else {
                    String uri = songData.getAlbum().getCoverAlbum();
                    int resources = imageView.getResources().getIdentifier(uri, null, imageView.getContext().getPackageName());
                    imageView.setImageResource(resources);
                }
                topText.setText(songData.getTitle());
                bottomText.setText("");
            } else if (data instanceof Artist) {
                Artist artistData = (Artist) data;
                String uri = artistData.getPicture();
                int resources = imageView.getResources().getIdentifier(uri, null, imageView.getContext().getPackageName());
                imageView.setImageResource(resources);
                topText.setText(artistData.getName());
                bottomText.setText("");
            } else if (data instanceof Genre) {
                Genre genreData = (Genre) data;
//                String uri = artistData.getPicture();
//                int resources = imageView.getResources().getIdentifier(uri, null, imageView.getContext().getPackageName());
//                imageView.setImageResource(resources);
                topText.setText(genreData.getName());
                bottomText.setText("");
            }
        }
    }

}
