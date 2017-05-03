package co.frontyard.cordova.plugin.exoplayer;

import android.net.*;
import android.view.*;
import org.json.*;

public class Configuration {
    static final String TAG = "ExoPlayerPlugin";

    private final JSONObject config;

    public Configuration(JSONObject config) {
        this.config = config;
    }

    public final Uri getUri() {
        return Uri.parse(config.optString("url", ""));
    }

    public final JSONObject getDimensions() {
        return config.optJSONObject("dimensions");
    }

    public String getUserAgent() {
        return this.config.optString("userAgent", "ExoPlayer");
    }

    public boolean isAspectRatioFillScreen() {
        return config.optString("aspectRatio", "FIT_SCREEN").equalsIgnoreCase("FILL_SCREEN");
    }

    public long getPlayOffset() {
        return config.optLong("playOffset", -1);
    }

    public boolean isVisibleControls() {
        return config.optBoolean("controlsVisible", true);
    }

    public final JSONObject getController() {
        return config.optJSONObject("controller");
    }

    public int getHideTimeout() {
        return config.optInt("hideTimeout", 5000); // Default 5 sec.
    }

    public int getSkipTimeMs() {
        return config.optInt("skipTime", 60000); // Default 1 min.
    }
}
