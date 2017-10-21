package com.example.venomousboxer.collegespaceapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_IMAGE_CAPTURE = 1;
    Button b;
    Button markingButton;
    Intent i;
    ImageView image;
    String WATERMARK = "www.collegespace.com";
    Bitmap k;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = (Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(i,REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        markingButton = (Button) findViewById(R.id.button2);
        markingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Point p = new Point(10,20);
                k=mark(k,WATERMARK,p,200,23,20,true);
                image.setImageBitmap(k);
            }
        });
    }

    private String getPictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String timeStamp = sdf.format(new Date());
        return "plantPlacesImage"+timeStamp+".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        k = (Bitmap)data.getExtras().get("data");
        image = (ImageView) findViewById(R.id.imageView);
        image.setImageBitmap(k);
    }

    public void save(){
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureName = getPictureName();
        File imageFile = new File(pictureDirectory,pictureName);
        Uri imageUri = Uri.fromFile(imageFile);
    }

    public static Bitmap mark(Bitmap src, String watermark, Point location, int color, int alpha, int size, boolean underline) {
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
        canvas.drawText(watermark, location.x, location.y, paint);

        return result;
    }
}