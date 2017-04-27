package co.frontyard.cordova.plugin.exoplayer;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.android.exoplayer2.*;
import com.google.android.exoplayer2.extractor.*;
import com.google.android.exoplayer2.source.*;
import com.google.android.exoplayer2.source.dash.*;
import com.google.android.exoplayer2.source.hls.*;
import com.google.android.exoplayer2.source.smoothstreaming.*;
import com.google.android.exoplayer2.trackselection.*;
import com.google.android.exoplayer2.ui.*;
import com.google.android.exoplayer2.upstream.*;
import com.google.android.exoplayer2.util.*;
import com.squareup.picasso.*;
import org.apache.cordova.*;
import org.json.*;

public class Player {
    private static final String TAG = "ExoplayerPlugin";
    private final Activity activity;
    private final CallbackContext callbackContext;
    private final Configuration config;
    private Dialog dialog;
    private SimpleExoPlayer exoPlayer;
    private SimpleExoPlayerView exoView;
    private CordovaWebView webView;

    public Player(Configuration config, Activity activity, CallbackContext callbackContext, CordovaWebView webView) {
        this.config = config;
        this.activity = activity;
        this.callbackContext = callbackContext;
        this.webView = webView;
    }

    private ExoPlayer.EventListener playerEventListener = new ExoPlayer.EventListener() {
        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.e(TAG, "Error in ExoPlayer", error);
            JSONObject payload = Payload.playerErrorEvent(Player.this.exoPlayer, error, null);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.ERROR, payload, true);
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            JSONObject payload = Payload.loadingEvent(Player.this.exoPlayer, isLoading);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            JSONObject payload = Payload.stateEvent(Player.this.exoPlayer, playbackState);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
        }

        @Override
        public void onPositionDiscontinuity() {
            JSONObject payload = Payload.positionDiscontinuityEvent(Player.this.exoPlayer);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            // TODO Maybe?
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // TODO Maybe?
        }
    };

    private DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (exoPlayer != null) {
                exoPlayer.release();
            }
            exoPlayer = null;
            JSONObject payload = Payload.stopEvent(exoPlayer);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
        }
    };

    private DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            JSONObject payload = Payload.keyEvent(event);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        int previousAction = -1;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int eventAction = event.getAction();
            if (previousAction != eventAction) {
                previousAction = eventAction;
                JSONObject payload = Payload.touchEvent(event);
                new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
            }
            return true;
        }
    };

    public void createDialog() {
        dialog = new Dialog(this.activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setOnKeyListener(onKeyListener);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(dismissListener);

        FrameLayout mainLayout = LayoutProvider.getMainLayout(this.activity);
        exoView = LayoutProvider.getExoPlayer(this.activity, config);
        // Disable default controller since it's rather basic.
        exoView.setUseController(config.isVisibleControls());
        mainLayout.addView(exoView);
        dialog.setContentView(mainLayout);
        dialog.show();
        afterDialogIsShown();
    }

    private void afterDialogIsShown() {
        dialog.getWindow().setAttributes(LayoutProvider.getDialogLayoutParams(activity, config, dialog));
        exoView.requestFocus();
        exoView.setOnTouchListener(onTouchListener);
        preparePlayer(config.getUri());
    }

    private void preparePlayer(Uri uri) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this.activity, trackSelector, loadControl);
        exoPlayer.addListener(playerEventListener);
        exoView.setPlayer(exoPlayer);

        MediaSource mediaSource = getMediaSource(uri, bandwidthMeter);
        if (mediaSource != null) {
            long offset = config.getOffset();
            if (offset > -1) {
                exoPlayer.seekTo(offset);
            }
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
            JSONObject payload = Payload.startEvent(exoPlayer);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
        }
        else {
            String msg = "Failed to construct mediaSource for " + uri;
            Log.e(TAG, msg);
            JSONObject payload = Payload.playerErrorEvent(Player.this.exoPlayer, null, msg);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.ERROR, payload, true);
        }
    }

    private MediaSource getMediaSource(Uri uri, DefaultBandwidthMeter bandwidthMeter) {
        String userAgent = Util.getUserAgent(this.activity, config.getUserAgent());
        Handler mainHandler = new Handler();
        HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this.activity, bandwidthMeter, httpDataSourceFactory);

        int type = Util.inferContentType(uri.toString());
        switch(type) {
            case C.TYPE_DASH:
                DefaultDashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(dataSourceFactory);
                return new DashMediaSource(uri, new DefaultDataSourceFactory(this.activity, null, new DefaultHttpDataSourceFactory(userAgent, null)),
                        dashChunkSourceFactory, mainHandler, null);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, dataSourceFactory, mainHandler, null);
            case C.TYPE_SS:
                DefaultSsChunkSource.Factory ssChunkSourceFactory = new DefaultSsChunkSource.Factory(dataSourceFactory);
                return new SsMediaSource(uri, dataSourceFactory, ssChunkSourceFactory, mainHandler, null);
            default:
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                return new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, mainHandler, null);
        }
    }

    public void close() {
        if (exoPlayer != null) {
            exoPlayer.release();
        }
        exoPlayer = null;
        if (this.dialog != null) {
            dialog.dismiss();
        }
    }

    public void setStream(Uri uri) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        MediaSource mediaSource = getMediaSource(uri, bandwidthMeter);
        exoPlayer.prepare(mediaSource);
    }

    public void play() {
        exoPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        exoPlayer.setPlayWhenReady(false);
    }

    public void seekTo(long timeMillis) {
        long seekPosition = exoPlayer.getDuration() == 0 ? 0 : Math.min(Math.max(0, timeMillis), exoPlayer.getDuration());
        exoPlayer.seekTo(seekPosition);
        JSONObject payload = Payload.seekEvent(Player.this.exoPlayer, timeMillis);
        new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
    }

    public JSONObject getPlayerState() {
        return Payload.stateEvent(exoPlayer, exoPlayer.getPlaybackState());
    }
}
