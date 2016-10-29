package com.example.joshuapitkofsky.tesstest;
import java.net.URL;
import java.net.MalformedURLException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimingLogger;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;
import android.view.View;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    Bitmap image; //our image
    private TessBaseAPI mTess; //Tess API reference
    String datapath = ""; //path to folder containing language data file
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init image
        image = BitmapFactory.decodeResource(getResources(), R.drawable.test_url);

        datapath = getFilesDir()+ "/tesseract/";

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
        if (!dir.exists()&& dir.mkdirs()){
            copyFile();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFile();
            }
        }
    }



    public void processImage(View view){
        mTess.setImage(image);
        long startTime = System.nanoTime();
        String OCRresult = mTess.getUTF8Text();
        long stopTime = System.nanoTime();
        Log.d("Time Taken", String.valueOf(stopTime - startTime));
        Log.d("OCR'ed Text:", OCRresult);
        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setText(OCRresult);
        processURL(OCRresult);
    }

    public void processURL(String string){;
        // separate input by spaces ( URLs don't have spaces )
        String [] parts = string.split("\\s+");

        // Attempt to convert each item into an URL.
        for( String item : parts ) try {
            URL url = new URL(item);
            // If possible then replace with anchor...

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
            startActivity(browserIntent);
        } catch (MalformedURLException e) {
            // If there was an URL that was not it!...
            System.out.print( item + " " );
        }

        System.out.println();
    }


    }




