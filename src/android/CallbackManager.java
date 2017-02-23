package com.add.exoplayer;

import android.app.Activity;

import org.apache.cordova.CallbackContext;

public interface CallbackManager {

    void noResultCallback();

    void successResultCallback();

    void successResultCallback(String message);

    void successResultCallback(CallbackContext callbackContext, String message, boolean keepCallback);

    Activity getActivity();
}
