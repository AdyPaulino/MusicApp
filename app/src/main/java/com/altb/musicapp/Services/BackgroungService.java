package com.altb.musicapp.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.altb.musicapp.Utils.Constants;
import com.altb.musicapp.receiver.AlarmHandler;

/**
 * Created by adypaulino on 2016-08-15.
 */
public class BackgroungService extends IntentService {

    public BackgroungService() {
        super(BackgroungService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve a PendingIntent that will perform a broadcast alarm
        Intent alarmIntent = new Intent(this, AlarmHandler.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Repeating on every 3 minutes interval
        int interval = 1000 * 60 * Constants.TIMER_2_MIN;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                interval, pendingAlarmIntent);

    }
}
