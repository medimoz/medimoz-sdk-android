package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

import java.util.Observable;
import java.util.Observer;

import medimoz.sdk.MZEvents;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * This class asks permission for WRITE_EXTERNAL_STORAGE. We need it for automation hooks
 * as we need to write into the SD card and automation will parse this file.
 */
public abstract class AbstractHookActivity extends Activity implements Observer {
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    final String TAG = this.getClass().toString();

    private final SDCardLogcatOoyalaEventsLogger log = new SDCardLogcatOoyalaEventsLogger();

    protected String embedCode;
    protected String pcode;
    protected String domain;
    protected OoyalaPlayerLayoutController playerLayoutController;
    protected OoyalaPlayerLayout playerLayout;

    // Handle to the player instance
    OoyalaPlayer player;
    // Handle to the tracking component
    MZEvents medimoz;

    private boolean writePermission = false;
    boolean asked = false;

    // complete player setup after we asked for permission to write into external storage
    abstract void completePlayerSetup(final boolean asked);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            writePermission = true;
            asked = true;
        }

        embedCode = getIntent().getExtras().getString("embed_code");
        pcode = getIntent().getExtras().getString("pcode");
        domain = getIntent().getExtras().getString("domain");

        // Medimoz: Life cycle linking
        if (null != medimoz) {
            medimoz.timerClear();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            asked = true;
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                writePermission = true;
            }
            completePlayerSetup(asked);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != player) {
            player.suspend();
        }

        // Medimoz: Life cycle linking
        if (null != medimoz) {
            medimoz.timerClear();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != player) {
            player.resume();
        }

        // Medimoz: Life cycle linking
        if (null != medimoz) {
            medimoz.timerClear();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // Medimoz: Life cycle linking
        // Listens to the video events in the player
        final String arg1 = OoyalaNotification.getNameOrUnknown(arg);
        if (arg1.equals(OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME)) {
            return;
        }

        String text = "ooyala Notification Received: " + arg1 + " - state: " + player.getState();
        Log.d(TAG, text);

        if (null != medimoz && arg1.equals(OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME)) {
            OoyalaPlayer.State currentState = player.getState();
            switch (currentState) {
                case PLAYING:
                    medimoz.timerStart();
                    break;
                case PAUSED:
                    medimoz.timerClear();
                    break;
                case ERROR:
                    medimoz.timerClear();
                    break;
                case COMPLETED:
                    medimoz.timerClear();
                    break;
                case READY:
                    medimoz.timerClear();
                    break;
            }
        }
    }
}