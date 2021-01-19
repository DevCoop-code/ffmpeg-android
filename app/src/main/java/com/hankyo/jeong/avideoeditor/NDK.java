package com.hankyo.jeong.avideoeditor;

public class NDK {
    static {
        System.loadLibrary("sample-ffmpeg");
    }

    public native int scanning(String filepath);
}
