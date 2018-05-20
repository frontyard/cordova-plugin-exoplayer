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
import android.content.*;
import android.media.*;
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
import java.lang.*;
import java.lang.Math;
import java.lang.Override;
import org.apache.cordova.*;
import org.json.*;

public class Player {
    public static final String TAG = "ExoPlayerPlugin";
    private final Activity activity;
    private final CallbackContext callbackContext;
    private final Configuration config;
    private Dialog dialog;
    private SimpleExoPlayer exoPlayer;
    private SimpleExoPlayerView exoView;
    private CordovaWebView webView;
    private int controllerVisibility;
    private boolean paused = false;
    private AudioManager audioManager;

    public Player(Configuration config, Activity activity, CallbackContext callbackContext, CordovaWebView webView) {
        this.config = config;
        this.activity = activity;
        this.callbackContext = callbackContext;
        this.webView = webView;
        this.audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
    }

    private ExoPlayer.EventListener playerEventListener = new ExoPlayer.EventListener() {
        @Override
        public void onLoadingChanged(boolean isLoading) {
            JSONObject payload = Payload.loadingEvent(Player.this.exoPlayer, isLoading);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            Log.i(TAG, "Playback parameters changed");
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            JSONObject payload = Payload.playerErrorEvent(Player.this.exoPlayer, error, null);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.ERROR, payload, true);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (config.getShowBuffering()) {
                LayoutProvider.setBufferingVisibility(exoView, activity, playbackState == ExoPlayer.STATE_BUFFERING);
            }
            JSONObject payload = Payload.stateEvent(Player.this.exoPlayer, playbackState, Player.this.controllerVisibility == View.VISIBLE);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            JSONObject payload = Payload.positionDiscontinuityEvent(Player.this.exoPlayer, reason);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
        }

        @Override
        public void onRepeatModeChanged(int newRepeatMode) {
            // Need to see if we want to send this to Cordova.
        }
    
