package com.add.exoplayer;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.google.android.exoplayer2.SimpleExoPlayer;

import org.json.JSONException;
import org.json.JSONObject;

public class Payload {

    public static String keyEvent(KeyEvent event) {
        int eventAction = event.getAction();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event_type", "key_event");
            jsonObject.put("event_action", eventAction == KeyEvent.ACTION_DOWN ? "ACTION_DOWN" : eventAction == KeyEvent.ACTION_UP ? "ACTION_UP" : "" + eventAction);
            jsonObject.put("event_keycode", KeyEvent.keyCodeToString(event.getKeyCode()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String touchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event_type", "touch_event");
            jsonObject.put("event_action", eventAction == MotionEvent.ACTION_DOWN ? "ACTION_DOWN" : eventAction == MotionEvent.ACTION_UP ? "ACTION_UP" : eventAction == MotionEvent.ACTION_MOVE ? "ACTION_MOVE" : "" + eventAction);
            jsonObject.put("event_axis_x", event.getX());
            jsonObject.put("event_axis_y", event.getY());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String videoProperties(SimpleExoPlayer player) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("duration", player.getDuration());
            jsonObject.put("current_position", player.getCurrentPosition());
            jsonObject.put("is_playing", player.getPlayWhenReady());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
