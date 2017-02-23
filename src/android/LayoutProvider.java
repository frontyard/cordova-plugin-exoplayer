package com.add.exoplayer;

import android.app.Dialog;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

public class LayoutProvider {

    public static FrameLayout getMainLayout(CallbackManager callbackManager) {
        FrameLayout view = new FrameLayout(callbackManager.getActivity());
        view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        view.setKeepScreenOn(true);

        return view;
    }

    public static LinearLayout getLinearLayout(int orientation, int height, String headerColor, boolean hasPadding, CallbackManager callbackManager, Entity entity) {
        LinearLayout view = new LinearLayout(callbackManager.getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, height);
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(Color.parseColor(headerColor));
        view.setOrientation(orientation);

        if (hasPadding) {
            int padding = entity.getPadding();
            view.setPadding(padding, padding, padding, padding);
        }

        return view;
    }

    public static ImageView getImageView(CallbackManager callbackManager, Entity entity) {
        ImageView view = new ImageView(callbackManager.getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(entity.getHeaderHeight(), entity.getHeaderHeight());
        view.setLayoutParams(layoutParams);
        int padding = entity.getPadding();
        view.setPadding(padding, padding, 0, padding);

        return view;
    }

    public static TextView getTextView(CallbackManager callbackManager, Entity entity) {
        TextView view = new TextView(callbackManager.getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        view.setTextColor(Color.parseColor(entity.getHeaderTextColor()));
        view.setGravity(entity.getHeaderTextGravity() | Gravity.CENTER_VERTICAL);
        view.setEllipsize(TextUtils.TruncateAt.END);
        view.setSingleLine();
        view.setTextSize(entity.getTextSize());

        return view;
    }

    public static SimpleExoPlayerView getExoPlayer(CallbackManager callbackManager, Entity entity) {
        SimpleExoPlayerView view = new SimpleExoPlayerView(callbackManager.getActivity());
        view.setUseController(entity.isVisibleControls());
        view.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        if (entity.isAspectRatioFillScreen())
            view.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

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
