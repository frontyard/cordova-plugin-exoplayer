package co.frontyard.cordova.plugin.exoplayer;

import android.view.View;

public enum Visibility {
    VISIBLE("VISIBLE", View.VISIBLE),
    INVISIBLE("INVISIBLE", View.INVISIBLE),
    GONE("GONE", View.GONE);

    private final String key;
    private final Integer value;

    Visibility(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public Integer getValue() {
        return value;
    }
}
