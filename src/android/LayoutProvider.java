package co.frontyard.cordova.plugin.exoplayer;

import android.app.*;
import android.graphics.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.android.exoplayer2.ui.*;
import org.json.*;

public class LayoutProvider {

    public static FrameLayout getMainLayout(Activity activity) {
        FrameLayout view = new FrameLayout(activity);
        view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        view.setKeepScreenOn(true);

        return view;
    }

    public static SimpleExoPlayerView getExoPlayer(Activity activity, Configuration config) {
        SimpleExoPlayerView view = new SimpleExoPlayerView(activity);
        view.setUseController(false);
        view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        if (config.isAspectRatioFillScreen()) {
            view.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        }

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
