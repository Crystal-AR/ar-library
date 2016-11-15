package com.example.joshuapitkofsky.tesstest;

import android.graphics.Rect;

/**
 * Created by thomas on 11/15/16.
 */

/*
     * basic class that collects a word and its position
     */
public class Word {
    public Word(String s, Rect rect) {
        str = s;
        x = rect.left;
        y = rect.top;
        width = rect.width();
        height = rect.height();
    }

    public String str;
    public int x;
    public int y;
    public int width;
    public int height;

    @Override
    public String toString() {
        return String.valueOf(str) + " (" + String.valueOf(x) + ", " + String.valueOf(y) + ") (" + String.valueOf(width) + ", " + String.valueOf(height) + ")\n";
    }
}
