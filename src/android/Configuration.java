package co.frontyard.cordova.plugin.exoplayer;

import android.net.*;
import android.view.*;
import org.json.*;

public class Configuration {
    static final String TAG = "ExoplayerPlugin";

    private final JSONObject config;
    private final JSONObject header;

    public Configuration(JSONObject config) {
        this.config = config;
        this.header = config.optJSONObject("header");
    }

    public final Uri getUri() {
        return Uri.parse(config.optString("url", ""));
    }

    public final JSONObject getDimensions() {
        return config.optJSONObject("dimensions");
    }

    public String getUserAgent() {
        return this.config.optString("userAgent", "PluginExoPlayer");
    }

    public boolean isAspectRatioFillScreen() {
        return config.optString("aspectRatio", "FIT_SCREEN").equalsIgnoreCase("FILL_SCREEN");
    }

    public long getOffset() {
        return config.optLong("offset", -1);
    }

    public boolean isVisibleControls() {
        return config.optBoolean("controlsVisible", true);
    }
}
