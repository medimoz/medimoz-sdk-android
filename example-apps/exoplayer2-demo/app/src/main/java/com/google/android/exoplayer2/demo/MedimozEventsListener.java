package com.google.android.exoplayer2.demo;

import android.util.Log;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.DefaultEventListener;

import medimoz.sdk.MZEvents;


/**
 * Implementa captura de eventos para medición
 * de audiencia con Medimoz para Exoplayer
 */
public class MedimozEventsListener extends DefaultEventListener {

  private final String LOGGING_TAG = "Medimoz: MZEvents: " + this.getClass().toString();
  private MZEvents medimoz;

  public MedimozEventsListener(MZEvents medimoz) {
    this.medimoz = medimoz;
  }

  @Override
  public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
    if (playWhenReady && playbackState == Player.STATE_READY) {
      // media actually playing

      if (null != medimoz) {
        medimoz.timerStart();
      }
      Log.d(LOGGING_TAG, "play");

    } else if (playWhenReady) {
      // might be idle (plays after prepare()),
      // buffering (plays when data available)
      // or ended (plays when seek away from end)

      if (null != medimoz) {
        medimoz.timerClear();
      }
      Log.d(LOGGING_TAG, "pause-alt");

    } else {
      // player paused in any state

      if (null != medimoz) {
        medimoz.timerClear();
      }

      Log.d(LOGGING_TAG, "pause");

    }
  }


  @Override
  public void onPlayerError(ExoPlaybackException error) {
    Log.d(LOGGING_TAG, "error");

    if (null != medimoz) {
      medimoz.timerClear();
    }
  }
}
