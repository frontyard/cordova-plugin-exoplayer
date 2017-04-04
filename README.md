# Cordova ExoPlayer Plugin

Currently supporting DASH and HLS and has an optional stylable header.

## Using

Create a new Cordova Project

    $ cordova create hello com.example.helloapp Hello
    
Install the plugin

    $ cd hello
    $ cordova plugin add https://github.com/frontyard/cordova-exoplayer-plugin
    

Edit `www/js/index.js` and add the following code inside `onDeviceReady`

```js
    var success = function(message) {
    };

    var failure = function(error) {
    };

    var params = {
        url: "http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0"
    };

    var player = exoplayer.init(params);
    player.show(success, failure);
```

Plugin methods
```js
{
    init(params),  // set player parameters
    show(successCallback, errorCallback) // show player and add main callbacks
    
    setText("some text\nsome other text") // change header text on intialized player
    setStream(url) // switch stream

    play() 
    pause()
    seekTo(milliseconds)

    videoProperties(success, error) // returns video information: {duration, current_position, is_playing}

    close() // --> close player
}
```

Player parameters set on initialization
```js
{
    user_agent: "PluginExoPlayer", 
    plugin_controls_visible: false, // exoplayer controls visibilty

    header: {   // top header panel
        height: 200, 
        padding: 30, 
        background_color: "#33F0F8FF",
        image_url: "https://s-media-cache-ak0.pinimg.com/originals/77/7a/df/777adf082fc125aa9490a3450192ec6c.jpg",
        text_color: "#BBFAA8EF",
        text_align: "center", // left, center or right
        text: "Lorem ipsum Ipsum\nlorem Lorem",
        text_size: 20
    },

    url: "https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8",
    full_screen: false, 
    aspect_ratio: "fill_screen" // fit_screen or fill_screen
    raw_touch_events: false
}
```

Example of a key events
```js
{
    "event_type":"key_event",
    "event_action":"ACTION_DOWN",
    "event_keycode":"KEYCODE_VOLUME_UP"
}

{   
    "event_type":"key_event",
    "event_action":"ACTION_UP",
    "event_keycode":"KEYCODE_VOLUME_UP"
}
```


Example of a raw_touch_events
```js
{
    "event_type":"touch_event",
    "event_action":"ACTION_DOWN",
    "event_axis_x":543,
    "event_axis_y":1321.8009033203125
}

{   "event_type":"touch_event",
    "event_action":"ACTION_MOVE",
    "event_axis_x":543,
    "event_axis_y":1320.5
}

{
    "event_type":"touch_event",
    "event_action":"ACTION_UP",
    "event_axis_x":543,
    "event_axis_y":1320.5
}
```

Install Android platform

    cordova platform add android
    
Run the code

    cordova run 

## More Info

For more information on setting up Cordova see [the documentation](http://cordova.apache.org/docs/en/latest/guide/cli/index.html)

For more info on plugins see the [Plugin Development Guide](http://cordova.apache.org/docs/en/latest/guide/hybrid/plugins/index.html)
