package com.altb.musicapp.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.altb.musicapp.Activities.MainActivity;
import com.altb.musicapp.Model.MusicData;
import com.altb.musicapp.Model.Song;
import com.altb.musicapp.R;
import com.altb.musicapp.Services.PlayerService;
import com.altb.musicapp.Utils.Controls;
import com.altb.musicapp.Utils.Constants;
import com.altb.musicapp.Utils.UtilFunctions;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;

import java.util.Calendar;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import app.minimize.com.seek_bar_compat.SeekBarCompat;


/**
 * Class to deal with all related operations of music player.
 * <p/>
 * Created by Ady
 */
public class PlayerFragment extends Fragment implements PlayerService.PlayerListener, Serializable {

    private static final String LOG = "PlayerFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SONG_POSITION = "songName";
    private static final String SONG_LIST = "songList";
    private static final String PLAY_SERVICE = "playService";
    private static final String OPEN_FRAGMENT = "openFragment";

    private static TextView songNameView;
    private static TextView bottomText;

    int NOTIFICATION_ID = 1111;

    private PlayerService playerService;

    boolean longClick = false;
    private boolean repeatOne = false;
    private boolean repeatAll = false;
    private boolean openFragment = false;

    private static Button play, repeatOneButton, repeatAllButton, addToPlayListButton,
            next, previous, settings, editButton;

    private SeekBarCompat songProgressBar;

    private static TimeTask timeTask;
    private TextView totalTime;
    private TextView elapsedTime;
    private static ImageView coverAlbum;

    private static View v;

    private List<MusicData> songList;
    private int songPosition = -1;

    //volume controller
    private static SeekBar volumeSeekbar = null;
    private static AudioManager audioManager = null;
    private LinearLayout settingsLayout;
    private static AlertDialog alertDialog;
    private static RadioGroup radioGroup;

    //edit Song
    private static EditText editTitle = null;
    private static EditText editArtist = null;
    private static EditText editAlbum = null;
    private static EditText editGenre = null;

    public PlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param songPosition Parameter 1.
     * @return A new instance of fragment PlayerFragment.
     */
    public static PlayerFragment newInstance(int songPosition, List<MusicData> songList) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putInt(SONG_POSITION, songPosition);
        args.putSerializable(SONG_LIST, (Serializable) songList);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlayerFragment.
     */
    public static PlayerFragment newInstance(boolean openFragment) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putBoolean(OPEN_FRAGMENT, openFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.hidePlayer();
        if (getArguments() != null) {
            songPosition = getArguments().getInt(SONG_POSITION);
            songList = (List<MusicData>) getArguments().getSerializable(SONG_LIST);
            playerService = ((MainActivity) getContext()).getPlayerService();
            openFragment = getArguments().getBoolean(OPEN_FRAGMENT);
        }

        init();

        if (!openFragment) {

            if (playerService != null) {
                playerService.setListener(this);

                //starts playing the selected song, the last song in queue
                if (songPosition > -1) {
                    playerService.setSong(Constants.SONG_NUMBER);
                    //always add the song to the queue
                    Song song = (Song) songList.get(songPosition);
                    if (!Constants.SONGS_QUEUE.contains(song)) {
                        Constants.SONGS_QUEUE.addFirst(song);
                    } else {
                        Constants.SONGS_QUEUE.remove(song);
                        Constants.SONGS_QUEUE.addFirst(song);
                    }

                } else {
                    //when play all is selected for example

                    if (!songList.isEmpty()) {
                        Constants.SONGS_QUEUE.clear();
                        Constants.SONGS_QUEUE_RECENT_PLAYED.clear();
                        //reverse the list to maintain the order added or shuffled for example.
                        Collections.reverse(songList);
                        for (MusicData song : songList) {
                            if (!Constants.SONGS_QUEUE.contains(song)) {
                                Constants.SONGS_QUEUE.addFirst(song);
                            }
                        }
                    }
                }

                //pass list
                playerService.setSongList(Constants.SONGS_QUEUE);
                if (!Constants.SONGS_QUEUE.isEmpty()) {
                    Constants.PLAY_FLAG = true;
                    Constants.SONG_PAUSED = false;
                    playerService.playSong();

                /*Pass song name,Album name ,False*/
                    Song song = (Song) Constants.SONGS_QUEUE.getLast();
                    MainActivity.setDataToPlayer(song.getName(), song.getAlbum(), getContext());
                }
            }
        }

        timeTask = new TimeTask();

    }

