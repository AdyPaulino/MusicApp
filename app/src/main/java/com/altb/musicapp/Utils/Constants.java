package com.altb.musicapp.Utils;

import android.os.Handler;

import com.altb.musicapp.Model.MusicData;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Lalit Bagga on 2016-08-06.
 */

/*Class is used to set the status of buttons in player screen */
public class Constants {

    public static String MUSIC = "MUSIC";
    public static String MUSIC_DATA = "MUSIC_DATA";
    public static String RECENT_PLAYED = "RECENT_PLAYED";

    public static Boolean REPEAT_ALL_FLAG = false;
    public static Boolean REPEAT_ONE_FLAG = false;
    public static Boolean PLAY_FLAG = false;
    public static Boolean PLAYLIST_FLAG = false;
    public static Boolean SETTINGS_FLAG = false;
    public static Boolean EDIT_FLAG = false;

    //Player constants
    //song is playing or paused
    public static boolean SONG_PAUSED = true;
    //song changed (next, previous)
    public static boolean SONG_CHANGED = false;
    //List of Songs
    public static LinkedList<MusicData> SONGS_QUEUE = new LinkedList<MusicData>() ;
    //List of Songs recently played
    public static LinkedList<MusicData> SONGS_QUEUE_RECENT_PLAYED = new LinkedList<MusicData>() ;
    //song number which is playing right now from SONGS_QUEUE
    public static int SONG_NUMBER = 0;
    //handler for song changed(next, previous) defined in service(PlayerService)
    public static Handler SONG_CHANGE_HANDLER;
    //handler for song play/pause defined in service(SongService)
    public static Handler PLAY_PAUSE_HANDLER;
    //handler for showing song progress defined in Activities(MainActivity, AudioPlayerActivity)
    public static Handler PROGRESSBAR_HANDLER;
    //units to increase/decrease volume
    public static int VOLUME_UNIT = 1;
    //time that the music started playing
    public static Date TIME_START_PLAYING;
    //constants for timer
    public static int TIMER_30_MIN = 3;
    //constants for timer
    public static int TIMER_60_MIN = 60;
    //constants for timer
    public static int TIMER_2_MIN = 2;

}
