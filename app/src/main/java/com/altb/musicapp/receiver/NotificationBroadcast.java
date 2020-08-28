package com.altb.musicapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.altb.musicapp.Activities.MainActivity;
import com.altb.musicapp.Services.PlayerService;
import com.altb.musicapp.Utils.Controls;
import com.altb.musicapp.Utils.Constants;

/**
 * Created by ady on 2016-08-12.
 */
public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if(!Constants.PLAY_FLAG){
                        Controls.pauseControl(context);
                    }else{
                        Controls.playControl(context);
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT");
                    Controls.nextControl(context);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS");
                    Controls.previousControl(context);
                    break;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    Log.d("TAG", "TAG: KEYCODE_VOLUME_UP");
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    Log.d("TAG", "TAG: KEYCODE_VOLUME_DOWN");
                    break;
            }
        }  else{
            if (intent.getAction().equals(PlayerService.NOTIFY_PLAY)) {
                Controls.playControl(context);
            } else if (intent.getAction().equals(PlayerService.NOTIFY_PAUSE)) {
                Controls.pauseControl(context);
            } else if (intent.getAction().equals(PlayerService.NOTIFY_NEXT)) {
                Controls.nextControl(context);
            } else if (intent.getAction().equals(PlayerService.NOTIFY_DELETE)) {
                stopActions(context);
            }else if (intent.getAction().equals(PlayerService.NOTIFY_PREVIOUS)) {
                Controls.previousControl(context);
            }
        }
    }

    public static void stopActions(Context context){
        MainActivity.getMainActivity().stopIntent();
        Intent in = new Intent(context, MainActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(in);
    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}
