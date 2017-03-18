package co.frontyard.cordova.plugin.exoplayer;

import android.app.*;
import android.graphics.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.google.android.exoplayer2.ui.*;

public class LayoutProvider {

    public static FrameLayout getMainLayout(Activity activity) {
        FrameLayout view = new FrameLayout(activity);
        view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        view.setKeepScreenOn(true);

        return view;
    }

    public static LinearLayout getLinearLayout(Activity activity, Configuration config, int orientation, int height, String headerColor, boolean hasPadding) {
        LinearLayout view = new LinearLayout(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, height);
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(Color.parseColor(headerColor));
        view.setOrientation(orientation);

        if (hasPadding) {
            int padding = config.getPadding();
            view.setPadding(padding, padding, padding, padding);
        }

        return view;
    }

    public static ImageView getImageView(Activity activity, Configuration config) {
        ImageView view = new ImageView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(config.getHeaderHeight(), config.getHeaderHeight());
        view.setLayoutParams(layoutParams);
        int padding = config.getPadding();
        view.setPadding(padding, padding, 0, padding);

        return view;
    }

    public static TextView getTextView(Activity activity, Configuration config) {
        TextView view = new TextView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        view.setTextColor(Color.parseColor(config.getHeaderTextColor()));
        view.setGravity(config.getHeaderTextGravity() | Gravity.CENTER_VERTICAL);
        view.setEllipsize(TextUtils.TruncateAt.END);
        view.setSingleLine();
        view.setTextSize(config.getTextSize());

        return view;
    }

    public static SimpleExoPlayerView getExoPlayer(Activity activity, Configuration config) {
        SimpleExoPlayerView view = new SimpleExoPlayerView(activity);
        view.setUseController(config.isVisibleControls());
        view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        if (config.isAspectRatioFillScreen()) {
            view.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        }

        return view;
    }

    public static WindowManager.LayoutParams getLayoutParams(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        return lp;
    }
}
