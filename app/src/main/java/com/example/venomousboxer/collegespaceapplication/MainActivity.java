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
import android.support.v4.content.FileProvider;
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

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int GET_FROM_GALLERY = 3;
    Button captureImage;
    Button markingButton;
    Button resetButton;
    Button uploadButton;
    Button saveImageButton;
    ImageView image;
    Bitmap k,capturedImageBitmap;
    final String TAG = "CollegeSpaceApplication";
    static boolean uploadButtonClicked = false;
    static boolean checkImageUploaded = false;
    static boolean checkImageCaptured = false;
    String value;
    int size;
    String watermarkingString;
    boolean underlinePreference;
    int setAlphaValue;
    float rotationAngleValue;
    int xCoordinate = 0, yCoordinate = 0;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureImage = findViewById(R.id.capture_image_button);
        markingButton = findViewById(R.id.watermarking_button);
        uploadButton = findViewById(R.id.upload_image_button);
        resetButton = findViewById(R.id.reset_image_button);
        saveImageButton = findViewById(R.id.save_image_button);
        image = findViewById(R.id.imageView);


        /*
        *  Capture image button call camera app of phone to capture image
        * */

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImageCaptured = true;
                dispatchTakePictureIntent();
            }
        });


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
        *  Marking button watermarks the captured image
        * */

        markingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG ,"value of color : " + value);
                Log.d(TAG ,"value of size : " + Integer.toString(size));
                Log.d(TAG ,"WaterMarking string is : "  + watermarkingString);
                Log.d(TAG ,"value of underlinePreferences : " + Boolean.toString(underlinePreference));
                Log.d(TAG ,"value of Alpha : " + Integer.toString(setAlphaValue));
                Log.d(TAG ,"value of Angle : " + Float.toString(rotationAngleValue));
                Log.d(TAG ,"value X coordinate is : " + Integer.toString(xCoordinate));
                Log.d(TAG ,"value of Y coordinate is : " + Integer.toString(yCoordinate));
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
                    image.setImageBitmap(capturedImageBitmap);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && !uploadButtonClicked) {

            try {
                k = (Bitmap) data.getExtras().get("data");
                capturedImageBitmap = k;
                image.setImageBitmap(k);
            }catch (NullPointerException n) {
                n.printStackTrace();
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
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
                e.printStackTrace();
            }
            uploadButtonClicked = false;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /*
    *  This method adds watermarked image to Gallery
    *  */

    private void addPicToGallery(Bitmap bitmap) {
        File file;
        try {
            file = createImageFile();
            try{

                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(file));
                this.sendBroadcast(mediaScanIntent);
                Log.d(TAG ,"Image broadcast completed");

            } catch (FileNotFoundException e) {
                Log.e(TAG ,"File not found error while saving image");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG ,"IOException while saving image");
                e.printStackTrace();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent ,REQUEST_IMAGE_CAPTURE);
            }
        }
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


    /*
    *   function of interface OnSharedPreferenceChangeListener we implement it to change the value of preferences dynamically
    * */

    /*
    * @Override
    * public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    *     if (key.equals(getResources().getString(R.string.color_pref_key))) {
    *         value = sharedPreferences.getString(getResources().getString(R.string.color_pref_key),
    *                 getResources().getString(R.string.c1));
    *         Log.d(TAG,"value of color : " + value);
    *     }
    *     else if (key.equals(getResources().getString(R.string.edit_text_preference_title))) {
    *         size = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.edit_text_preference_title),
    *                 getResources().getString(R.string.default_value_of_size)));
    *         Log.d(TAG,"value of size : " + Integer.toString(size));
    *     }
    *     else if (key.equals(getResources().getString(R.string.pref_watermark_key))) {
    *         watermarkingString = sharedPreferences.getString(getResources().getString(R.string.pref_watermark_key),
    *                 getResources().getString(R.string.default_value_of_watermark));
    *         Log.d(TAG,"WaterMarking string is : "  + watermarkingString);
    *     }
    *     else if (key.equals(getResources().getString(R.string.underline_checkbox_key))) {
    *         underlinePreference = sharedPreferences.getBoolean(getResources().getString(R.string.underline_checkbox_key),
    *                 getResources().getBoolean(R.bool.pref_underline_default_value));
    *         Log.d(TAG,"value of underlinePreferences : " + Boolean.toString(underlinePreference));
    *     }
    *     else if (key.equals(getResources().getString(R.string.alpha_pref_key))) {
    *         setAlphaValue = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.alpha_pref_key),
    *                 getResources().getString(R.string.alpha_pref_default_value)));
    *         Log.d(TAG,"value of Alpha : " + Integer.toString(setAlphaValue));
    *     }
    *
    *     // on change of preferences image are reset to original one
    *
    *     if ((checkImageCaptured || checkImageUploaded) && capturedImageBitmap != null){
    *         image.setImageBitmap(capturedImageBitmap);
    *     }
    *     else{
    *         Toast.makeText(MainActivity.this,getText(R.string.error_toast_message),Toast.LENGTH_SHORT).show();
    *     }
    *
    *     // work of marking button done once on change of preferences
    *
    *
    *     if (checkImageCaptured || checkImageUploaded){
    *         Point p = new Point(12,18);
    *         k = mark(k , watermarkingString , p , setColorFromPreferences(value) , setAlphaValue , size , underlinePreference);
    *         image.setImageBitmap(k);
    *     }
    *     else {
    *         Toast.makeText(MainActivity.this,getText(R.string.error_toast_message),Toast.LENGTH_SHORT).show();
    *     }
    *
    * }
    * */


//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(getResources().getString(R.string.color_pref_key))) {
//            String value = sharedPreferences.getString(getResources().getString(R.string.color_pref_key),
//                    getResources().getString(R.string.c1));
//            Point p = new Point(10,20);
//            k = mark(k , WATERMARK , p , setColorFromPreferences(value) , 23 , 20 , true);
//            image.setImageBitmap(k);
//        }
//    }


}