package com.altb.musicapp.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.altb.musicapp.Activities.MainActivity;
import com.altb.musicapp.Fragments.PlayerFragment;
import com.altb.musicapp.Model.MusicData;
import com.altb.musicapp.Model.Song;
import com.altb.musicapp.R;
import com.altb.musicapp.Utils.Constants;
import com.altb.musicapp.Utils.UtilFunctions;
import com.altb.musicapp.receiver.NotificationBroadcast;

import java.io.Serializable;
import java.util.Queue;

/**
 * This class is responsible for the main features of the MUSIC App.
 * Created by ady on 2016-07-28.
 */
public class PlayerService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private static final String LOG = "PlayerService";

    int NOTIFICATION_ID = 1111;
    public static final String NOTIFY_PREVIOUS = "com.altb.musicapp.previous";
    public static final String NOTIFY_DELETE = "com.altb.musicapp.delete";
    public static final String NOTIFY_PAUSE = "com.altb.musicapp.pause";
    public static final String NOTIFY_PLAY = "com.altb.musicapp.play";
    public static final String NOTIFY_NEXT = "com.altb.musicapp.next";

    //variables
    private Queue<MusicData> songList;
    //media player
    private MediaPlayer player;
    //current position
    private int songPosition;
    //class to bind the service and activities
    private final IBinder musicBind = new MusicBinder();

    // forward and backward interval
    private static final int INTERVAL = 5000;
    private double startTime = 0;
    private double finalTime = 0;

    /*
    All - true
    One - false
    None - null
  */
    private Boolean repeat;

    private PlayerListener listener;

    private Song currentSong;

    private static boolean currentVersionSupportLockScreenControls = false;
    private static boolean currentVersionSupportBigNotification = false;
    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    AudioManager audioManager;
    Bitmap mDummyAlbumArt;

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosition = 0;
        //create player
        player = new MediaPlayer();

        currentVersionSupportLockScreenControls = UtilFunctions.currentVersionSupportLockScreenControls();
        currentVersionSupportBigNotification = UtilFunctions.currentVersionSupportBigNotification();

        initMusicPlayer();
    }

    public void initMusicPlayer() {
        //set player properties

        //player settings
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setSongList(Queue<MusicData> songList) {
        this.songList = songList;
    }

    public void setSong(int songIndex) {
        songPosition = songIndex;
    }

    public void setRepeat(Boolean repeat) {
        this.repeat = repeat;
    }

    /**
     * Play the selected music in the array position
     */
    public void playSong(boolean isNextPrevious) {

        //when clicked next or previous, the paused song will be ignored
        if (isNextPrevious) {
            Constants.SONG_PAUSED = false;
        }

        playSong();
    }

    /**
     * Play the selected music in the array position
     */
    public void playSong() {

        if (!Constants.SONG_PAUSED) {
            //reset the player
            player.reset();

            //get song
            currentSong = (Song) Constants.SONGS_QUEUE.removeFirst();
            //put at the end of the list again
            Constants.SONGS_QUEUE.addLast(currentSong);
            //get id
            long currSong = currentSong.getID();
            //set uri
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currSong);

            try {
                player.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }

            //callback to prepare the player
            player.prepareAsync();
        } else {
            //just resume playing at the same point it stopped
            player.start();
        }

        Constants.SONG_PAUSED = false;

        //add to the recent songs played
        if (!Constants.SONGS_QUEUE_RECENT_PLAYED.contains(currentSong)){
            Constants.SONGS_QUEUE_RECENT_PLAYED.add(currentSong);
        }

        newNotification();
    }

    public void stopSong() {
        //stop the player
        player.stop();
        Constants.SONG_PAUSED = true;

        NotificationBroadcast.stopActions(getApplicationContext());

        newNotification();
    }

    public void forwardSong() {
        forwardSong(player.getCurrentPosition());
    }

    public void forwardSong(int currentPosition) {
        //moves the song forward
        finalTime = player.getDuration();
        startTime = currentPosition;

        int temp = (int) startTime;

        if ((temp + INTERVAL) <= finalTime) {
            startTime = startTime + INTERVAL;
            player.seekTo((int) startTime);
        } else {
            //end of the song
            stopSong();
        }
    }

    public void backwardSong() {
        //moves the song backward
        finalTime = player.getDuration();
        startTime = player.getCurrentPosition();

        int temp = (int) startTime;

        if ((temp - INTERVAL) > 0) {
            startTime = startTime - INTERVAL;
            player.seekTo((int) startTime);
        } else {
            //beginning of the song
            player.start();
        }
    }

    public void pauseSong() {
        //pause the player
        if (player.isPlaying()) {
            player.pause();
            Constants.SONG_PAUSED = true;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            //player.stop();
            player = null;
        }
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //when the song completed playing
        mp.seekTo(0);
        listener.onCompletion(currentSong);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        player.release();
        mp.release();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
    }

    public PlayerListener getListener() {
        return listener;
    }

    public void setListener(PlayerListener listener) {
        this.listener = listener;
    }

    public class MusicBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    public interface PlayerListener {
        void onCompletion(Song song);
    }

    public long getSongCurrentPosition() {
        long position = 0;
        try {
            position = player.getCurrentPosition();
        } catch (Exception e) {
            Log.e(LOG, "getSongCurrentPosition", e);
            position = 0;
        }

        return position;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (currentVersionSupportLockScreenControls) {
            RegisterRemoteClient();
        }

        try {
            Constants.SONG_CHANGE_HANDLER = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {

                    try {
                        playSong();
                        newNotification();
                        PlayerFragment.changeUI(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            Constants.PLAY_PAUSE_HANDLER = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    String message = (String) msg.obj;
                    if (player == null)
                        return false;
                    if (message.equalsIgnoreCase(getResources().getString(R.string.play))) {
                        if (currentVersionSupportLockScreenControls) {
                            //remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                        }
                        playSong();
                        Constants.SONG_PAUSED = false;
                    } else if (message.equalsIgnoreCase(getResources().getString(R.string.pause))) {
                        Constants.SONG_PAUSED = true;
                        if (currentVersionSupportLockScreenControls) {
                            //remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                        }
                        pauseSong();
                    } else if (message.equalsIgnoreCase(getResources().getString(R.string.stop))) {
                        stopSong();
                    }
                    PlayerFragment.changeUI(null);
                    newNotification();
                    try {
                        //MainActivity.changeButton();
                        PlayerFragment.changeUI(null);
                    } catch (Exception e) {
                    }

                    return false;
                }
            });

        } catch (Exception e) {
            Log.e(LOG, "onStartCommand: ", e);
        }
        return START_STICKY;
    }

    /**
     * Notification
     * Custom Bignotification is available from API 16
     */
    private void newNotification() {
        if (Constants.SONGS_QUEUE != null && !Constants.SONGS_QUEUE.isEmpty()) {
            Song song = (Song) Constants.SONGS_QUEUE.getLast();
            String songName = song.getName();
            String albumName = song.getAlbum().getName();
            RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification);
            RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);

            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_music)
                    .setContentTitle(songName).build();

            setListeners(simpleContentView);
            setListeners(expandedView);

            notification.contentView = simpleContentView;
            if (currentVersionSupportBigNotification) {
                notification.bigContentView = expandedView;
            }

            try {
                long albumId = song.getAlbum().getID();
                Bitmap albumArt = UtilFunctions.getAlbumArt(getApplicationContext(), albumId, 100);
                if (albumArt != null) {
                    notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
                    if (currentVersionSupportBigNotification) {
                        notification.bigContentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
                    }
                } else {
                    notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.cover_album_default);
                    if (currentVersionSupportBigNotification) {
                        notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.cover_album_default);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Constants.SONG_PAUSED) {
                notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
                notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

                if (currentVersionSupportBigNotification) {
                    notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
                    notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
                }
                Constants.PLAY_FLAG = false;
            } else {
                notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

                if (currentVersionSupportBigNotification) {
                    notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                    notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
                }

                Constants.PLAY_FLAG = true;
            }

            notification.contentView.setTextViewText(R.id.textSongName, songName);
            notification.contentView.setTextViewText(R.id.textAlbumName, albumName);
            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setTextViewText(R.id.textSongName, songName);
                notification.bigContentView.setTextViewText(R.id.textAlbumName, albumName);
            }
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            startForeground(NOTIFICATION_ID, notification);

            /*Pass song name,Album name ,False*/
            MainActivity.setDataToPlayer(song.getName(), song.getAlbum(), getApplicationContext());
            MainActivity.changeButtons();
        }
    }

    /**
     * Notification click listeners
     *
     * @param view
     */
    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);

    }

    private void RegisterRemoteClient() {
        remoteComponentName = new ComponentName(getApplicationContext(), new NotificationBroadcast().ComponentName());
        try {
            if (remoteControlClient == null) {
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);
            }
            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                            RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        } catch (Exception ex) {
        }
    }
}
