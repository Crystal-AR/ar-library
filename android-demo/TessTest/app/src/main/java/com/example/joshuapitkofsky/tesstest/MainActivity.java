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

public class MainActivity extends AppCompatActivity {

    Bitmap image; //our image
    private TessBaseAPI mTess; //Tess API reference
    String datapath = ""; //path to folder containing language data file
    ArrayList<Rect> lst = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init image
        image = BitmapFactory.decodeResource(getResources(), R.drawable.text_test_four);

        //image = BitmapFactory.decodeResource(getResources(), R.drawable.url);

        datapath = getFilesDir() + "/tesseract/";
        Log.d("Storage dir is", Environment.getExternalStorageDirectory().toString());

        //make sure training data has been copied
        checkFile(new File(datapath + "tessdata/"));

        //init Tesseract API
        String language = "eng";

        mTess = new TessBaseAPI();
        mTess.init(datapath, language);
    }


    private void copyFile() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists() && dir.mkdirs()) {
            copyFile();
        }
        //The directory exists, but there is no data file in it
        if (dir.exists()) {
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFile();
            }
        }
    }

    public class CharCoord {
        public CharCoord(String ch, String x, String y, String X, String Y) {
            c = ch.charAt(0);
            x1 = Integer.parseInt(x);
            y1 = Integer.parseInt(y);
            x2 = Integer.parseInt(X);
            y2 = Integer.parseInt(Y);
        }

        public char c;
        int x1;
        int y1;
        int x2;
        int y2;

        @Override
        public String toString() {
            return String.valueOf(c) + " (" + String.valueOf(x1) + ", " + String.valueOf(y1) + ") (" + String.valueOf(x2) + ", " + String.valueOf(y2) + ")\n";
        }
    }

    private CharCoord[] textCoordinatesToClass(String input) {
        String[] lines = input.split("\n");
        CharCoord[] rtn = new CharCoord[lines.length];
        for (int i = 0; i < lines.length; ++i) {
            String[] coords = lines[i].split(" ");
            Log.d("lines", lines[i]);
            rtn[i] = new CharCoord(coords[0], coords[1], coords[2], coords[3], coords[4]);
        }
        Log.d("CharCoords", rtn[0].toString());
        return rtn;
    }


    CharCoord[] stuff;

    public void processImage(View view) {

        mTess.setImage(image);
        long startTime = System.nanoTime();
        String OCRresult = mTess.getUTF8Text();
        String coordinatesAsText = mTess.getBoxText(0);

        Pixa p = mTess.getWords();
        ArrayList<Rect> lst = p.getBoxRects();
        String[] parts = OCRresult.split("\\s+");
        for (int i = 0; i < parts.length; ++i) {
            if (i >= lst.size()) {
                Log.d("HELLO:", "Boop");
                break;
            }

            ImageView iv = (ImageView)findViewById(R.id.imageView);
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.ImageContainer);
            Button bt = new Button(this);

            int ourWidth = image.getWidth();
            int ourHeight = image.getHeight();
            //TODO
            // Take whitespace into consideration when finding the position of the button.
            // Currently we ignore whitespace and therefore the buttons are slightly 'off'.
//            int whitespaceHorizontal = 0;
//            int whitespaceVertical = 0;
//            if (ourHeight > ourWidth) {
//                whitespaceHorizontal = ourHeight - ourWidth;
//            } else {
//                whitespaceVertical = ourWidth - ourHeight;
//            }

            int scalarWidth = ourWidth / rl.getWidth();
            int scalarHeight = ourHeight / rl.getHeight();
            int scaledLeft = lst.get(i).left / scalarWidth;
            int scaledTop = lst.get(i).top / scalarHeight;

            bt.setX(scaledLeft);
            bt.setY(scaledTop);
            bt.setText(parts[i]);
            Log.d("button x", String.valueOf(scaledLeft));
            Log.d("button y", String.valueOf(scaledTop));


            rl.addView(bt);

            //iv.setImageResource(R.drawable.rect);

//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((lst.get(i).width())*8, lst.get(i).height()*8);
//            params.leftMargin = lst.get(i).left;
//            params.topMargin = lst.get(i).top;


//            Bitmap tempBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
         //   Canvas tempCanvas = new Canvas(tempBitmap);
            //Draw the image bitmap into the cavas
            //Paint myPaint = new Paint();
            //myPaint.setColor(Color.rgb(255, 0, 0));
          //  myPaint.setStrokeWidth(3);
      //      tempCanvas.drawBitmap(image, 0, 0, null);
        //    tempCanvas.drawRoundRect(new RectF(lst.get(i).left,lst.get(i).top,lst.get(i).width(),lst.get(i).height()), 2, 2, myPaint);
           // Log.d("HELLO:", String.valueOf(lst.get(i).left) + ", " + String.valueOf(lst.get(i).top) + ", " +  String.valueOf(lst.get(i).width())+ ", " + String.valueOf(lst.get(i).height()));
            // lst.get(4).left, lst.get(4).top, lst.get(4).width(), lst.get(4).height();
            // parts[4]

        }



        // todo allow user to take photo - check
        // draw view with border around it tap on view to follow url. Bounding box around text in one color and around URL in another color
        //
        // real time

        long stopTime = System.nanoTime();
        Log.d("Time Taken", String.valueOf(stopTime - startTime));
        Log.d("OCR'ed Text:", OCRresult);
        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setText(OCRresult);
        //processURL(OCRresult);


    }

    public void processURL(String string) {
        // separate input by spaces ( URLs don't have spaces )
        String[] parts = string.split("\\s+");

        // Attempt to convert each item into an URL.
        for (String item : parts)
            try {
                Log.d("ITEM",item);
                URL url = new URL(item);
                // If possible then replace with anchor...

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                startActivity(browserIntent);
            } catch (MalformedURLException e) {
                // If there was an URL that was not it!...
                System.out.print(item + " ");
            }

        System.out.println();
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private Uri mImageUri;

    public void dispatchTakePictureIntent(View view) {


        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = null;
        try {
            // place where to store camera taken picture
            photo = this.createTemporaryFile("picture", ".jpg");
            photo.delete();
        } catch (Exception e) {
            Log.v("Hi", "Can't create file to take picture!");

        }
        mImageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        //start camera intent
        this.startActivityForResult(intent, REQUEST_TAKE_PHOTO);


    }

    private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    public void grabImage(ImageView imageView) {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            Log.d("height", String.valueOf(bitmap.getHeight()));
            Log.d("width", String.valueOf(bitmap.getWidth()));
            //bitmap.getWidth() / 5

            Bitmap thumb = Bitmap.createScaledBitmap(bitmap, 400, (bitmap.getHeight() / bitmap.getWidth()) * 400, false);

            imageView.setImageBitmap(thumb);
            image = thumb;
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("ji", "Failed to load", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.imageView);
            //... some code to inflate/create/find appropriate ImageView to place grabbed image
            this.grabImage(mImageView);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

}