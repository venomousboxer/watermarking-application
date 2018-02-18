package com.example.venomousboxer.collegespaceapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gun0912.tedpicker.ImagePickerActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    // Constants for Capture image and Upload image intent
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;

    // Buttons used in MainActivity
    Button captureImage;
    Button markingButton;
    Button uploadButton;
    Button saveImageButton;

    // TAG for log messages in code
    final String TAG = "CollegeSpaceApplication";

    // Preference variables
    String value;
    int size;
    String watermarkingString;
    boolean underlinePreference;
    int setAlphaValue;
    float rotationAngleValue;
    int xCoordinate = 0, yCoordinate = 0;
    ImageViewAdapter mAdapter;

    ArrayList<String> tempUris;
    ArrayList<String> finalUris;

    // String for saving path of image file in which image is saved
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView imageViewRecyclerView;

        // Uris of images saved before watermarking
        tempUris = new ArrayList<>();
        // Uris of images saved after watermarking
        finalUris = new ArrayList<>();

        // Views initialised
        imageViewRecyclerView = findViewById(R.id.all_images_recycler_view);
        captureImage = findViewById(R.id.capture_image_button);
        markingButton = findViewById(R.id.watermarking_button);
        uploadButton = findViewById(R.id.upload_image_button);
        saveImageButton = findViewById(R.id.save_image_button);


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

        imageViewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ImageViewAdapter(MainActivity.this, tempUris, watermarkingString,
                setColorFromPreferences(value), setAlphaValue, size, underlinePreference, rotationAngleValue, true);
        imageViewRecyclerView.setAdapter(mAdapter);


        /*
        *  Capture image button call camera app of phone to capture image
        * */

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddingImages();
                }
            });


        /*
        *  Marking button watermarks the captured image
        * */

        markingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });


        /*
        *  Upload button uploads image in app from gallery
        * */

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddingImages();
            }
        });


        /*
        *  Save image button saves the watermarked image to gallery
        *  */

        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
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

    // Adding Images to PDF
    void startAddingImages() {
        // Check if permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
            } else {
                selectImages();
            }
        } else {
            selectImages();
        }
    }

    /**
     * Opens ImagePickerActivity to select Images
     */
    public void selectImages() {
        Intent intent = new Intent(MainActivity.this, ImagePickerActivity.class);

        //add to intent the URIs of the already selected images
        //first they are converted to Uri objects
        ArrayList<Uri> uris = new ArrayList<>(tempUris.size());
        for (String stringUri : tempUris) {
            uris.add(Uri.fromFile(new File(stringUri)));
        }
        // add them to the intent
        intent.putExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, uris);

        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }


    /*
    *  onActivityResult is called when capture image intent or upload image intent is dispatched
    *  */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {

            tempUris.clear();

            ArrayList<Uri> imageUris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            for (int i = 0; i < imageUris.size(); i++) {
                tempUris.add(imageUris.get(i).getPath());
            }
            Toast.makeText(MainActivity.this, R.string.toast_images_added, Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, "no. of images : " +
                    String.valueOf(tempUris.size()), Toast.LENGTH_SHORT).show();
            mAdapter.swapUri(tempUris);
        }
    }

    /**
     * Called after user is asked to grant permissions
     *
     * @param requestCode  REQUEST Code for opening permissions
     * @param permissions  permissions asked to user
     * @param grantResults bool array indicating if permission is granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImages();
                    Toast.makeText(MainActivity.this, R.string.toast_permissions_given, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_insufficient_permissions, Toast.LENGTH_SHORT).show();
                }
            }
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
        String imageFileName = "JPEG_" + timeStamp + "_" + ".PNG";
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageFileName);
        return mediaFile;
    }


    /*
    *  This method adds watermarked image to Gallery
    *  */

//    private void addPicToGallery() {
//        if (checkImageUploaded || checkImageCaptured) {
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
//            Bitmap bitmap = bitmapDrawable.getBitmap();
//            storeImage(bitmap);
//        }
//        else {
//            Toast.makeText(MainActivity.this,getText(R.string.error_toast_message),Toast.LENGTH_SHORT).show();
//        }
//    }

//    public static Bitmap mark(Bitmap src, String watermark, Point location, int color,
//                              int alpha, int size, boolean underline, float angle) {
//        int w = src.getWidth();
//        int h = src.getHeight();
//        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
//
//        Canvas canvas = new Canvas(result);
//        canvas.drawBitmap(src, 0, 0, null);
//        Paint paint = new Paint();
//        paint.setColor(color);
//        paint.setAlpha(alpha);
//        paint.setTextSize(size);
//        paint.setAntiAlias(true);
//        paint.setUnderlineText(underline);
//        canvas.rotate( angle , 0, 0);
//        canvas.drawText(watermark, location.x, location.y, paint);
//
//        return result;
//    }


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