        @Override
        public void onSeekProcessed() {
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            JSONObject payload = Payload.timelineChangedEvent(Player.this.exoPlayer, timeline, manifest);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // Need to see if we want to send this to Cordova.
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
            int action = event.getAction();
            String key = KeyEvent.keyCodeToString(event.getKeyCode());
            // We need android to handle these key events
            if (key.equals("KEYCODE_VOLUME_UP") ||
                    key.equals("KEYCODE_VOLUME_DOWN") ||
                    key.equals("KEYCODE_VOLUME_MUTE")) {
                return false;
            }
            else {
                JSONObject payload = Payload.keyEvent(event);
                new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
                return true;
            }
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

    private PlaybackControlView.VisibilityListener playbackControlVisibilityListener = new PlaybackControlView.VisibilityListener() {
        @Override
        public void onVisibilityChange(int visibility) {
            Player.this.controllerVisibility = visibility;
        }
    };

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                JSONObject payload = Payload.audioFocusEvent(Player.this.exoPlayer, "AUDIOFOCUS_LOSS_TRANSIENT");
                new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
            }
            else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                JSONObject payload = Payload.audioFocusEvent(Player.this.exoPlayer, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
            }
            else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                JSONObject payload = Payload.audioFocusEvent(Player.this.exoPlayer, "AUDIOFOCUS_GAIN");
                new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
            }
            else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                JSONObject payload = Payload.audioFocusEvent(Player.this.exoPlayer, "AUDIOFOCUS_LOSS");
                new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
            }
        }
    };

    public void createPlayer() {
        if (!config.isAudioOnly()) {
            createDialog();
        }
        preparePlayer(config.getUri());
    }

    public void createDialog() {
        dialog = new Dialog(this.activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setOnKeyListener(onKeyListener);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View decorView = dialog.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(dismissListener);

        FrameLayout mainLayout = LayoutProvider.getMainLayout(this.activity);
        exoView = LayoutProvider.getExoPlayerView(this.activity, config);
        exoView.setControllerVisibilityListener(playbackControlVisibilityListener);

        mainLayout.addView(exoView);
        dialog.setContentView(mainLayout);
        dialog.show();

        dialog.getWindow().setAttributes(LayoutProvider.getDialogLayoutParams(activity, config, dialog));
        exoView.requestFocus();
        exoView.setOnTouchListener(onTouchListener);
        LayoutProvider.setupController(exoView, activity, config.getController());
    }

    private int setupAudio() {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        return audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    private void preparePlayer(Uri uri) {
        int audioFocusResult = setupAudio();
        String audioFocusString = audioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_FAILED ?
                "AUDIOFOCUS_REQUEST_FAILED" :
                "AUDIOFOCUS_REQUEST_GRANTED";
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        //TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this.activity, trackSelector, loadControl);
        exoPlayer.addListener(playerEventListener);
        if (null != exoView) {
            exoView.setPlayer(exoPlayer);
        }

        MediaSource mediaSource = getMediaSource(uri, bandwidthMeter);
        if (mediaSource != null) {
            long offset = config.getSeekTo();
            boolean autoPlay = config.autoPlay();
            if (offset > -1) {
                exoPlayer.seekTo(offset);
            }
            exoPlayer.prepare(mediaSource);

            exoPlayer.setPlayWhenReady(autoPlay);
            paused = !autoPlay;

            JSONObject payload = Payload.startEvent(exoPlayer, audioFocusString);
            new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.OK, payload, true);
        }
        else {
            sendError("Failed to construct mediaSource for " + uri);
        }
    }

    private MediaSource getMediaSource(Uri uri, DefaultBandwidthMeter bandwidthMeter) {
        String userAgent = Util.getUserAgent(this.activity, config.getUserAgent());
        Handler mainHandler = new Handler();
        int connectTimeout = config.getConnectTimeout();
        int readTimeout = config.getReadTimeout();
        int retryCount = config.getRetryCount();

        HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter, connectTimeout, readTimeout, true);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this.activity, bandwidthMeter, httpDataSourceFactory);
        MediaSource mediaSource;
        int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                long livePresentationDelayMs = DashMediaSource.DEFAULT_LIVE_PRESENTATION_DELAY_PREFER_MANIFEST_MS;
                DefaultDashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(dataSourceFactory);
                // Last param is AdaptiveMediaSourceEventListener
                mediaSource = new DashMediaSource(uri, dataSourceFactory, dashChunkSourceFactory, retryCount, livePresentationDelayMs, mainHandler, null);
                break;
            case C.TYPE_HLS:
                // Last param is AdaptiveMediaSourceEventListener
                mediaSource = new HlsMediaSource(uri, dataSourceFactory, retryCount, mainHandler, null);
                break;
            case C.TYPE_SS:
                DefaultSsChunkSource.Factory ssChunkSourceFactory = new DefaultSsChunkSource.Factory(dataSourceFactory);
                // Last param is AdaptiveMediaSourceEventListener
                mediaSource = new SsMediaSource(uri, dataSourceFactory, ssChunkSourceFactory, mainHandler, null);
                break;
            default:
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, mainHandler, null);
                break;
        }

        String subtitleUrl = config.getSubtitleUrl();
        if (null != subtitleUrl) {
            Uri subtitleUri = Uri.parse(subtitleUrl);
            String subtitleType = inferSubtitleType(subtitleUri);
            Log.i(TAG, "Subtitle present: " + subtitleUri + ", type=" + subtitleType);
            com.google.android.exoplayer2.Format textFormat = com.google.android.exoplayer2.Format.createTextSampleFormat(null, subtitleType, null, com.google.android.exoplayer2.Format.NO_VALUE, com.google.android.exoplayer2.Format.NO_VALUE, "en", null, 0);
            MediaSource subtitleSource = new SingleSampleMediaSource(subtitleUri, httpDataSourceFactory, textFormat, C.TIME_UNSET);
            return new MergingMediaSource(mediaSource, subtitleSource);
        }
        else {
            return mediaSource;
        }
    }

    private static String inferSubtitleType(Uri uri) {
        String fileName = uri.getPath().toLowerCase();

        if (fileName.endsWith(".vtt")) {
            return MimeTypes.TEXT_VTT;
        }
        else {
            // Assume it's srt.
            return MimeTypes.APPLICATION_SUBRIP;
        }
    }

    public void close() {
        audioManager.abandonAudioFocus(audioFocusChangeListener);
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        if (this.dialog != null) {
            dialog.dismiss();
        }
    }

    public void setStream(Uri uri, JSONObject controller) {
        if (null != uri && null != exoPlayer) {
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            MediaSource mediaSource = getMediaSource(uri, bandwidthMeter);
            exoPlayer.prepare(mediaSource);
            play();
        }
        setController(controller);
    }

    public void playPause() {
        if (this.paused) {
            play();
        }
        else {
            pause();
        }
    }

    private void pause() {
        if (null != exoPlayer) {
            paused = true;
            exoPlayer.setPlayWhenReady(false);
        }
    }

    private void play() {
        paused = false;
        exoPlayer.setPlayWhenReady(true);
    }

    public void stop() {
        paused = false;
        exoPlayer.stop();
    }

    private long normalizeOffset(long newTime) {
        long duration = exoPlayer.getDuration();
        return duration == 0 ? 0 : Math.min(Math.max(0, newTime), duration);
    }

    public JSONObject seekTo(long timeMillis) {
        long newTime = normalizeOffset(timeMillis);
        exoPlayer.seekTo(newTime);
        JSONObject payload = Payload.seekEvent(Player.this.exoPlayer, newTime);
        return payload;
    }

    public JSONObject seekBy(long timeMillis) {
        long newTime = normalizeOffset(exoPlayer.getCurrentPosition() + timeMillis);
        exoPlayer.seekTo(newTime);
        JSONObject payload = Payload.seekEvent(Player.this.exoPlayer, newTime);
        return payload;
    }

    public JSONObject getPlayerState() {
        return Payload.stateEvent(exoPlayer,
                null != exoPlayer ? exoPlayer.getPlaybackState() : SimpleExoPlayer.STATE_ENDED,
                Player.this.controllerVisibility == View.VISIBLE);
    }

    public void showController() {
        if (null != exoView) {
            exoView.showController();
        }
    }

    public void hideController() {
        if (null != exoView) {
            exoView.hideController();
        }
    }

    public void setController(JSONObject controller) {
        if (null != exoView) {
            LayoutProvider.setupController(exoView, activity, controller);
        }
    }

    private void sendError(String msg) {
        Log.e(TAG, msg);
        JSONObject payload = Payload.playerErrorEvent(Player.this.exoPlayer, null, msg);
        new CallbackResponse(Player.this.callbackContext).send(PluginResult.Status.ERROR, payload, true);
    }   
}
