package com.example.venomousboxer.collegespaceapplication;

/*
 * Created by venomousboxer on 18/02/18.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder>{

    private Context mContext;
    private ArrayList<String> imageUris;
    private int mItemCount = 0;
    private String waterMark;
    private int colorId, alpha, size;
    private boolean underline;
    boolean watermarkEnabled;
    private float angle;
    private static final int PADDING = 8;

    public ImageViewAdapter(Context context, ArrayList<String> uris, String wt, int cid, int alp, int sz, boolean un, float an, boolean watermarkEnabled){
        mContext = context;
        imageUris = new ArrayList<>();
        imageUris = uris;
        mItemCount = imageUris.size();
        waterMark = wt;
        colorId = cid;
        alpha = alp;
        size = sz;
        underline = un;
        angle = an;
        this.watermarkEnabled = watermarkEnabled;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.image_list_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Uri uri = Uri.fromFile(new File(imageUris.get(position)));
        Picasso.with(mContext)
                .load(uri)
                .error(R.drawable.error)
                .placeholder(R.drawable.placeholder)
                .into(holder.mImageView);
        Bitmap source = ((BitmapDrawable)holder.mImageView.getDrawable()).getBitmap();

        int w = source.getWidth();
        int h = source.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, source.getConfig());
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        paint.setColor(colorId);
        paint.setTextSize(size);
        paint.setAlpha(alpha);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        Rect textBounds = new Rect();
        paint.getTextBounds(waterMark, 0, waterMark.length(), textBounds);
        int x = source.getWidth() - PADDING;
        int y = source.getHeight() - PADDING;

        canvas.rotate( -angle , x, y);
        canvas.drawText(waterMark, x, y, paint);
        source.recycle();
        holder.mImageView.setImageBitmap(result);
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    void swapUri(ArrayList<String> uri){
        imageUris = uri;
        mItemCount = imageUris.size();
        this.notifyDataSetChanged();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.list_items_image_view);
        }
    }
}
