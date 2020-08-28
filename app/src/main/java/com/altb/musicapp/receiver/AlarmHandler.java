package com.altb.musicapp.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.altb.musicapp.Fragments.PlayerFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Class that handles the alarm, notifications
 * Created by adypaulino on 2016-08-15.
 */
public class AlarmHandler extends BroadcastReceiver {

    public AlarmHandler() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //the task to run when the alarm starts

        PlayerFragment.checkSleepTime(context);

    }

}
