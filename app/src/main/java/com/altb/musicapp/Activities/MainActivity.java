package com.altb.musicapp.Activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.altb.musicapp.Fragments.DetailFragment;
import com.altb.musicapp.Fragments.MainFragment;
import com.altb.musicapp.Fragments.MyLibrary;
import com.altb.musicapp.Fragments.PlayerFragment;
import com.altb.musicapp.Model.Album;
import com.altb.musicapp.Model.Artist;
import com.altb.musicapp.Model.Genre;
import com.altb.musicapp.Model.MusicData;
import com.altb.musicapp.Model.Song;
import com.altb.musicapp.R;
import com.altb.musicapp.Services.BackgroungService;
import com.altb.musicapp.Services.PlayerService;
import com.altb.musicapp.Utils.Constants;
import com.altb.musicapp.Utils.Controls;
import com.altb.musicapp.Utils.UtilFunctions;
import com.altb.musicapp.receiver.AlarmHandler;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/*This class will MainActivity which will manage all fragments
*
* Created by Lalit Bagga
* */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG = "Main";
    private final int READEXTERNALSTORAGE = 101;
    private Boolean navBarToggle = true;
    private String[] drawerItems = {"Home", "Library"};
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<String> mAdapter;
    private RelativeLayout mDrawerRelativeLayout;
    private static RelativeLayout playerLayout, albumImageRelativeLayout;
    private static Button play;
    private static TextView tSong, tAlbum;
    private static ImageView profileImage;
    private static Boolean showPlayerFlag = false;
    private FragmentManager fm;

    //player variables
    private PlayerService playerService;
    private Intent playIntent;
    private boolean musicBound = false;

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    private static void setMainActivity(MainActivity mainActivity) {
        MainActivity.mainActivity = mainActivity;
    }

    private static MainActivity mainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*As soon as Activity start ask permission */
        methodRequiresTwoPermission();
        setContentView(R.layout.activity_main);
        setDrawerList();
        setMainActivity(this);

        fm = getSupportFragmentManager();
        MainFragment mainFragment = (MainFragment) fm.findFragmentById(R.id.main_container);
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
            fm.beginTransaction().add(R.id.main_container, mainFragment).commit();
        }
        playerLayout = (RelativeLayout) findViewById(R.id.playingLayout);
        play = (Button) findViewById(R.id.play);
        tSong = (TextView) findViewById(R.id.textView2);
        tAlbum = (TextView) findViewById(R.id.textView3);
        profileImage = (ImageView) findViewById(R.id.profile_image);

        tSong.setOnClickListener(this);
        tAlbum.setOnClickListener(this);

        /*Hide small player layout as soon as activity starts*/
        hidePlayer();


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Pause or play  song and also change button image */
                if (Constants.PLAY_FLAG) {
                    Controls.pauseControl(getApplicationContext());
                } else {
                    Controls.playControl(getApplicationContext());
                }

                MainActivity.changeButtons();
            }
        });


    }

    public static void changeButtons() {
        if (Constants.PLAY_FLAG) {
            //show play button
            play.setBackgroundResource(R.drawable.pause);
        } else {
            //show pause button
            play.setBackgroundResource(R.drawable.play);
        }
    }

    /*Go to player fragment on click of textSong,txtAlbum*/
    @Override
    public void onClick(View v) {
       /*go to player Fragnment */
        fm.beginTransaction().replace(R.id.main_container, PlayerFragment.newInstance(true)).addToBackStack(null).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.MusicBinder binder = (PlayerService.MusicBinder) service;
            //get service
            playerService = binder.getService();

            musicBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public PlayerService getPlayerService() {
        return playerService;
    }

    public Intent getPlayerIntent() {
        return playIntent;
    }

    //This is called when on any card has been clicked and passed details of that card
    public void loadDetailScreen(MusicData data, int position) {
        DetailFragment detailFragment = new DetailFragment();

        if (data instanceof Album) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                    DetailFragment.newInstance(data)).addToBackStack(null).commit();
        } else if (data instanceof Song) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                    PlayerFragment.newInstance(position, detailFragment.getAllSongs(this))).addToBackStack(null).commit();
        } else if (data instanceof Artist) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                    DetailFragment.newInstance(data)).addToBackStack(null).commit();
        } else if (data instanceof Genre) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                    DetailFragment.newInstance(data)).addToBackStack(null).commit();
        }
    }

    private void setDrawerList() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.list_view_drawer);
        mDrawerRelativeLayout = (RelativeLayout) findViewById(R.id.left_dra);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.DrawerOpen,  /* "open drawer" description */
                R.string.DrawerClose  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };


        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(mDrawerToggle);
         /*setAdapter*/
        mAdapter = new ArrayAdapter<String>(this, R.layout.nav_list_whitetetxt, drawerItems);
        drawerListView.setAdapter(mAdapter);

        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fm = getSupportFragmentManager();
                if (position == 0) {
                    Fragment fragment = new MainFragment();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawer(mDrawerRelativeLayout);
                } else if (position == 1) {
                    //Go to my Library
                    Fragment fragment = new MyLibrary();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawer(mDrawerRelativeLayout);
                } else {
                    //Go to settings
                }
            }
        });


        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.center_actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Home");


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    //This function is used to mostly when user go to detail fragnment
    public void toggleNavBar() {
/*        if (navBarToggle) {
            getSupportActionBar().hide();
            navBarToggle = false;
        } else {
            getSupportActionBar().show();
            navBarToggle = true;
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toggleNavBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerService != null) {
            unbindService(musicConnection);
            stopService(playIntent);
            playerService = null;
            playIntent = null;
            musicBound = false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /*Method will ask permission to read from external storage */
    @AfterPermissionGranted(READEXTERNALSTORAGE)
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //bindMusicService();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.AskPermission),
                    READEXTERNALSTORAGE, perms);
        }
    }

    private void bindMusicService() {
        if (playIntent == null) {
            playIntent = new Intent(this, PlayerService.class);
            boolean connected = this.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            Log.i("BIND SERVICE", "MUSIC Service is connected: " + connected);

            this.startService(playIntent);

            setSleepTimer();
        }
    }

    /*Pass songName,ArtistName and songs status to set data to small player layout*/
    public static void setDataToPlayer(String songName, Album album, Context context) {
        tSong.setText(songName);
        tAlbum.setText(album.getName());
        showPlayerFlag = true;
        if (album.getCoverAlbumBitmap() != null){
            profileImage.setImageBitmap(album.getCoverAlbumBitmap());
        } else {
            int resources = profileImage.getResources().getIdentifier(album.getCoverAlbum(), null,
                    profileImage.getContext().getPackageName());
            profileImage.setImageResource(resources);
        }
        MainActivity.changeButtons();
    }

    /*This function player will hide the layout of small player */
    public static void hidePlayer() {
        playerLayout.setVisibility(View.GONE);
        playerLayout.setAlpha(0.0f);
            /*    Start Animation*/
        playerLayout.animate()
                .translationY(0)
                .alpha(0.0f);
    }

    /*Method is used to show small player layout*/
    public static void showPlayer() {
        if (showPlayerFlag) {
            playerLayout.setVisibility(View.VISIBLE);
            playerLayout.setAlpha(0.0f);
            /*     Start Animation*/
            playerLayout.animate()
                    .translationY(1.0f)
                    .alpha(1.0f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!musicBound) {
            bindMusicService();
        }

        showPlayerFlag = false;
        hidePlayer();
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        onVolumeKeyPressed(keyCode);
        return true;
    }

    public static void onVolumeKeyPressed(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Log.i(LOG, "KEYCODE_VOLUME_DOWN");
            PlayerFragment.updateVolumeBar(-Constants.VOLUME_UNIT);
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Log.i(LOG, "KEYCODE_VOLUME_UP");
            PlayerFragment.updateVolumeBar(Constants.VOLUME_UNIT);
        }
    }

    private void setSleepTimer() {
        //Run the service in background
        Intent serviceIntent = new Intent(this, BackgroungService.class);
        this.startService(serviceIntent);

        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter();

        // Registers the receiver with the new filter
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new AlarmHandler(),
                mStatusIntentFilter);
    }

    /**
     * Used when user sets the sleep time
     */
    public void stopIntent(){
        if (playerService != null) {
            unbindService(musicConnection);
            stopService(playIntent);
            playerService = null;
            playIntent = null;
            musicBound = false;
        }

    }
}
