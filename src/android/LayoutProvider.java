package co.frontyard.cordova.plugin.exoplayer;

import android.app.*;
import android.graphics.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.android.exoplayer2.ui.*;
import org.json.*;
import com.squareup.picasso.*;

public class LayoutProvider {

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
        view.setUseController(config.isVisibleControls());
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

        return view;
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
