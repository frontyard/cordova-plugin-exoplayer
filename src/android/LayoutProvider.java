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
    private static final String TAG = "ExoPlayerPlugin";
    private enum BUTTON { exo_prev, exo_rew, exo_play, exo_pause, exo_ffwd, exo_next }

    public static FrameLayout getMainLayout(Activity activity) {
        FrameLayout view = new FrameLayout(activity);
        view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        view.setKeepScreenOn(true);

        return view;
    }

    public static SimpleExoPlayerView getExoPlayer(Activity activity, Configuration config) {
        SimpleExoPlayerView view = new SimpleExoPlayerView(activity);
        view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        if (config.isAspectRatioFillScreen()) {
            view.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        }
        view.setFastForwardIncrementMs(config.getSkipTimeMs());
        view.setRewindIncrementMs(config.getSkipTimeMs());
        view.setShowMultiWindowTimeBar(true);
        view.setControllerHideOnTouch(true);
        view.setControllerShowTimeoutMs(config.getHideTimeout());

        //ImageButton playButton = (ImageButton) activity.findViewById(/*com.google.android.exoplayer2.*/R.id.exo_play);
        //ImageView playButton = (ImageView)activity.findViewById(R.id.exo_play);
        //playButton.setImageResource(R.drawable.ic_play);
        //Picasso.with(imageButton.getContext()).load("http://icons.iconarchive.com/icons/icons-land/play-stop-pause/256/Play-Normal-icon.png").into(imageButton);

//        String artwork = config.getArtwork();
//        if (null != artwork) {
////            ImageView imageView = (ImageView) LayoutInflater.from(activity).inflate(com.google.android.exoplayer2.ui.R.id.exo_artwork, null);
//            ImageView imageView = (ImageView) activity.findViewById(com.google.android.exoplayer2.R.id.exo_artwork);
//            if (null != imageView) {
//                Picasso.with(imageView.getContext()).load(artwork).into(imageView);
//            }
//        }

        setupController(view, activity, config.getController());
        return view;
    }

    public static void setupController(SimpleExoPlayerView parentView, Activity activity, JSONObject controller) {
        if (null != controller) {
            parentView.setUseController(true);
            setupButtons(parentView, activity, controller);
            setupBar(parentView, activity, controller);
        }
        else {
            parentView.setUseController(false);
        }
    }

    private static void setupButtons(SimpleExoPlayerView parentView, Activity activity, JSONObject controller) {
        java.lang.String packageName = activity.getPackageName();
        JSONObject buttonsConfig = controller.optJSONObject("buttons");
        if (null != buttonsConfig) {
            for (BUTTON b : BUTTON.values()) {
                String buttonName = b.name();
                if (buttonsConfig.has(buttonName)) {
                    ImageButton imageButton = (ImageButton) findView(parentView, activity, buttonName);
                    if (null != imageButton) {
                        String buttonUrl = buttonsConfig.optString(buttonName);
                        if (null == buttonUrl) {
                            Log.i(TAG, "Hiding " + buttonName + " button");
                            imageButton.setVisibility(View.GONE);
                        }
                        else {
                            Log.i(TAG, "Loading " + buttonName + " from " + buttonUrl);
                            Picasso.with(imageButton.getContext()).load(buttonUrl).into(imageButton);
                        }
                    }
                    else {
                        Log.e(TAG, "ImageButton " + buttonName + " not found!");
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
        String streamSubtitle = controller.optString("streamSubtitle", null);
        String streamImage = controller.optString("streamImage", null);
        boolean hideProgress = controller.optBoolean("hideProgress");

        ImageView imageView = (ImageView) findView(parentView, activity, "exo_image");
        TextView titleView = (TextView) findView(parentView, activity, "exo_title");
        TextView subtitleView = (TextView) findView(parentView, activity, "exo_subtitle");
        View timebarView = findView(parentView, activity, "exo_timebar");

        if(null != streamImage) {
            Picasso.with(imageView.getContext()).load(streamImage).into(imageView);
        }
        if(null != streamTitle) {
            titleView.setText(streamTitle);
        }
        if (null != streamSubtitle && !streamSubtitle.equals("null")) { // TODO: Why are we getting string "null" here?
            timebarView.setVisibility(View.GONE);
            subtitleView.setText(streamSubtitle);
        }
        else if (hideProgress) {
            timebarView.setVisibility(View.GONE);
            subtitleView.setText(streamSubtitle);
        }
        else {
            subtitleView.setVisibility(View.GONE);
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
