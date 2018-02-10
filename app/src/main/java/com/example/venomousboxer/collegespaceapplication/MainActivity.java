package com.example.venomousboxer.collegespaceapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    // Constants for Capture image and Upload image intent
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int GET_FROM_GALLERY = 3;

    // Buttons used in MainActivity
    Button captureImage;
    Button markingButton;
    Button resetButton;
    Button uploadButton;
    Button saveImageButton;

    // ImageView in MainActivity
    ImageView image;

    // Bitmap variables for storing edited and non edited image respectively
    Bitmap k,capturedImageBitmap;

    // TAG for log messages in code
    final String TAG = "CollegeSpaceApplication";

    // Booleans for checking various actions
    static boolean uploadButtonClicked = false;
    static boolean checkImageUploaded = false;
    static boolean checkImageCaptured = false;

    // Preference variables
    String value;
    int size;
    String watermarkingString;
    boolean underlinePreference;
    int setAlphaValue;
    float rotationAngleValue;
    int xCoordinate = 0, yCoordinate = 0;

    // String for saving path of image file in which image is saved
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views initialised
        captureImage = findViewById(R.id.capture_image_button);
        markingButton = findViewById(R.id.watermarking_button);
        uploadButton = findViewById(R.id.upload_image_button);
        resetButton = findViewById(R.id.reset_image_button);
        saveImageButton = findViewById(R.id.save_image_button);
        image = findViewById(R.id.imageView);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        // Preference Variables Initialization

        value = sharedPreferences.getString(getResources().getString(R.string.color_pref_key),
                getResources().getString(R.string.c1));
        size = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.edit_text_preference_title),
                getResources().getString(R.string.default_value_of_size)));
        watermarkingString = sharedPreferences.getString(getResources().getString(R.string.pref_watermark_key),
                getResources().getString(R.string.default_value_of_watermark));
        underlinePreference = sharedPreferences.getBoolean(getResources().getString(R.string.underline_checkbox_key),
                getResources().getBoolean(R.bool.pref_underline_default_value));
        setAlphaValue = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.alpha_pref_key),
                getResources().getString(R.string.alpha_pref_default_value)));
        rotationAngleValue = Float.parseFloat(sharedPreferences.getString(getResources().getString(R.string.angle_pref_key),
                getResources().getString(R.string.angle_pref_default_value)));
        xCoordinate = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.x_coordinate_pref_key),
                getResources().getString(R.string.x_coordinate_pref_default_value)));
        yCoordinate = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.y_coordinate_pref_key),
                getResources().getString(R.string.y_coordinate_pref_default_value)));


        /*
        *  Capture image button call camera app of phone to capture image
        * */

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImageCaptured = true;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


        /*
        *  Marking button watermarks the captured image
        * */

        markingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkImageCaptured || checkImageUploaded){
                    Point p = new Point(xCoordinate , yCoordinate);
                    k = mark(k , watermarkingString , p , setColorFromPreferences(value) ,
                            setAlphaValue , size , underlinePreference , rotationAngleValue);
                    image.setImageBitmap(k);
                }
                else {
                    Toast.makeText(MainActivity.this,getText(R.string.error_toast_message),Toast.LENGTH_SHORT).show();
                }
            }
        });


        /*
        *  Reset button resets the image to original photo captured by camera
        * */

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((checkImageCaptured || checkImageUploaded ) && capturedImageBitmap != null){
                    k = capturedImageBitmap;
                    image.setImageBitmap(k);
                    k = ((BitmapDrawable)image.getDrawable()).getBitmap();
                    capturedImageBitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                }
                else{
                    Toast.makeText(MainActivity.this,getText(R.string.error_toast_message),Toast.LENGTH_SHORT).show();
                }
            }
        });


        /*
        *  Upload button uploads image in app from gallery
        * */

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadButtonClicked = true;
                checkImageUploaded = true;
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        GET_FROM_GALLERY);
            }
        });


        /*
        *  Save image button saves the watermarked image to gallery
        *  */

        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkImageUploaded || checkImageCaptured) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    addPicToGallery(bitmap);
                }
                else {
                    Toast.makeText(MainActivity.this,getText(R.string.error_toast_message),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*
    *  When we return from settings activity to our main activity
    *  onStart of MainActivity is called therefore we change the
    *  value of preference variables in this function
    *  */

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Preferences are changed when after we return from settings activity

        value = sharedPreferences.getString(getResources().getString(R.string.color_pref_key),
                getResources().getString(R.string.c1));
        size = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.edit_text_preference_title),
                getResources().getString(R.string.default_value_of_size)));
        watermarkingString = sharedPreferences.getString(getResources().getString(R.string.pref_watermark_key),
                getResources().getString(R.string.default_value_of_watermark));
        underlinePreference = sharedPreferences.getBoolean(getResources().getString(R.string.underline_checkbox_key),
                getResources().getBoolean(R.bool.pref_underline_default_value));
        setAlphaValue = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.alpha_pref_key),
                getResources().getString(R.string.alpha_pref_default_value)));
        rotationAngleValue = Float.parseFloat(sharedPreferences.getString(getResources().getString(R.string.angle_pref_key),
                getResources().getString(R.string.angle_pref_default_value)));
        xCoordinate = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.x_coordinate_pref_key),
                getResources().getString(R.string.x_coordinate_pref_default_value)));
        yCoordinate = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.y_coordinate_pref_key),
                getResources().getString(R.string.y_coordinate_pref_default_value)));
    }


    /*
    *  onActivityResult is called when capture image intent or upload image intent is dispatched
    *  */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && !uploadButtonClicked) {

            Bundle bundle = data.getExtras();
            if (bundle != null) {
                k = (Bitmap) bundle.get("data");
                if (k != null) {
                    capturedImageBitmap = k;
                    image.setImageBitmap(k);
                }
                else {
                    Toast.makeText(this, "There was some error while clicking the photograph",Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "There was some error while clicking the photograph");
                }
            }
            else {
                Toast.makeText(this, "There was some error while clicking the photograph",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "There was some error while clicking the photograph");
            }
        }
        else if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK && uploadButtonClicked) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                image.setImageBitmap(bitmap);
                k = bitmap;
                capturedImageBitmap = bitmap;
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Selecting picture cancelled");
                Toast.makeText(this, "Selecting picture cancelled", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
                e.printStackTrace();
            }
            uploadButtonClicked = false;
        }
    }


    private void storeImage(Bitmap bitmap) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile != null) {
            mCurrentPhotoPath = pictureFile.getAbsolutePath();
        }else {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }


    /*
    *  Function to create an image File to save edited image
    *  */

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files/Pictures");

        Log.i("image", "image saved to >>>" + mediaStorageDir.getAbsolutePath());

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageFileName);
        return mediaFile;
    }


    /*
    *  This method adds watermarked image to Gallery
    *  */

    private void addPicToGallery(Bitmap bitmap) {
        storeImage(bitmap);
    }



    /*
    *  Function to add watermark to the image
    * */

    public static Bitmap mark(Bitmap src, String watermark, Point location, int color,
                              int alpha, int size, boolean underline, float angle) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.rotate( angle , 0, 0);
        canvas.drawText(watermark, location.x, location.y, paint);

        return result;
    }


    /*
    *  Menu options are used to go to settings activity
    *  */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    *  function to set color of watermark through value chosen in list preference
    * */

    public int setColorFromPreferences(String value) {
        switch (value) {
            case "Indigo":
                return R.color.colorPrimaryDark;
            case "Dark Blue":
                return R.color.colorPrimary;
            case "Dark pink":
                return R.color.colorAccent;
            case "Gray":
                return R.color.colorOption1;
            case "Red":
                return R.color.colorOption2;
            case "Flesh Tint":
                return R.color.colorOption3;
            case "Light Green":
                return R.color.colorOption4;
            case "Light Blue":
                return R.color.colorOption5;
            case "Black":
                return R.color.colorOption6;
            case "Purple":
                return R.color.colorOption7;
            default:
                return R.color.colorOption1;
        }
    }
}