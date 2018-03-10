package medimoz.sdk;

import org.piwik.sdk.Piwik;
import org.piwik.sdk.Tracker;
import org.piwik.sdk.TrackerConfig;
import org.piwik.sdk.extra.TrackHelper;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;


public class MZEvents {
    private int siteId;
    private String trackingUrl;
    private Tracker tracker;
    private Context context;

    private final int TIMER_STEP = 60;
    private boolean timerPlayToggle = true;
    private Handler timerHandler;


    public MZEvents(int siteId, String trackingUrl, Context context) {
        this.siteId = siteId;
        this.trackingUrl = trackingUrl;
        this.context = context;

        this.tracker = this.getTracker();
        this.timerSetup();
    }

    public synchronized Tracker getTracker() {
        if (tracker == null) {
            tracker = Piwik.getInstance(context).newTracker(TrackerConfig.createDefault(trackingUrl, siteId));
            tracker.setDispatchInterval(0);
            Log.i("Medimoz", "MZEvents: Inicializa tracker: "+ trackingUrl + " : " + siteId);
        }
        return tracker;
    }

    public void reset() {
        this.timerClear();
    }

    public void start() {
        this.timerClear();
    }

    public void pause() {
        this.timerClear();
    }


    public void sendEvent(String category, String action, String name) {
        //TrackHelper.track().screen("screen-1").title("title-1").with(tracker);
        // Envía evento genérico
        TrackHelper.track().event(category, action).name(name).with(tracker);
    }

    public void sendTimeEvent(String contentName, String time) {
        // Envía evento time
        sendEvent("video", time, contentName);
    }



    abstract private class TimerRunnable implements Runnable {
        protected int time = 0;
        protected TimerRunnable(int time) {
            this.time = time;
        }
    }

    private void timerSetup() {
        this.timerHandler = new Handler();
    }

    public void timerStart() {
        if(timerPlayToggle) {
            int time = 0;
            timerHandler.postDelayed(new TimerRunnable(time) {
                @Override
                public void run() {
                    this.time += TIMER_STEP;
                    String value = "time-" + String.valueOf(this.time);
                    Log.i("Medimoz", "MZEvents: Reporta tiempo: " + value);
                    sendTimeEvent("contenido-no-identificado", value);
                    timerHandler.postDelayed(this, TIMER_STEP * 1000);
                }
            }, TIMER_STEP * 1000);
            timerPlayToggle = false;
        }
    }

    public void timerClear() {
        timerPlayToggle = true;
        timerHandler.removeCallbacksAndMessages(null);
    }

}
