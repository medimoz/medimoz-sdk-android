package medimoz.sdk;

import org.piwik.sdk.Piwik;
import org.piwik.sdk.Tracker;
import org.piwik.sdk.TrackerConfig;
import org.piwik.sdk.extra.TrackHelper;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;


/**
 * Medimoz tracking component
 *
 */
public class MZEvents {
    private int siteId;
    private String trackingUrl;
    private Tracker tracker;
    private Context context;

    /**
     * Number of seconds between consecutive events.
     * Usually 60 for video.
     */
    private final int TIMER_STEP = 60;

    /**
     * Internal timer
     */
    private boolean timerPlayToggle = true;
    private Handler timerHandler;


    /**
     * Initializes the tracking component.
     *
     * @param siteId ID for the site (broadcaster, channel, etc.)
     * @param trackingUrl URL for sending tracking data
     * @param context Application context
     */
    public MZEvents(int siteId, String trackingUrl, Context context) {
        this.siteId = siteId;
        this.trackingUrl = trackingUrl;
        this.context = context;

        this.tracker = this.getTracker();
        this.timerSetup();
    }


    /**
     * Returns the tracking object
     *
     * @return the single tracker instance for the app
     */
    public synchronized Tracker getTracker() {
        if (tracker == null) {
            tracker = Piwik.getInstance(context).newTracker(TrackerConfig.createDefault(trackingUrl, siteId));
            tracker.setDispatchInterval(0);
            tracker.setDispatchGzipped(false);
            Log.i("Medimoz", "MZEvents: Inicializa tracker: "+ trackingUrl + " : " + siteId);
        }
        return tracker;
    }


    /**
     * Resets the tracking timer
     */
    public void reset() {
        this.timerClear();
    }


    /**
     * Starts the tracking timer
     */
    public void start() {
        this.timerClear();
    }


    /**
     * Pauses the tracking timer
     */
    public void pause() {
        this.timerClear();
    }


    /**
     * Sends an event as a tracking record.
     *
     * @param category Category of the event.
     *                 video for live content
     *                 vod for on-demand content
     * @param action A description of the time the event ocurred
     *               Usually time-XXX where XXX is a quantity in seconds,
     *               multiple of the tracking timer step configuration
     * @param name The name of the content being tracked
     */
    public void sendEvent(String category, String action, String name) {
        // Envía evento genérico
        TrackHelper.track().event(category, action).name(name).with(tracker);
    }


    /**
     * Sends a time event
     *
     * @param contentName Name of the content
     * @param time Quantity in seconds
     */
    public void sendTimeEvent(String contentName, String time) {
        // Envía evento time
        sendEvent("video", time, contentName);
    }


    /**
     * Implementation for the timer
     */
    abstract private class TimerRunnable implements Runnable {
        protected int time = 0;
        protected TimerRunnable(int time) {
            this.time = time;
        }
    }


    /**
     * Setups the internal state of the timer
     *
     */
    private void timerSetup() {
        this.timerHandler = new Handler();
    }


    /**
     * Starts the timer
     */
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


    /**
     * Clears the internal state of the timer
     */
    public void timerClear() {
        timerPlayToggle = true;
        timerHandler.removeCallbacksAndMessages(null);
    }


    /**
     * Changes the current ID of the site for tracking
     *
     * @param newId The new ID
     */
    public void changeSiteId(int newId) {
        this.tracker = Piwik.getInstance(context).newTracker(TrackerConfig.createDefault(this.trackingUrl, newId));
        this.tracker.setDispatchInterval(0);
        this.tracker.setDispatchGzipped(false);
        Log.i("Medimoz", "MZEvents: Reinicializa tracker cambiando ID de sitio: "+ this.trackingUrl + " : " + newId);
    }

}
