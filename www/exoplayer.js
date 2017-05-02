/*global cordova, module*/

module.exports = {
    init: function (data) {
        return {
            show: function (successCallback, errorCallback) {
                cordova.exec(successCallback, errorCallback, "ExoPlayer", "show", [data]);
            },
            setText: function (text, successCallback, errorCallback) {
                cordova.exec(successCallback, errorCallback, "ExoPlayer", "setText", [text]);
            },
            setStream: function (url, successCallback, errorCallback) {
                cordova.exec(successCallback, errorCallback, "ExoPlayer", "setStream", [url]);
            },
            setStreamTitle: function (title, successCallback, errorCallback) {
                cordova.exec(successCallback, errorCallback, "ExoPlayer", "setStreamTitle", [title]);
            },
            play: function (successCallback, errorCallback) {
                cordova.exec(successCallback, errorCallback, "ExoPlayer", "play", []);
            },
            pause: function (successCallback, errorCallback) {
                cordova.exec(successCallback, errorCallback, "ExoPlayer", "pause", []);
            },
            seekTo: function (milliseconds, successCallback, errorCallback) {
                cordova.exec(successCallback, errorCallback, "ExoPlayer", "seekTo", [milliseconds]);
            },
            getState: function (successCallback, errorCallback) {
                cordova.exec(successCallback, errorCallback, "ExoPlayer", "getState", []);
            },
            close: function (successCallback, errorCallback) {
                cordova.exec(successCallback, errorCallback, "ExoPlayer", "close", [data]);
            }
        }
    }
};
