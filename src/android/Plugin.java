package co.frontyard.cordova.plugin.exoplayer;

import android.net.*;
import org.apache.cordova.*;
import org.json.*;

public class Plugin extends CordovaPlugin {
    private Player player;

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {
        try {
            if (action.equals("show")) {
                if (this.player != null) {
                    this.player.close();
                    // TODO Do we need to send this back to the app?
                    //new CallbackResponse(callbackContext).send(PluginResult.Status.OK, true);
                }
                this.player = new Player(new Configuration(data.getJSONObject(0)), cordova.getActivity(), callbackContext);
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Plugin.this.player.createDialog();
                    }
                });

                new CallbackResponse(callbackContext).send(PluginResult.Status.NO_RESULT, true);
                return true;
            }
            else if (action.equals("setText")) {
                if (this.player == null) {
                    return false;
                }
                final String message = data.optString(0, "");
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Plugin.this.player.setText(message);
                    }
                });

                new CallbackResponse(callbackContext).send(PluginResult.Status.NO_RESULT, true);
                return true;
            }
            else if (action.equals("setStream")) {
                if (this.player == null) {
                    return false;
                }
                final String url = data.optString(0, "");
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Plugin.this.player.setStream(Uri.parse(url));
                    }
                });

                new CallbackResponse(callbackContext).send(PluginResult.Status.NO_RESULT, true);
                return true;
            }
            else if (action.equals("play")) {
                if (this.player == null) {
                    return false;
                }
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Plugin.this.player.play();
                    }
                });

                new CallbackResponse(callbackContext).send(PluginResult.Status.NO_RESULT, true);
                return true;
            }
            else if (action.equals("pause")) {
                if (this.player == null) {
                    return false;
                }
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Plugin.this.player.pause();
                    }
                });

                new CallbackResponse(callbackContext).send(PluginResult.Status.NO_RESULT, true);
                return true;
            }
            else if (action.equals("seekTo")) {
                if (this.player == null) {
                    return false;
                }
                final int seekTime = data.optInt(0, 0);
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Plugin.this.player.seekTo(seekTime);
                    }
                });

                new CallbackResponse(callbackContext).send(PluginResult.Status.NO_RESULT, true);
                return true;
            }
            else if (action.equals("getState")) {
                if (this.player == null) {
                    return false;
                }
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        JSONObject response = Plugin.this.player.getPlayerState();
                        new CallbackResponse(callbackContext).send(PluginResult.Status.OK, response, false);
                    }
                });

                //TODO This is synchronous, while the actual call is async?
                new CallbackResponse(callbackContext).send(PluginResult.Status.NO_RESULT, true);
                return true;
            }
            else if (action.equals("close")) {
                if (this.player != null) {
                    this.player.close();
                    new CallbackResponse(callbackContext).send(PluginResult.Status.OK, false);
                }
                this.player = null;
                return true;
            }
            else {
                new CallbackResponse(callbackContext).send(PluginResult.Status.INVALID_ACTION, false);
                return false;
            }
        }
        catch (Exception e) {
            new CallbackResponse(callbackContext).send(PluginResult.Status.JSON_EXCEPTION, false);
            return false;
        }
    }
}
