package co.frontyard.cordova.plugin.exoplayer;

import android.view.*;
import com.google.android.exoplayer2.*;
import java.lang.*;
import java.lang.Integer;
import java.lang.StackTraceElement;
import java.lang.StringBuffer;
import java.util.*;
import org.json.*;

public class Payload {

    private static String playbackStateToString(int playbackState) {
        String state = "UNKNOWN";
        switch (playbackState) {
            case ExoPlayer.STATE_IDLE:
                state = "STATE_IDLE";
                break;
            case ExoPlayer.STATE_BUFFERING:
                state = "STATE_BUFFERING";
                break;
            case ExoPlayer.STATE_READY:
                state = "STATE_READY";
                break;
            case ExoPlayer.STATE_ENDED:
                state = "STATE_ENDED";
                break;
        }
        return state;
    }

    public static JSONObject startEvent(ExoPlayer player) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("eventType", "START_EVENT");
        map.put("playWhenReady", Boolean.toString(player.getPlayWhenReady()));
        map.put("playbackState", playbackStateToString(player.getPlaybackState()));
        return new JSONObject(map);
    }

    public static JSONObject stopEvent(ExoPlayer player) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("eventType", "STOP_EVENT");
        return new JSONObject(map);
    }

    public static JSONObject keyEvent(KeyEvent event) {
        int eventAction = event.getAction();
        Map<String, String> map = new HashMap<String, String>();
        map.put("eventType", "KEY_EVENT");
        map.put("eventAction", eventAction == KeyEvent.ACTION_DOWN ? "ACTION_DOWN" : eventAction == KeyEvent.ACTION_UP ? "ACTION_UP" : "" + eventAction);
        map.put("eventKeycode", KeyEvent.keyCodeToString(event.getKeyCode()));
        return new JSONObject(map);
    }

    public static JSONObject touchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        Map<String, String> map = new HashMap<String, String>();
        map.put("eventType", "TOUCH_EVENT");
        map.put("eventAction", eventAction == MotionEvent.ACTION_DOWN ? "ACTION_DOWN" : eventAction == MotionEvent.ACTION_UP ? "ACTION_UP" : eventAction == MotionEvent.ACTION_MOVE ? "ACTION_MOVE" : "" + eventAction);
        map.put("eventAxisX", Float.toString(event.getX()));
        map.put("eventAxisY", Float.toString(event.getY()));
        return new JSONObject(map);
    }

    public static JSONObject loadingEvent(ExoPlayer player, boolean loading) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("eventType", "LOADING_EVENT");
        map.put("loading", Boolean.toString(loading));
        map.put("playWhenReady", Boolean.toString(player.getPlayWhenReady()));
        map.put("playbackState", playbackStateToString(player.getPlaybackState()));
        map.put("bufferPercentage", Integer.toString(player.getBufferedPercentage()));
        return new JSONObject(map);
    }

    public static JSONObject stateEvent(ExoPlayer player, int playbackState) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("eventType", "STATE_CHANGED_EVENT");
        map.put("duration", Long.toString(player.getDuration()));
        map.put("position", Long.toString(player.getCurrentPosition()));
        map.put("playWhenReady", Boolean.toString(player.getPlayWhenReady()));
        map.put("playbackState", playbackStateToString(playbackState));
        map.put("bufferPercentage", Integer.toString(player.getBufferedPercentage()));
        return new JSONObject(map);
    }

    public static JSONObject positionDiscontinuityEvent(ExoPlayer player) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("eventType", "POSITION_DISCONTINUITY_EVENT");
        map.put("duration", Long.toString(player.getDuration()));
        map.put("position", Long.toString(player.getCurrentPosition()));
        map.put("playWhenReady", Boolean.toString(player.getPlayWhenReady()));
        map.put("playbackState", playbackStateToString(player.getPlaybackState()));
        map.put("bufferPercentage", Integer.toString(player.getBufferedPercentage()));
        return new JSONObject(map);
    }

    public static JSONObject seekEvent(ExoPlayer player, long offset) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("eventType", "SEEK_EVENT");
        map.put("duration", Long.toString(player.getDuration()));
        map.put("position", Long.toString(player.getCurrentPosition()));
        map.put("offset", Long.toString(offset));
        map.put("playWhenReady", Boolean.toString(player.getPlayWhenReady()));
        map.put("playbackState", playbackStateToString(player.getPlaybackState()));
        map.put("bufferPercentage", Integer.toString(player.getBufferedPercentage()));
        return new JSONObject(map);
    }


    public static JSONObject playerErrorEvent(ExoPlayer player, ExoPlaybackException origin, String message) {
        int type = 0;
        Map<String, String> map = new HashMap<String, String>();
        map.put("eventType", "PLAYER_ERROR_EVENT");

        if (null != origin) {
            type = origin.type;
            Throwable error = (Throwable) origin;
            while (null != error.getCause()) {
                error = error.getCause();
            }
            error.fillInStackTrace();
            StringBuffer stackTrace = new StringBuffer();
            if (null != error) {
                StackTraceElement[] st = error.getStackTrace();
                if (null != st && st.length > 0) {
                    for (int i = 0; i < st.length; i++) {
                        StackTraceElement elem = st[i];
                        stackTrace.append(elem.getClassName() + "#" + elem.getMethodName() + "@" + elem.getLineNumber() + (elem.isNativeMethod() ? " NATIVE" : "")).append("\n");
                    }
                }
            }
            map.put("stackTrace", stackTrace.toString());
            map.put("errorMessage", error.getMessage());
        }
        if (null != message) {
            map.put("customMessage", message);
        }

        switch (type) {
            case ExoPlaybackException.TYPE_RENDERER:
                map.put("errorType", "RENDERER");
                break;
            case ExoPlaybackException.TYPE_SOURCE:
                map.put("errorType", "SOURCE");
                break;
            case ExoPlaybackException.TYPE_UNEXPECTED:
                map.put("errorType", "UNEXPECTED");
                break;
            default:
                map.put("errorType", "UNKNOWN");
                break;
        }

        return new JSONObject(map);
    }
}
