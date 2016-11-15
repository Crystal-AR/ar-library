package com.example.joshuapitkofsky.tesstest;

import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimingLogger;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Toast;

public class Library {
    private TessBaseAPI mTess; //Tess API reference
    String datapath = ""; //path to folder containing language data file
    ArrayList<Rect> lst = null;
    String OCRresult;

    Library(String path) {
        String language = "eng";
        mTess = new TessBaseAPI();
        datapath = path;
        mTess.init(datapath, language);
    }

    public class Word {
        public Word(String s, Rect rect) {
            str = s;
            x = rect.left;
            y = rect.top;
            w = rect.width();
            h = rect.height();
        }

        public String str;
        public int x;
        public int y;
        public int w;
        public int h;

        @Override
        public String toString() {
            return String.valueOf(str) + " (" + String.valueOf(x) + ", " + String.valueOf(y) + ") (" + String.valueOf(w) + ", " + String.valueOf(h) + ")\n";
        }
    }

    public void processImage(Bitmap image) {
        mTess.setImage(image);
        long startTime = System.nanoTime();
        OCRresult = mTess.getUTF8Text();
    }

    public Word[] getWordCoordinates() {
        Pixa p = mTess.getWords();
        ArrayList<Rect> lst = p.getBoxRects();
        String[] parts = OCRresult.split("\\s+");
        Word[] rtn = new Word[lst.size()];
        for (int i = 0; i < lst.size(); ++i) {
            if (i >= parts.length)
                break;
            rtn[i] = new Word(parts[i], lst.get(i));
        }
        return rtn;
    }
}