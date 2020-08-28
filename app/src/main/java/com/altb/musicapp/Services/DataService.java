package com.altb.musicapp.Services;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.altb.musicapp.Model.Album;
import com.altb.musicapp.Model.Artist;
import com.altb.musicapp.Model.Genre;
import com.altb.musicapp.Model.MusicData;
import com.altb.musicapp.Model.Song;
import com.altb.musicapp.Utils.UtilFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will give all album data
 * Created by Lalit Bagga on 16-07-30.
 */
public class DataService {
    private static DataService ourInstance = new DataService();

    private ContentResolver cr;
    private Uri uri;

    public static DataService getInstance() {
        return ourInstance;
    }

    private DataService() {
    }

    /*Parameters are artist name,imageName,Album name */
    public List<MusicData> getAllAlbumData(Context context, MusicData musicData) {

        List<MusicData> list = new ArrayList<>();

        String selection = getSelection(musicData);

        // ascending order by title
        Cursor cur = getDataFromStorage(context, MediaStore.Audio.Media.ALBUM + " ASC", musicData, selection);

        if (cur != null) {

            if (cur.getCount() > 0) {
                Log.i("DATASERVICE", "Album list:");
                while (cur.moveToNext()) {
                    String artistName = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    Artist artist = new Artist(artistName);
                    String albumName = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    Log.i("Album: ", albumName);

                    Album album = populateAlbum(artist, albumName, context, (long) cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));

                    if (!list.contains(album)) {
                        list.add(album);
                    }
                }
            }

            cur.close();

        }

