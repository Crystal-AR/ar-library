package com.example.joshuapitkofsky.tesstest;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import com.crystal_ar.crystal_ar.*;
//import com.example.arlibrary.*;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity {

    Bitmap image; //our image
    String datapath = ""; //path to folder containing language data file
//    Library ourLibrary;
    CrystalAR notSaba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init image
//        image = BitmapFactory.decodeResource(getResources(), R.drawable.text_test_four);
        image = BitmapFactory.decodeResource(getResources(), R.drawable.phonenumbers1);

        // make sure training data has been copied
//        checkFile(new File(datapath + "tessdata/"));
//
//        datapath = getFilesDir() + "/tesseract/";
//        ourLibrary = new Library(datapath);
        Context context = getApplicationContext();
        notSaba = new CrystalAR(context);
        notSaba.setLanguage("eng");

        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }
    }

//    private void copyFile() {
//        try {
//            //location we want the file to be at
//            String filepath = datapath + "/tessdata/eng.traineddata";
//
//            //get access to AssetManager
//            AssetManager assetManager = getAssets();
//
//            //open byte streams for reading/writing
//            InputStream instream = assetManager.open("tessdata/eng.traineddata");
//            OutputStream outstream = new FileOutputStream(filepath);
//
//            //copy the file to the location specified by filepath
//            byte[] buffer = new byte[1024];
//            int read;
//            while ((read = instream.read(buffer)) != -1) {
//                outstream.write(buffer, 0, read);
//            }
//            outstream.flush();
//            outstream.close();
//            instream.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void checkFile(File dir) {
//        //directory does not exist, but we can successfully create it
//        if (!dir.exists() && dir.mkdirs()) {
//            copyFile();
//        }
//        //The directory exists, but there is no data file in it
//        if (dir.exists()) {
//            String datafilepath = datapath + "/tessdata/eng.traineddata";
//            File datafile = new File(datafilepath);
//            if (!datafile.exists()) {
//                copyFile();
//            }
//        }
//    }

    public void initiateHandler(View view) {
        imageHandler handler = new imageHandler(view);
//        Thread thread = new Thread(notSaba.processImageAsync(handler, image));
        Thread thread = new Thread(notSaba.getProcessImageRunnable(handler, image));
        thread.setDaemon(true);
        thread.start();
    }

    private class imageHandler extends Handler {
        View view;

        public imageHandler(View view) {
            this.view = view;
        }
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case CrystalAR.IMAGE_PROCESSED:
                    processImage(this.view);
                    break;
            }
        }
    }

    public void processImage(View view) {
        ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Processing Image...");
        mDialog.setCancelable(false);
        mDialog.show();

//        notSaba.processImage(image);

        Word[] words = notSaba.getWords();
        for (int i = 0; i < words.length; ++i) {
            ImageView iv = (ImageView)findViewById(R.id.imageView);
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.ImageContainer);
            Button bt = new Button(this);
            float imgWidth = (float) image.getWidth();
            float imgHeight = (float) image.getHeight();
            float viewWidth = (float) rl.getWidth();
            float viewHeight = (float) rl.getHeight();

            float scale;
            if (imgHeight/viewHeight > imgWidth/viewWidth)
                scale = viewHeight / imgHeight;
            else
                scale = viewWidth / imgWidth;

            float xpos = (float) (words[i].x - imgWidth / 2.0);
            float ypos = (float) (words[i].y - imgHeight / 2.0);
            xpos = (float) (xpos * scale + viewWidth / 2.0);
            ypos = (float) (ypos * scale + viewHeight / 2.0);

            bt.setX(xpos);
            bt.setY(ypos);
            bt.setText(words[i].str);

            rl.addView(bt);
        }

        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setText(notSaba.getPrimitiveString());

        mDialog.dismiss();

//        ArrayList<String> phonenumbers = notSaba.getPhoneNumbers();
//        Log.d("#", Integer.toString(phonenumbers.size()));
//        for (String number : phonenumbers) {
//            Log.d("phone", number);
//            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
//            startActivity(intent);
//        }

//        openURLs();
    }

    public void openURLs() {
        // separate input by spaces ( URLs don't have spaces )
        URL[] urls = notSaba.getURLs();

        // Attempt to convert each item into an URL.
        for (URL url : urls) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
            startActivity(browserIntent);
        }
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private Uri mImageUri;

    public void dispatchTakePictureIntent(View view) {
        Context context = this;
        PackageManager pm = context.getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            File photo = null;
            try {
                // place where to store camera taken picture
                photo = this.createTemporaryFile("picture", ".jpg");
                photo.delete();
            } catch (Exception e) {
                Log.v("Error", "Can't create file to take picture!");
            }
            mImageUri = Uri.fromFile(photo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            //start camera intent
            this.startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }
    }

    private File createTemporaryFile(String part, String ext) throws Exception {
        File externalStoradeDir = Environment.getExternalStorageDirectory();
        File tempDir = new File(externalStoradeDir.getAbsolutePath() + "/.temp/");
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

            Bitmap thumb = Bitmap.createScaledBitmap(bitmap, 400, (bitmap.getHeight() / bitmap.getWidth()) * 400, false);

            imageView.setImageBitmap(thumb);
            image = thumb;
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("Error", "Failed to load", e);
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