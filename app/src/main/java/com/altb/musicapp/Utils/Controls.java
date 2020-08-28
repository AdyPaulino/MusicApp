package com.altb.musicapp.Utils;

import android.content.Context;

import com.altb.musicapp.R;
import com.altb.musicapp.Services.PlayerService;

/**
 * Created by ady on 2016-08-12.
 */
public class Controls {
    static String LOG_CLASS = "Controls";

    public static void playControl(Context context) {
        sendMessage(context.getResources().getString(R.string.play));
    }

    public static void stopControl(Context context) {
        sendMessage(context.getResources().getString(R.string.stop));
    }

    public static void pauseControl(Context context) {
        sendMessage(context.getResources().getString(R.string.pause));
    }

    public static void nextControl(Context context) {
        boolean isServiceRunning = UtilFunctions.isServiceRunning(PlayerService.class.getName(), context);
        if (!isServiceRunning)
            return;
        if (Constants.SONGS_QUEUE.size() > 0) {
            if (Constants.SONG_NUMBER < (Constants.SONGS_QUEUE.size() - 1)) {
                Constants.SONG_NUMBER++;
                Constants.SONG_CHANGE_HANDLER.sendMessage(Constants.SONG_CHANGE_HANDLER.obtainMessage());
            } else {
                Constants.SONG_NUMBER = 0;
                Constants.SONG_CHANGE_HANDLER.sendMessage(Constants.SONG_CHANGE_HANDLER.obtainMessage());
            }
        }
        Constants.SONG_PAUSED = false;
    }

    public static void previousControl(Context context) {
        boolean isServiceRunning = UtilFunctions.isServiceRunning(PlayerService.class.getName(), context);
        if (!isServiceRunning)
            return;
        if (Constants.SONGS_QUEUE.size() > 0) {
            if (Constants.SONG_NUMBER > 0) {
                Constants.SONG_NUMBER--;
                Constants.SONG_CHANGE_HANDLER.sendMessage(Constants.SONG_CHANGE_HANDLER.obtainMessage());
            } else {
                Constants.SONG_NUMBER = Constants.SONGS_QUEUE.size() - 1;
                Constants.SONG_CHANGE_HANDLER.sendMessage(Constants.SONG_CHANGE_HANDLER.obtainMessage());
            }
        }
        Constants.SONG_PAUSED = false;
    }

    private static void sendMessage(String message) {
        try {
            Constants.PLAY_PAUSE_HANDLER.sendMessage(Constants.PLAY_PAUSE_HANDLER.obtainMessage(0, message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
