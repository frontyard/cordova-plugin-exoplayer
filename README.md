[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](LICENSE) [![Build Status](https://travis-ci.org/frontyard/cordova-plugin-exoplayer.svg?branch=2.0.0)](https://travis-ci.org/frontyard/cordova-plugin-exoplayer) [![Code Climate](https://codeclimate.com/github/frontyard/cordova-plugin-exoplayer/badges/gpa.svg)](https://codeclimate.com/github/frontyard/cordova-plugin-exoplayer) [![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2ZPEB7VKFG2CY)

# Cordova ExoPlayer Plugin

Cordova media player plugin using Google's ExoPlayer framework.

Please send us links to your cool projects made with this plugin so we can include them on this page!

## Keeping track of exoplayer versions here:
|plugin|exoplayer|
|-----|---------|
|2.5.6|2.9.6|
|2.5.5|2.8.1|
|2.5.4|2.6.1|

## Changin in version 2.5.6
Upgraded exoplayer to 2.9.6

Upgrading to following versions is now possible
cordova: 11.0.0 (latest)
cordova-android: ^9.1.0

## Changes in version 2.5.4
- Added loading progress bar to the top that shows up when player is buffering. This needs to be explicitly turned on with `showBuffering` boolean configuration setting.
- Added ability to change text, buttons and buffering colors using new controller configuration settings `textColor`, `buttonsColor` and `bufferingColor`.
- Added positionInFirstPeriod to TIMELINE_EVENT.
- Upgraded exoplayer to 2.6.1.

## Changes in version 2.5.3
- After observing performance issues removing the usage of okhttp.
- Removing the 'android.hardware.touchscreen' check before adding touch handler. This feature is not reliably reported by various devices. 

## Changes in version 2.5.2
- Upgraded exoplayer to version 2.5.1
- Only adding touchscreen event handler if device has "android.hardware.touchscreen" system feature.
- Added `connectTimeout`, `readTimeout` and `retryCount` configuration settings. These are related to http client player uses. 
- Added `seekBy` which is simmilar to seekTo but operates relative to the current possition. Value can be positive (seek forward) or negative (seek backwards). 

## Changes in version 2.5.1
- Added boolean options `hidePosition` and `hideDuration`. When playing dvr content thise numbers are relative to current time and very confusing to users. Adding ability to hide the numbers until better solution can be found.
- Added `autoPlay` (defaults to true) to allow user not to play the stream automatically. Must call `play` explicitly.

## Changes in version 2.5.0
- Removed configuration setting `skipTime` and replaced it with separate `forwardTime` and `rewindTime`.
- Sending `TIMELINE_EVENT` to Cordova with `periodDurationX` and `periodWindowPositionX` properties for duration and window position for each period (marked X) in the stream.  

## Changes in version 2.4.5
- Added `setController` method to update the controller mid-stream. It accepts the same controller object that is used as part of parameters for `show` method.

## Changes in version 2.4.4
- Added `stop` method to fully stop the stream but not realease the player. Call `setStream` to restart the playback or `close` to dispose of it.

## Changes in version 2.4.2
- Plugin no longer acts on audio focus event but rather let's Cordova app decide what to do.

## Changes in version 2.4.1
- When audio focus changes, plugin will send `AUDIO_FOCUS_EVENT` with parameter `audioFocus` that can be on of:
 `AUDIOFOCUS_LOSS_TRANSIENT`, `AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK`, `AUDIOFOCUS_GAIN`, `AUDIOFOCUS_LOSS`. 

## Changes in version 2.4.0
- Controller will no longer show automatically on touch and key events.
- Added showController and hideController methods for explicit control of controller visibility.
- Plugin will not longer close on KEYCODE_BACK key event, please call .close() explicitly.
- Plugin will request audio focus on startup. It will also listen for audio focus events and pause/play on those events. It will close on AUDIOFOCUS_LOSS. 
- START_EVENT will return additional info `audioFocus` which can be AUDIOFOCUS_REQUEST_FAILED or AUDIOFOCUS_REQUEST_GRANTED.
- STATE_EVENT will return additional info `controllerVisible` to tell the app if controller is visible or not.

## Changes in version 2.3.0

- Renamed plugin's namespace from window.exoplayer to window.ExoPlayer
- Removed `init` method as there is no need to keep instance of the plugin around. Just call methods directly on window.ExoPlayer
- `show` now takes configuration parameters as the first argumeent since we don't need init any more.
- Renamed `playOffset` configuration setting to `seekTo` to match the coresponding method that does the same thing
- Replaced `play` and `pause` methods with single `playPause` that does both and keeps track of playback status

## Using

Create a new Cordova Project

    $ cordova create hello com.example.helloapp Hello
    
Install the plugin

    $ cd hello
    $ cordova plugin add cordova-plugin-exoplayer
    

Edit `www/js/index.js` and add the following code inside `onDeviceReady`

```js
    var successCallback = function(json) {
    };

    var errorCallback = function(error) {
    };

    var params = {
        url: "http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0"
    };

    window.ExoPlayer.show(parameters, successCallback, errorCallback);
```

Plugin methods exported via window.ExoPlayer
```js
{
    setStream(url, controllerConfig) // switch stream without disposing of the player. controllerConfig is "controller" part of the inital parameters. 
    playPause() // will pause if playing and play if paused :-)
    stop() // will stop the current stream
    seekTo(milliseconds) // jump to particular poing into the stream
    getState(successCallback, errorCallback) // returns player state
    showController() // shows player controller
    hideController() // hides player controller
    setController() // sets `controller` part of configuration related to the info bar and control buttons.
    close() // close and dispose of player, very important to call this method when your app exits!
}
```

This is what `parameters` look like for the `show` call, most of them are optional: 
```js
{
    url: 'https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8',
    userAgent: 'MyAwesomePlayer', // default is 'ExoPlayerPlugin'
    aspectRatio: 'FILL_SCREEN', // default is FIT_SCREEN
    hideTimeout: 5000, // Hide controls after this many milliseconds, default is 5 sec
    autoPlay: true, // When set to false stream will not automatically start
    seekTo: 10 * 60 * 60 * 1000, // Start playback 10 minutes into video specified in ms, default is 0
    forwardTime: 60 * 1000, // Amount of time in ms to use when skipping forward, default is 1 min
    rewindTime: 60 * 1000, // Amount of time in ms to use when skipping backward, default is 1 min
    audioOnly: true, // Only play audio in the backgroud, default is false
    subtitleUrl: 'http://url.to/subtitle.srt', // Optional subtitle url
    connectTimeout: 1000, // http connect timeout in ms (default is 0)
    readTimeout: 1000, // http read timeout in ms (default is 0)
    retryCount: 5, // Number of times datasource will retry the stream before giving up (default is 3)
    showBuffering: true, // When buffering, player will show indicator at the top of the screen, default is false
    controller: { // If this object is not present controller will not be visible
        streamImage: 'http://url.to/channel.png',
        streamTitle: 'Cool channel / movie',
        streamDescription: '2nd line you can use to display whatever you want like current program epg or movie description',
        hideProgress: true, // Hide entire progress timebar
        hidePosition: false, // If timebar is visible hide current position from it
        hideDuration: false, // If timebar is visible Hide stream duration from it
        controlIcons: {
            'exo_rew': null, // Set to null to remove the button from the player
            'exo_play': 'http://url.to/play.png',
            'exo_pause': 'http://url.to/pause.png',
            //'exo_ffwd': null, // Buttons not included in configuration will show up as default ExoPlayer buttons
        },
        textColor: '#ffff0000', // These colors can be any valid Android color
        buttonsColor: '#ff00ff00', // This example uses hex values including alpha (first byte)
        bufferingColor: '#ff0000ff' // Alpha of 'ff' makes it 100% opaque
    }
}
```
Controller is composed of several pieces. To the left there is optional streamImage, followed by two lines on the right, top and bottom. Top line is reserved for streamTitle, while bottom line can either be streamDescription or progress bar. If you provide streamDescription, progress bar will not be visible. Optionaly you can turn off progress bar by passing hideProgress: true if you don't want to show either.

Playback control buttons are centered on the screen and use default ExoPlayer icons. Optionally you can override these by your own images via controlIcons object.

You can pass `subtitleUrl` for subtitle to be shown over the video. We currently support .srt and .vtt subtitle formats. Subtitles are not supported on all stream types, as ExoPlayer has requirement that both video and subtitle "must have the same number of periods, and must not have any dynamic windows", which means for simple mp4s it should work, but on more complex HLS/Dash setups it might not. 

If you pass in `audioOnly: true`, make sure to manually close the player on some event (like escape button) since the plugin won't be detecting keypresses when playing audio in the background.

If you want to show default control buttons (play/pause, rewind, forward) you need an empty controlIncons object:
```js
    controlIcons: {
    }
```
 
Plugin will send following events back to Cordova app through successCallback specified through show function:
```js
START_EVENT
STOP_EVENT
KEY_EVENT
TOUCH_EVENT
LOADING_EVENT
STATE_CHANGED_EVENT
POSITION_DISCONTINUITY_EVENT
SEEK_EVENT
PLAYER_ERROR_EVENT
TIMELINE_EVENT
```
Each event will send JSON payload coresponding to that event. Some events (where appropriate) will also send additional information about playback like duration, postion, etc. 

Example of key events:
```js
{
    'eventType':'KEY_EVENT',
    'eventAction':'ACTION_DOWN',
    'eventKeycode':'KEYCODE_VOLUME_UP'
}

{   
    'eventType':'KEY_EVENT',
    'eventAction':'ACTION_UP',
    'eventKeycode':'KEYCODE_VOLUME_UP'
}
```


Example of touch events:
```js
{
    'eventType':'TOUCH_EVENT',
    'eventAction':'ACTION_DOWN',
    'eventAxisX':543,
    'eventAxisY':1321.8009033203125
}

{   
    'eventType':'TOUCH_EVENT',
    'eventAction':'ACTION_MOVE',
    'eventAxisX':543,
    'eventAxisY':1320.5
}

{
    'eventType':'TOUCH_EVENT',
    'eventAction':'ACTION_UP',
    'eventAxisX':543,
    'eventAxisY':1320.5
}
```

Install Android platform

    cordova platform add android
    
Run the code

    cordova run

## IONIC

For examples of using this plugin in an [IONIC](https://ionicframework.com/) app please see [this thread](https://github.com/frontyard/cordova-plugin-exoplayer/issues/4).

For IONIC Native see [npm module](https://www.npmjs.com/package/@ionic-native/android-exoplayer).

## Contributing
    
1. Fork it
2. Create your feature branch off of current upstram branch (currently 2.0.0)
3. Commit and push your changes to that branch
4. Create new Pull Request

## More Info

For more information on setting up Cordova see [the documentation](http://cordova.apache.org/docs/en/latest/guide/cli/index.html)

For more info on plugins see the [Plugin Development Guide](http://cordova.apache.org/docs/en/latest/guide/hybrid/plugins/index.html)

General ExoPlayer [documentation](https://google.github.io/ExoPlayer/)

ExoPlayer [source code](https://github.com/google/ExoPlayer)
https://github.com/frontyard/cordova-plugin-exoplayer

## Donation
If this project helps you consider donating to support it!

[![paypal](https://cdn.rawgit.com/twolfson/paypal-github-button/1.0.0/dist/button.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2ZPEB7VKFG2CY)

## License

 The MIT License (MIT)

 Copyright (c) 2017-2018 Nedim Cholich

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
