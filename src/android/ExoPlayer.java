package com.add.exoplayer;

import android.app.Activity;
import android.net.Uri;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExoPlayer extends CordovaPlugin implements CallbackManager {

    private LayoutManager manager;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("show")) {
            if (this.manager != null) {
                this.manager.close();
                successResultCallback();
            }
            this.callbackContext = callbackContext;

            this.manager = new LayoutManager(new Entity(data.getJSONObject(0)), this);
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ExoPlayer.this.manager.createDialog();
                }
            });

            noResultCallback();
            return true;
        } else if (action.equals("setText")) {
            if (this.manager == null) return false;
            final String message = data.optString(0, "");
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ExoPlayer.this.manager.setText(message);
                }
            });

            noResultCallback();
            return true;
        } else if (action.equals("setStream")) {
            if (this.manager == null) return false;
            final String type = data.optString(0, "dash");
            final String url = data.optString(1, "");
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ExoPlayer.this.manager.setStream(type.equalsIgnoreCase(Entity.DASH), Uri.parse(url));
                }
            });

            noResultCallback();
            return true;
        } else if (action.equals("play")) {
            if (this.manager == null) return false;
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ExoPlayer.this.manager.play();
                }
            });

            noResultCallback();
            return true;
        } else if (action.equals("pause")) {
            if (this.manager == null) return false;
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ExoPlayer.this.manager.pause();
                }
            });

            noResultCallback();
            return true;
        } else if (action.equals("seekTo")) {
            if (this.manager == null) return false;
            final int seekTime = data.optInt(0, 0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ExoPlayer.this.manager.seekTo(seekTime);
                }
            });

            noResultCallback();
            return true;
        } else if (action.equals("videoProperties")) {
            if (this.manager == null) return false;
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    String values = ExoPlayer.this.manager.videoProperties();
                    successResultCallback(callbackContext, values, false);
                }
            });

            noResultCallback();
            return true;
        } else if (action.equals("close")) {
            if (this.manager != null) {
                this.manager.close();
                successResultCallback();
            }
            this.manager = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void noResultCallback() {
        if (this.callbackContext == null) return;

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        this.callbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void successResultCallback() {
        if (this.callbackContext == null) return;

        PluginResult result = new PluginResult(PluginResult.Status.OK);
        result.setKeepCallback(false);
        callbackContext.sendPluginResult(result);
    }

    @Override
    public void successResultCallback(String message) {
        if (this.callbackContext == null) return;

        PluginResult result = new PluginResult(PluginResult.Status.OK, message);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    @Override
    public void successResultCallback(CallbackContext callbackContext, String message, boolean keepCallback) {
        if (callbackContext == null) return;

        PluginResult result = new PluginResult(PluginResult.Status.OK, message);
        result.setKeepCallback(keepCallback);
        callbackContext.sendPluginResult(result);
    }

    @Override
    public Activity getActivity() {
        return cordova.getActivity();
    }
}
