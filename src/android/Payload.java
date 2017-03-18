package co.frontyard.cordova.plugin.exoplayer;

import android.view.*;
import com.google.android.exoplayer2.*;
import java.util.*;
import org.json.*;

public class Payload {

    public static JSONObject startEvent(ExoPlayer player) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("event_type", "start_event");
        return new JSONObject(map);
    }

    public static JSONObject stopEvent(ExoPlayer player) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("event_type", "stop_event");
        return new JSONObject(map);
    }

    public static JSONObject keyEvent(KeyEvent event) {
        int eventAction = event.getAction();
        Map<String, String> map = new HashMap<String, String>();
        map.put("event_type", "key_event");
        map.put("event_action", eventAction == KeyEvent.ACTION_DOWN ? "ACTION_DOWN" : eventAction == KeyEvent.ACTION_UP ? "ACTION_UP" : "" + eventAction);
        map.put("event_keycode", KeyEvent.keyCodeToString(event.getKeyCode()));
        return new JSONObject(map);
    }

    public static JSONObject touchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        Map<String, String> map = new HashMap<String, String>();
        map.put("event_type", "touch_event");
        map.put("event_action", eventAction == MotionEvent.ACTION_DOWN ? "ACTION_DOWN" : eventAction == MotionEvent.ACTION_UP ? "ACTION_UP" : eventAction == MotionEvent.ACTION_MOVE ? "ACTION_MOVE" : "" + eventAction);
        map.put("event_axis_x", Float.toString(event.getX()));
        map.put("event_axis_y", Float.toString(event.getY()));
        return new JSONObject(map);
    }

    public static JSONObject stateEvent(ExoPlayer player) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("event_type", "state_event");
        map.put("playing", Boolean.toString(player.getPlayWhenReady()));
        map.put("duration", Long.toString(player.getDuration()));
        map.put("position", Long.toString(player.getCurrentPosition()));
        return new JSONObject(map);
    }

    public static JSONObject loadingEvent(ExoPlayer player, boolean loading) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("event_type", "loading_event");
        map.put("loading", Boolean.toString(loading));
        return new JSONObject(map);
    }

    public static JSONObject stateChangedEvent(ExoPlayer player, int playbackState) {
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
        Map<String, String> map = new HashMap<String, String>();
        map.put("event_type", "state_changed_event");
        map.put("playback_state", state);
        return new JSONObject(map);
    }

    public static JSONObject positionDiscontinuityEvent(ExoPlayer player) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("event_type", "position_discontinuity_event");
        map.put("playing", Boolean.toString(player.getPlayWhenReady()));
        map.put("duration", Long.toString(player.getDuration()));
        map.put("position", Long.toString(player.getCurrentPosition()));
        return new JSONObject(map);
    }

    public static JSONObject playerErrorEvent(ExoPlayer player, ExoPlaybackException error) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("event_type", "player_error_event");
        switch (error.type) {
            case ExoPlaybackException.TYPE_RENDERER:
                map.put("error_type", "renderer");
                map.put("error_message", error.getMessage());
                break;
            case ExoPlaybackException.TYPE_SOURCE:
                map.put("error_type", "source");
                map.put("error_message", error.getMessage());
                break;
            case ExoPlaybackException.TYPE_UNEXPECTED:
                map.put("error_type", "unexpected");
                map.put("error_message", error.getMessage());
                break;
        }
        return new JSONObject(map);
    }
}
