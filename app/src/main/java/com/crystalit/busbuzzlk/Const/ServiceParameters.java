package com.crystalit.busbuzzlk.Const;

public class ServiceParameters {
    private static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";

    private static final long DETECTION_INTERVAL_IN_MILLISECONDS = 30 * 1000;

    private static final int CONFIDENCE = 70;


    public static String getBroadcastDetectedActivity() {
        return BROADCAST_DETECTED_ACTIVITY;
    }

    public static long getDetectionIntervalInMilliseconds() {
        return DETECTION_INTERVAL_IN_MILLISECONDS;
    }

    public static int getCONFIDENCE() {
        return CONFIDENCE;
    }
}
