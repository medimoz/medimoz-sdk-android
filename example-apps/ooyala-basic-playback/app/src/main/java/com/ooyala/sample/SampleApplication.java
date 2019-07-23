package com.ooyala.sample;


import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import medimoz.sdk.MZEvents;


public class SampleApplication extends Application {

    protected MZEvents medimoz;
    protected String MEDIMOZ_TRACKING_URL = "https://log.medimoz.com/mz";
    public static final int MEDIMOZ_MAIN_SITEID = 1;
    public static final int MEDIMOZ_SECONDARY_SITEID = 25;


    /**
     * Initializes and returns the tracking components.
     *
     * @return tracking component handle
     */
    public synchronized MZEvents getMedimoz() {
        if (medimoz == null) {
            medimoz = new MZEvents(MEDIMOZ_MAIN_SITEID, MEDIMOZ_TRACKING_URL, this);
        }
        return medimoz;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
