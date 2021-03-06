package com.ooyala.sample.players;

import android.os.Bundle;
//import android.support.multidex.MultiDex;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.SampleApplication;

import medimoz.sdk.MZEvents;


/**
 * This activity illustrates how you can play basicPlayback Video
 * you can also play Ooyala and VAST advertisements programmatically
 * through the SDK
 */
public class BasicPlaybackVideoPlayerActivity extends AbstractHookActivity {


    @Override
    void completePlayerSetup(boolean asked) {
        if (asked) {
            player = new OoyalaPlayer(pcode, new PlayerDomain(domain));
            playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

            playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
            player.addObserver(this);

            if (player.setEmbedCode(embedCode)) {
                //Uncomment for Auto Play
                //player.play();
            } else {
                Log.e(TAG, "Asset Failure");
            }

            // Handle to the tracking component
            medimoz = ((SampleApplication) getApplication()).getMedimoz();
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_simple_layout);
        completePlayerSetup(asked);

        String activity = this.getClass().getSimpleName();
        Log.i("Medimoz", "App: Inicia actividad: " + activity);
    }
}


