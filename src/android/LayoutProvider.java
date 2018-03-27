/*
 The MIT License (MIT)

 Copyright (c) 2017 Nedim Cholich

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package co.frontyard.cordova.plugin.exoplayer;

import android.app.*;
import android.graphics.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.android.exoplayer2.ui.*;
import java.lang.*;
import java.lang.String;
import org.json.*;
import com.squareup.picasso.*;

public class LayoutProvider {
    private enum BUTTON { exo_prev, exo_rew, exo_play, exo_pause, exo_ffwd, exo_next }

    public static FrameLayout getMainLayout(Activity activity) {
        FrameLayout view = new FrameLayout(activity);
        view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        view.setKeepScreenOn(true);

        return view;
    }

    public static SimpleExoPlayerView getExoPlayerView(Activity activity, Configuration config) {
        SimpleExoPlayerView view = new SimpleExoPlayerView(activity);
        view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        if (config.isAspectRatioFillScreen()) {
            view.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        }
        view.setFastForwardIncrementMs(config.getForwardTimeMs());
        view.setRewindIncrementMs(config.getRewindTimeMs());
        view.setShowMultiWindowTimeBar(true);
        view.setControllerHideOnTouch(true);
        view.setControllerShowTimeoutMs(config.getHideTimeout());

        setupController(view, activity, config.getController());
        return view;
    }

    public static void setupController(SimpleExoPlayerView parentView, Activity activity, JSONObject controller) {
        if (null != controller) {
            parentView.setUseController(true);
            setupButtons(parentView, activity, controller);
            setupBar(parentView, activity, controller);
            setupBuffering(parentView, activity, controller);
        }
        else {
            parentView.setUseController(false);
        }
    }

    private static void setupButtons(SimpleExoPlayerView parentView, Activity activity, JSONObject controller) {
        String packageName = activity.getPackageName();
        String buttonsColor = controller.optString("buttonsColor");

        JSONObject buttonsConfig = controller.optJSONObject("controlIcons");
        if (null != buttonsConfig) {
            for (BUTTON b : BUTTON.values()) {
                String buttonName = b.name();
                ImageButton imageButton = (ImageButton) findView(parentView, activity, buttonName);
                if (null != imageButton) {
                    if (buttonsConfig.has(buttonName)) {
                        String buttonUrl = buttonsConfig.optString(buttonName);
                        if (null == buttonUrl || buttonUrl.equals("null")) { // Again, why is this a String "null"?
                            // Image is set to null, remove it from view.
                            imageButton.setVisibility(View.GONE);
                            ((ViewGroup) imageButton.getParent()).removeView(imageButton);
                        }
                        else {
                            // Loading from external source.
                            Picasso.with(imageButton.getContext()).load(buttonUrl).into(imageButton);
                        }
                    }
                    else if (null != buttonsColor) {
                        // Using default and tinting.
                        imageButton.setColorFilter(Color.parseColor(buttonsColor));
                    }
                }
            }
        }
        else {
            LinearLayout exoButtons = (LinearLayout) findView(parentView, activity, "exo_buttons");
            exoButtons.setVisibility(View.GONE);
        }
    }

    private static void setupBar(SimpleExoPlayerView parentView, Activity activity, JSONObject controller) {
        String streamTitle = controller.optString("streamTitle", null);
        String streamDescription = controller.optString("streamDescription", null);
        String streamImage = controller.optString("streamImage", null);
        String textColor = controller.optString("textColor");

        ImageView imageView = (ImageView) findView(parentView, activity, "exo_image");
        TextView titleView = (TextView) findView(parentView, activity, "exo_title");
        TextView subtitleView = (TextView) findView(parentView, activity, "exo_subtitle");
        View timebarView = findView(parentView, activity, "exo_timebar");
        TextView positionView = (TextView) findView(timebarView, activity, "exo_position");
        TextView durationView = (TextView) findView(timebarView, activity, "exo_duration");

        if (null != textColor) {
            int intTextColor = Color.parseColor(textColor);
            if (null != titleView) {
                titleView.setTextColor(intTextColor);
            }
            if (null != subtitleView) {
                subtitleView.setTextColor(intTextColor);
            }
            if (null != positionView) {
                positionView.setTextColor(intTextColor);
            }
            if (null != durationView) {
                durationView.setTextColor(intTextColor);
            }
        }
        if(null != streamImage) {
            Picasso.with(imageView.getContext()).load(streamImage).into(imageView);
        }
        if(null != streamTitle) {
            titleView.setText(streamTitle);
        }
        if (null != streamDescription && !streamDescription.equals("null")) { // TODO: Why are we getting string "null" here?
            subtitleView.setText(streamDescription);
        }
        else {
            subtitleView.setVisibility(View.GONE);
        }
        if (controller.optBoolean("hideProgress")) {
            timebarView.setVisibility(View.GONE);
        }
        else {
            if (controller.optBoolean("hidePosition") && null != positionView) {
                positionView.setVisibility(View.GONE);
                ((ViewGroup) positionView.getParent()).removeView(positionView);
            }
            if (controller.optBoolean("hideDuration") && null != durationView) {
                durationView.setVisibility(View.GONE);
                ((ViewGroup) durationView.getParent()).removeView(durationView);
            }
        }
    }

    private static void setupBuffering(SimpleExoPlayerView parentView, Activity activity, JSONObject controller) {
        String bufferingColor = controller.optString("bufferingColor");
        ProgressBar bufferingBar = (ProgressBar)findView(parentView, activity, "exo_buffering");
        if (null != bufferingBar && null != bufferingColor) {
            bufferingBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(bufferingColor), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    public static void setBufferingVisibility(SimpleExoPlayerView parentView, Activity activity, boolean visibile) {
        ProgressBar progressBar = (ProgressBar)findView(parentView, activity, "exo_buffering");
        if (null != progressBar) {
            progressBar.setVisibility(visibile ? View.VISIBLE : View.GONE);
        }
    }

    private static View findView(View view, Activity activity, String name) {
        int viewId = activity.getResources().getIdentifier(name, "id", activity.getPackageName());
        return view.findViewById(viewId);
    }

    public static WindowManager.LayoutParams getDialogLayoutParams(Activity activity, Configuration config, Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        JSONObject dim = config.getDimensions();
        if(null == dim) {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        else {
            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
            lp.x = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dim.optInt("x", 0), metrics);
            lp.y = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dim.optInt("y", 0), metrics);
            lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dim.optInt("width", WindowManager.LayoutParams.MATCH_PARENT), metrics);
            lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dim.optInt("height", WindowManager.LayoutParams.MATCH_PARENT), metrics);
        }

        return lp;
    }
}
