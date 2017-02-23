package com.add.exoplayer;

import android.net.Uri;
import android.view.Gravity;

import org.json.JSONObject;

public class Entity {

    public static final String DASH = "dash";

    private final JSONObject entity;
    private final JSONObject header;

    public Entity(JSONObject entity) {
        this.entity = entity;
        this.header = entity.optJSONObject("header");
    }

    public boolean isDash() {
        return this.entity.optString("type", "dash").equalsIgnoreCase(DASH);
    }

    public final Uri getUri() {
        return Uri.parse(entity.optString("url", ""));
    }

    public String getUserAgent() {
        return this.entity.optString("user_agent", "PluginExoPlayer");
    }

    public boolean isVisibleControls() {
        return entity.optBoolean("plugin_controls_visible", true);
    }

    public boolean hasHeader() {
        return this.header != null;
    }

    public int getHeaderHeight() {
        return header.optInt("height", 150);
    }
    
    public int getPadding() {
        return header.optInt("padding", 20);
    }

    public int getTextSize() {
        return header.optInt("text_size", 16);
    }

    public String getHeaderColor() {
        return header.optString("background_color", "#33F0F8FF");
    }

    public String getHeaderImage() {
        return header.optString("image_url", "");
    }

    public String getHeaderTextColor() {
        return header.optString("text_color", "#BBFAA8EF");
    }

    public int getHeaderTextGravity() {
        String align = header.optString("text_align", "left");
        if (align.equalsIgnoreCase("center")) return Gravity.CENTER;
        else if (align.equalsIgnoreCase("right")) return Gravity.RIGHT;
        else return Gravity.LEFT;
    }

    public String getHeaderText() {
        return header.optString("text", "");
    }

    public boolean isAspectRatioFillScreen() {
        return entity.optString("aspect_ratio", "fit_screen").equalsIgnoreCase("fill_screen");
    }

    public boolean isFullscreen() {
        return entity.optBoolean("full_screen", true);
    }

    public boolean publishRawTouchEvents() {
        return entity.optBoolean("raw_touch_events", true);
    }

    public int getTheme() {
        int themeFullscreen = android.R.style.Theme_Black_NoTitleBar_Fullscreen;
        int themeNoTitleBar = android.R.style.Theme_NoTitleBar;
        return isFullscreen() ? themeFullscreen : themeNoTitleBar;
    }
}