    private void init() {
        // Handler to update UI timer, progress bar etc,.
        Constants.PROGRESSBAR_HANDLER = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Integer i[] = (Integer[]) msg.obj;

                // Displaying Total Duration time
                totalTime.setText("" + UtilFunctions.milliSecondsToTimer(i[0]));
                // Displaying time completed playing
                elapsedTime.setText("" + UtilFunctions.milliSecondsToTimer(i[1]));

                // Updating progress bar
                int progress = i[2]; //(int) (UtilFunctions.getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                songProgressBar.setProgress(progress);
            }
        };

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    public void playSong(View view) {

        // set Progress bar values
        songProgressBar.setProgress(0);
        songProgressBar.setMax(100);

        changeUI(view);
        Controls.playControl(getActivity());

        updateProgressBar();
    }

    public void playSong(View view, boolean isNextOrPrevious) {
        changeUI(view);
        Controls.playControl(getActivity());
    }

    public void stopSong(View view) {
        playerService.stopSong();
    }

    public void pauseSong(View view) {
        Controls.pauseControl(getActivity());
    }

    public void forwardSong(View view) {
        playerService.forwardSong();
    }

    public void backwardSong(View view) {
        playerService.backwardSong();
    }

    public void nextSong(View view) {
        Controls.nextControl(getActivity());
    }

    public void previousSong(View view) {
        Controls.previousControl(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_players, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(null);
        actionBar.show();

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        /*Field are used to set time when music is playing*/
        totalTime = (TextView) v.findViewById(R.id.t1);
        elapsedTime = (TextView) v.findViewById(R.id.t2);
        final float startPos = totalTime.getX();

        songProgressBar = (SeekBarCompat) v.findViewById(R.id.materialSeekBar);
        songProgressBar.setProgressBackgroundColor(Color.WHITE);

        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeTimePosition(startPos, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // remove message Handler from updating progress bar
                Constants.PROGRESSBAR_HANDLER.removeCallbacks(timeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Constants.PROGRESSBAR_HANDLER.removeCallbacks(timeTask);
                Song song = (Song) Constants.SONGS_QUEUE.getLast();
                int totalDuration = Integer.parseInt(song.getDuration());
                int currentPosition = UtilFunctions.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                playerService.forwardSong(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        });


        // screen fields
        songNameView = (TextView) v.findViewById(R.id.songNameTextView);
        bottomText = (TextView) v.findViewById(R.id.bottomText);
        addToPlayListButton = (Button) v.findViewById(R.id.heart);
        addToPlayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.PLAYLIST_FLAG) {
                    addToPlayListButton.setBackgroundResource(R.drawable.heart_dark);
                    Constants.PLAYLIST_FLAG = false;
                } else {
                    addToPlayListButton.setBackgroundResource(R.drawable.heart);
                    Constants.PLAYLIST_FLAG = true;
                }

            }
        });

        play = (Button) v.findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song song = (Song) Constants.SONGS_QUEUE.getLast();
                if (Constants.PLAY_FLAG) {
                    Constants.PLAY_FLAG = false;
                    //pause song and show play button
                    pauseSong(v);
                           /*Pass song name,Album name ,true*/
                    MainActivity.setDataToPlayer(song.getName(), song.getAlbum(), getContext());


                } else {
                    Constants.PLAY_FLAG = true;
                    //play song and show pause button
                    playSong(v);

                    /*Pass song name,Album name ,False*/
                    MainActivity.setDataToPlayer(song.getName(), song.getAlbum(), getContext());
                }

            }
        });


        next = (Button) v.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!longClick) {
                    nextSong(v);
                    play.setBackgroundResource(R.drawable.pause);
                    Constants.PLAY_FLAG = true;
                }
                longClick = false;
            }
        });
        next.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClick = true;
                forwardSong(v);

                return false;
            }
        });

        previous = (Button) v.findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!longClick) {
                    previousSong(v);
                }
                longClick = false;
            }
        });
        previous.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClick = true;
                backwardSong(v);

                return false;
            }
        });

        repeatAllButton = (Button) v.findViewById(R.id.repeatAll);
        repeatOneButton = (Button) v.findViewById(R.id.repeatOne);

        repeatAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (repeatAll) {
                    Constants.REPEAT_ALL_FLAG = false;
                    repeatAllButton.setBackgroundResource(R.drawable.refresh);
                } else {
                    Constants.REPEAT_ALL_FLAG = true;
                    repeatAllButton.setBackgroundResource(R.drawable.refresh_red_red);
                    //turn off repeat one
                    repeatOneButton.setBackgroundResource(R.drawable.repeat);
                }
                repeatButtonClicked(v);
            }
        });

        repeatOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatOne) {
                    Constants.REPEAT_ONE_FLAG = false;
                    repeatOneButton.setBackgroundResource(R.drawable.repeat);
                } else {
                    Constants.REPEAT_ONE_FLAG = true;
                    repeatOneButton.setBackgroundResource(R.drawable.repeatone);
                    //turn off repeat all
                    repeatAllButton.setBackgroundResource(R.drawable.refresh);
                }
                repeatButtonClicked(v);
            }
        });


        final FrameLayout settingsLayout = new FrameLayout(getContext());
        //open dialogbox
        //create a dialog screen
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set dialog message
        alertDialogBuilder
                //.setMessage(sb.toString())
                .setView(settingsLayout);

        // create alert dialog
        alertDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settings.setBackgroundResource(R.drawable.settings);
                Constants.SETTINGS_FLAG = false;

                EditTags();

                dialog.dismiss();

            }
        });
        alertDialog = alertDialogBuilder.create();
        inflater = alertDialog.getLayoutInflater();

        View dialoglayout = inflater.inflate(R.layout.settings_layout, settingsLayout);

        editTitle = (EditText) dialoglayout.findViewById(R.id.EditTitle);
        editArtist = (EditText) dialoglayout.findViewById(R.id.EditArtist);
        editAlbum = (EditText) dialoglayout.findViewById(R.id.EditAlbum);
        editGenre = (EditText) dialoglayout.findViewById(R.id.EditGenre);
        Song song = (Song) Constants.SONGS_QUEUE.getLast();
        editTitle.setText(song.getTitle());
        editArtist.setText(song.getAlbum().getArtist().getName());
        editAlbum.setText(song.getAlbum().getName());
        editGenre.setText(song.getGenre().getName());

        volumeSeekbar = (SeekBar) dialoglayout.findViewById(R.id.volumeSeekbar);
        radioGroup = (RadioGroup) dialoglayout.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //every time that changes the time, set another start time to count
                Constants.TIME_START_PLAYING = new Date();
                checkSleepTime(getContext());
            }
        });

        updateVolumeBar(0);

        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }
        });

        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                boolean result = false;
                //when pressing back button for example
                if (keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                        keyCode != KeyEvent.KEYCODE_VOLUME_UP) {
                    result = false;
                } else {
                    //just for volume buttons
                    MainActivity.onVolumeKeyPressed(keyCode);
                    result = true;
                }
                return result;
            }
        });


        settings = (Button) v.findViewById(R.id.settingsButton);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.SETTINGS_FLAG) {
                    settings.setBackgroundResource(R.drawable.settings);
                    Constants.SETTINGS_FLAG = false;
                } else {
                    settings.setBackgroundResource(R.drawable.setting_red);
                    Constants.SETTINGS_FLAG = true;

                    // show the dialog
                    alertDialog.show();
                }

            }
        });

        editButton = (Button) v.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.EDIT_FLAG) {
                    editButton.setBackgroundResource(R.drawable.tick);
                    Constants.EDIT_FLAG = false;
                } else {
                    editButton.setBackgroundResource(R.drawable.tick_red);
                    Constants.EDIT_FLAG = true;
                }
            }
        });

        changeUI(v);

        return v;
    }


    private void changeTimePosition(float startPos, int progress) {
        /*Used to chnage time postion*/
        totalTime.setX(startPos + (progress * 5));
        elapsedTime.setX(startPos + (progress * 5));
    }

    public static void changeUI(View v) {
        Song song = (Song) Constants.SONGS_QUEUE.getLast();

        songNameView.setText(song.getTitle());

        bottomText.setText(song.getComposer());

        updateSongTimesPlayed(song);

        updateProgressBar();

        updateCoverAlbum(song);

        PlayerFragment.changeButtons();

    }

    private static void updateCoverAlbum(Song songData) {
        coverAlbum = (ImageView) v.findViewById(R.id.coverAlbum);
        if (songData.getAlbum().getCoverAlbumBitmap() != null) {
            coverAlbum.setImageBitmap(songData.getAlbum().getCoverAlbumBitmap());
        } else {
            String uri = songData.getAlbum().getCoverAlbum();
            int resources = coverAlbum.getResources().getIdentifier(uri, null, coverAlbum.getContext().getPackageName());
            coverAlbum.setImageResource(resources);
        }
    }

    private static void updateSongTimesPlayed(Song song) {
        song.increaseTimesPlayed();
        Log.i("PLAYERFRAGMENT", "Song: " + song.getTitle() + " played " + song.getTimesPlayed() + " times.");
    }

    private void repeatButtonClicked(View v) {
        // Check which radio button was clicked
        switch (v.getId()) {
            case R.id.repeatAll:
                if (Constants.REPEAT_ALL_FLAG) {
                    repeatAll = true;
                    repeatOne = false;
                    playerService.setRepeat(true);
                } else {
                    repeatAll = false;
                    repeatOne = false;
                    playerService.setRepeat(false);
                }
                break;
            case R.id.repeatOne:
                if (Constants.REPEAT_ONE_FLAG) {
                    repeatOne = true;
                    repeatAll = false;
                    playerService.setRepeat(false);
                } else {
                    repeatOne = false;
                    repeatAll = false;
                    playerService.setRepeat(false);
                }
                break;
            default:
                repeatAll = false;
                repeatOne = false;
                playerService.setRepeat(null);
        }
    }

    private void EditTags() {
        Song song = (Song) Constants.SONGS_QUEUE.getLast();

        final String pathdata = song.getPath();
//        stopSong(v);
        File src = new File(pathdata);
        MusicMetadataSet src_set = null;
        try {
            src_set = new MyID3().read(src);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } // read metadata

        if (src_set == null) // perhaps no metadata
        {
            Log.i("NULL", "NULL");
        }
        else
        {
            try {
                MusicMetadata meta = new MusicMetadata("name");
                meta.setSongTitle(editTitle.getText().toString());
                meta.setArtist(editArtist.getText().toString());
                meta.setGenre(editGenre.getText().toString());
                meta.setAlbum(editAlbum.getText().toString());

                new MyID3().update(src, src_set, meta);
                MediaScannerConnection.scanFile(getContext(), new String[]{src.getAbsolutePath()},
                        new String[]{URLConnection.getFileNameMap().getContentTypeFor(src.getPath())}, null);

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ID3WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }  // write updated metadata
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //to stop the thread
        Constants.PROGRESSBAR_HANDLER.removeCallbacks(timeTask);
    }

    @Override
    public void onCompletion(Song song) {
        if (repeatAll) {
            // repeat all
            Log.i("onCompletion", "Repeat All");
            nextSong(v);
        } else if (repeatOne) {
            // repeat one
            Log.i("onCompletion", "Repeat one");
            playSong(v);
        } else {
            Log.i("onCompletion", "no repeat");

            //change the button to play again
            Constants.PLAY_FLAG = false;
            PlayerFragment.changeButtons();
            MainActivity.changeButtons();

            //update seekbar
            songProgressBar.setProgress(0);
            changeTimePosition(0, 0);
            updateProgressBar();

            //check for stop settings
            checkSleepTime(getContext());
        }

    }

    /**
     * Update timer on seekbar
     */
    public static void updateProgressBar() {
        //timeTask.setSong(song);
        Constants.PROGRESSBAR_HANDLER.postDelayed(timeTask, 100);
    }

    /**
     * Background Runnable thread to update seekbar and timers for song
     */
    private class TimeTask implements Runnable {

        @Override
        public void run() {
            Song song = (Song) Constants.SONGS_QUEUE.getLast();
            long totalDuration = Long.parseLong(song.getDuration());
            long currentDuration = playerService.getSongCurrentPosition();

            // Displaying Total Duration time
            totalTime.setText("" + UtilFunctions.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            elapsedTime.setText("" + UtilFunctions.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (UtilFunctions.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            Constants.PROGRESSBAR_HANDLER.postDelayed(this, 100);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isServiceRunning = UtilFunctions.isServiceRunning(PlayerService.class.getName(), getActivity());
        if (isServiceRunning) {
            //changeUI(null);
        }
    }

    private static void changeButtons() {
        if (Constants.PLAY_FLAG) {
            //show play button
            play.setBackgroundResource(R.drawable.pause);
        } else {
            //show pause button
            play.setBackgroundResource(R.drawable.play);
        }
    }

    /**
     * Increase/Decrease volume by units
     *
     * @param unit
     */
    public static void updateVolumeBar(int unit) {
        if (audioManager != null) {
            int current = audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int volume = current;
            if (current + unit > maxVolume) {
                volume = maxVolume;
            } else if (current + unit < 0) {
                volume = 0;
            } else {
                volume = current + unit;
            }

            volumeSeekbar.setMax((int) maxVolume);
            volumeSeekbar.setProgress(volume);
        }
    }

    public static void checkSleepTime(Context context) {
        //radiobuttons
        if (radioGroup != null) {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            Calendar cal = null;
            switch (selectedId) {
                case R.id.mix:
                    //check recently played same size as queue
                    if (Constants.SONGS_QUEUE_RECENT_PLAYED.size() ==
                            Constants.SONGS_QUEUE.size()) {
                        Controls.stopControl(context);
                    }
                    break;
                case R.id.min30:
                    cal = Calendar.getInstance();
                    cal.setTime(Constants.TIME_START_PLAYING);
                    Log.i(LOG, "time started playing: " + cal.getTime());
                    cal.add(Calendar.MINUTE, Constants.TIMER_30_MIN);
                    Log.i(LOG, "time + : " + Constants.TIMER_30_MIN + ": " + cal.getTime());
                    Date now = new Date();
                    Log.i(LOG, "time checked: " + now);

                    if (now.after(cal.getTime())) {
                        Controls.stopControl(context);
                    }
                    break;
                case R.id.min60:
                    cal = Calendar.getInstance();
                    cal.setTime(Constants.TIME_START_PLAYING);
                    cal.add(Calendar.MINUTE, Constants.TIMER_60_MIN);
                    if (new Date().after(cal.getTime())) {
                        Controls.stopControl(context);
                    }
                    break;
                case R.id.never:
                default:
                    Log.i(LOG, "No timer selected.");

            }
        }
    }

}
