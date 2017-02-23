package com.add.exoplayer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

public class LayoutManager {

    private final CallbackManager callbackManager;
    private final Entity entity;
    private Dialog dialog;
    private SimpleExoPlayer player;
    private ImageView imageView;
    private TextView textView;
    private TextView textView2;
    private SimpleExoPlayerView exoView;
    private LinearLayout header;
    private int showTimeoutMs = 5000;

    public LayoutManager(Entity entity, CallbackManager callbackManager) {
        this.entity = entity;
        this.callbackManager = callbackManager;
    }

    private DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (player != null) player.release();
            player = null;
            callbackManager.successResultCallback();
        }
    };

    private DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            LayoutManager.this.callbackManager.successResultCallback(Payload.keyEvent(event));
            return false;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        int previousAction = -1;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (LayoutManager.this.entity.publishRawTouchEvents()) {
                int eventAction = event.getAction();
                if (previousAction != eventAction) {
                    previousAction = eventAction;
                    LayoutManager.this.callbackManager.successResultCallback(Payload.touchEvent(event));
                }
            }
            
            if (!entity.isVisibleControls() && event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                if (header.getVisibility() == View.VISIBLE) {
                    hide();
                } else {
                    maybeShowController(true);
                }
            }
            return !entity.isVisibleControls();
        }
    };

    private final Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private void maybeShowController(boolean isForced) {
        if (!entity.hasHeader() || header == null || player == null) {
            return;
        }
        int playbackState = player.getPlaybackState();
        boolean showIndefinitely = playbackState == com.google.android.exoplayer2.ExoPlayer.STATE_IDLE
                || playbackState == com.google.android.exoplayer2.ExoPlayer.STATE_ENDED || !player.getPlayWhenReady();
        boolean wasShowingIndefinitely = header.getVisibility() == View.VISIBLE && showTimeoutMs <= 0;
        int controllerShowTimeoutMs = 5000;
        showTimeoutMs = showIndefinitely ? 0 : controllerShowTimeoutMs;
        if (isForced || showIndefinitely || wasShowingIndefinitely) {
            show();
        }
    }

    public void show() {
        if (!(header.getVisibility() == View.VISIBLE)) {
            header.setVisibility(View.VISIBLE);
        }
        hideAfterTimeout();
    }

    private void hideAfterTimeout() {
        header.removeCallbacks(hideAction);
        if (showTimeoutMs > 0) {
            header.postDelayed(hideAction, showTimeoutMs);
        }
    }

    public void hide() {
        if (header.getVisibility() == View.VISIBLE) {
            header.setVisibility(View.GONE);
            header.removeCallbacks(hideAction);
        }
    }

    public void createDialog() {
        dialog = new Dialog(callbackManager.getActivity(), entity.getTheme());
        dialog.setOnKeyListener(onKeyListener);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(dismissListener);

        FrameLayout mainLayout = LayoutProvider.getMainLayout(callbackManager);
        exoView = LayoutProvider.getExoPlayer(callbackManager, entity);
        mainLayout.addView(exoView);
        if (entity.hasHeader()) {
            header = LayoutProvider.getLinearLayout(LinearLayout.HORIZONTAL, entity.getHeaderHeight(), entity.getHeaderColor(), false, callbackManager, entity);
            header.addView(imageView = LayoutProvider.getImageView(callbackManager, entity));
            LinearLayout textHolder = LayoutProvider.getLinearLayout(LinearLayout.VERTICAL, WindowManager.LayoutParams.MATCH_PARENT, "#00FFFFFF", true, callbackManager, entity);
            textHolder.addView(textView = LayoutProvider.getTextView(callbackManager, entity));
            textHolder.addView(textView2 = LayoutProvider.getTextView(callbackManager, entity));

            header.addView(textHolder);
            mainLayout.addView(header);
        }
        dialog.setContentView(mainLayout);
        dialog.show();

        afterDialogIsShown();
    }

    private void afterDialogIsShown() {
        dialog.getWindow().setAttributes(LayoutProvider.getLayoutParams(dialog));
        exoView.requestFocus();
        exoView.setOnTouchListener(onTouchListener);
        if (entity.hasHeader()) {
            Picasso.with(imageView.getContext())
                    .load(entity.getHeaderImage())
                    .into(imageView);
            setTexView(entity.getHeaderText());

            if (entity.isVisibleControls())
                exoView.setControllerVisibilityListener(new PlaybackControlView.VisibilityListener() {
                    @Override
                    public void onVisibilityChange(int visibility) {
                        header.setVisibility(visibility);
                    }
                });
            else show();
        }
        preparePlayer(entity.isDash(), entity.getUri());
    }

    private void preparePlayer(boolean isDash, Uri url) {
        Handler mainHandler = new Handler();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(callbackManager.getActivity(), trackSelector, loadControl);
        exoView.setPlayer(player);

        String userAgent = Util.getUserAgent(callbackManager.getActivity(), entity.getUserAgent());
        HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(callbackManager.getActivity(), bandwidthMeter, httpDataSourceFactory);
        player.prepare(getMediaSource(userAgent, dataSourceFactory, mainHandler, isDash, url));
        player.setPlayWhenReady(true);
    }

    private MediaSource getMediaSource(String userAgent, DataSource.Factory dataSourceFactory, Handler mainHandler, boolean isDash, Uri uri) {
        if (!isDash)
            return new HlsMediaSource(uri, dataSourceFactory, mainHandler, null);

        return new DashMediaSource(uri, new DefaultDataSourceFactory(callbackManager.getActivity(), null, new DefaultHttpDataSourceFactory(userAgent, null)), new DefaultDashChunkSource.Factory(dataSourceFactory), mainHandler, null);
    }

    public void close() {
        if (player != null) player.release();
        player = null;
        if (this.dialog != null) dialog.dismiss();
    }

    public void setText(String text) {
        if (textView != null && textView2 != null) {
            setTexView(text);
            if (entity.isVisibleControls()) exoView.showController();
            else show();
        }
    }

    private void setTexView(String text) {
        String[] split = text.split("\n");
        textView.setText(split[0]);
        textView2.setText(split[1]);
    }

    public void setStream(boolean isDash, Uri url) {
        player.release();
        player = null;
        preparePlayer(isDash, url);
    }

    public void play() {
        player.setPlayWhenReady(true);
    }

    public void pause() {
        player.setPlayWhenReady(false);
    }

    public void seekTo(int timeMillis) {
        long seekPosition = player.getDuration() == 0 ? 0
                : Math.min(Math.max(0, timeMillis), player.getDuration());
        player.seekTo(seekPosition);
    }

    public String videoProperties() {
        return Payload.videoProperties(player);
    }
}