        return list;
    }

    private Album populateAlbum(Artist artist, String albumName, Context context, long albumId) {
        String cover = "albumart";
        /*if (artist.getName().startsWith("Michael")) {
            cover = "michaelwsmith";
        }*/

        //add to list
        Album album = new Album(artist, cover, albumName, albumId);

        Bitmap coverBitmap = UtilFunctions.getAlbumArt(context, albumId, 150);
        album.setCoverAlbumBitmap(coverBitmap);

        return album;
    }

    /**
     * List of all songs by artist
     *
     * @param context
     * @return list of MusicData object
     */
    public List<MusicData> getAllArtistData(Context context) {
        List<MusicData> list = new ArrayList<>();

        String selection = getSelection(null);

        // ascending order by name
        Cursor cur = getDataFromStorage(context, MediaStore.Audio.Media.ARTIST + " ASC", null, selection);

        if (cur != null) {

            if (cur.getCount() > 0) {
                Log.i("DATASERVICE", "Artist list:");
                while (cur.moveToNext()) {
                    String artistName = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    Artist artist = new Artist(artistName);
                    Log.i("Artist: ", artistName);
                    String picture = "artist";
                    if (artist.getName().startsWith("Michael")) {
                        picture = "michaelwsmith";
                    }
                    artist.setPicture(picture);

                    //add to list
                    if (!list.contains(artist)) {
                        list.add(artist);
                    }
                }
            }

            cur.close();
        }

        return list;
    }

    public List<MusicData> getRecentSongsData() {
        List<MusicData> list = new ArrayList<>();

        return list;
    }

    private Cursor getDataFromStorage(Context context, String sortOrder, MusicData musicData, String selection) {
        cr = context.getContentResolver();

        //read the content from the device
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        //run the android database query
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);

        return cur;
    }

    private Song populateSong(Cursor cur, Context context) {
        return populateSong(cur, null, context);
    }

    private Song populateSong(Cursor cur, Genre genre, Context context) {
        //get the related data from songs
        String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
        String title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
        String duration = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
        String artistName = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        String albumName = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
        String composer = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
        long albumId = (long) cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
        long id = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID));
        Log.i("DATASERVICE", "SONG: " + title);
        // add to list
        Song song = new Song();
        song.setPath(path);
        song.setTitle(title);
        song.setID(id);
        Album album = populateAlbum(new Artist(artistName), albumName, context, albumId);
        song.setAlbum(album);
        if (genre == null) {
            genre = new Genre(MusicData.UNKNOWN);
        }
        song.setGenre(genre);
        song.setDuration(duration);
        song.setLyrics("");
        song.setComposer(composer);

        return song;
    }

    private List<MusicData> populateSongList(Cursor cur, Context context) {
        List<MusicData> list = new ArrayList<>();
        if (cur != null) {

            if (cur.getCount() > 0) {
                Log.i("DATASERVICE", "Song list:");
                while (cur.moveToNext()) {
                    list.add(populateSong(cur, context));
                }
            }
            cur.close();
        }

        return list;
    }

    /**
     * Get all the music files from the device storage
     */
    public List<MusicData> getAllSongsData(Context context, MusicData musicData) {
        List<MusicData> list = new ArrayList<>();
        String orderBy = MediaStore.Audio.Media.TITLE + " ASC";

        if (musicData instanceof Artist) {
            orderBy = MediaStore.Audio.Media.ALBUM + " ASC, ";
            orderBy += MediaStore.Audio.Media.TITLE + " ASC";
        }

        String selection = getSelection(musicData);

        if (musicData == null) {
            //get by genre
            list = getGenre(context, selection, orderBy);
        } else if (musicData instanceof Genre)
        {
          list = getAllSongsByGenre(context, orderBy, musicData, selection);
        } else {
            // ascending order by title
            Cursor cur = getDataFromStorage(context, orderBy, musicData, selection);
            list = populateSongList(cur, context);
        }

        return list;
    }

    private String getSelection(MusicData musicData) {
        //get only music files
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        if (musicData != null) {
            selection += " and ";
            if (musicData instanceof Album) {
                selection += MediaStore.Audio.Media.ALBUM + " = '" + musicData.getName() + "'";
            } else if (musicData instanceof Artist) {
                selection += MediaStore.Audio.Media.ARTIST + " = '" + musicData.getName() + "'";
            } else if (musicData instanceof Genre) {
                selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            }
        }

        return selection;
    }

    public List<MusicData> getAllGenresData(Context context, MusicData musicData) {
        List<MusicData> list = new ArrayList<>();

        String selection = getSelection(musicData);

        int index;
        long genreId;
        String[] proj1 = {MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID};

        Cursor genreCursor = cr.query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, proj1, null, null, null);

        if (genreCursor != null) {

            if (genreCursor.getCount() > 0) {
                Log.i("DATASERVICE", "Album list:");
                while (genreCursor.moveToNext()) {
                    index = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
                    String name = genreCursor.getString(index);

                    index = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);
                    genreId = Long.parseLong(genreCursor.getString(index));

                    Log.i("Tag-Genre name", name);
                    Log.i("Tag-Genre id", String.valueOf(genreId));

                    Genre genre = new Genre(name, genreId);

                    if (!list.contains(genre)) {
                        list.add(genre);
                    }
                }
            }
        }

        return list;
    }

    public List<MusicData> getAllSongsByGenre(Context context, String sortOrder , MusicData musicData, String selection)
    {
        //get songs by genre

        List<MusicData> list = new ArrayList<>();
                uri = MediaStore.Audio.Genres.Members.getContentUri("external", musicData.getID());

                Genre genre = new Genre(musicData.getName(), musicData.getID());

                Cursor cur = cr.query(uri, null, selection, null, sortOrder);
                Log.i("Tag-Number", cur.getCount() + "");
                if (cur.getCount() > 0) {
                    if (cur.moveToFirst()) {
                        do {
                            list.add(populateSong(cur, genre, context));
                        } while (cur.moveToNext());
                    }
                }
                cur.close();

        return list;

    }

    private List<MusicData> getGenre(Context context, String selection, String sortOrder) {
        //get songs by genre

        List<MusicData> list = new ArrayList<>();

        int index;
        long genreId;
        Cursor genreCursor;
        String[] proj1 = {MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID};

        genreCursor = cr.query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, proj1, null, null, null);
        if (genreCursor.moveToFirst()) {
            do {
                index = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
                String name = genreCursor.getString(index);
                Log.i("Tag-Genre name", name);

                index = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);
                genreId = Long.parseLong(genreCursor.getString(index));
                uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);

                Genre genre = new Genre(name);

                Cursor cur = cr.query(uri, null, selection, null, sortOrder);
                Log.i("Tag-Number", cur.getCount() + "");
                if (cur.getCount() > 0) {
                    if (cur.moveToFirst()) {
                        do {
                            list.add(populateSong(cur, genre, context));
                        } while (cur.moveToNext());
                    }
                }
                cur.close();
            } while (genreCursor.moveToNext());

        }

        genreCursor.close();

        return list;
    }

}
