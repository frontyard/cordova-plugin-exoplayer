package co.frontyard.cordova.plugin.exoplayer;

import org.apache.cordova.*;
import org.json.*;

public class CallbackResponse {
    private CallbackContext callbackContext;

    public CallbackResponse(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    public void send(PluginResult.Status status, boolean keepCallback) {
        if (callbackContext == null) {
            return;
        }
        PluginResult result = new PluginResult(status);
        result.setKeepCallback(keepCallback);
        callbackContext.sendPluginResult(result);
    }

    public void send(PluginResult.Status status, JSONObject message, boolean keepCallback) {
        if (callbackContext == null) {
            return;
        }
        PluginResult result = new PluginResult(status, message);
        result.setKeepCallback(keepCallback);
        callbackContext.sendPluginResult(result);
    }
}