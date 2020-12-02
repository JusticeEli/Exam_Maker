package com.justice.exammaker;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImagesRecyclerViewAdapter extends
        RecyclerView.Adapter<ImagesRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "ImagesRecyclerViewAdapt";

    public List<String> imageLst;
    private Context context;

    public ImagesRecyclerViewAdapter(List<String> list, Context ctx) {
        imageLst = list;
        context = ctx;
    }

    @Override
    public int getItemCount() {
        return imageLst.size();
    }

    @Override
    public ImagesRecyclerViewAdapter.ViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);

        ImagesRecyclerViewAdapter.ViewHolder viewHolder =
                new ImagesRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ImagesRecyclerViewAdapter.ViewHolder holder, final int position) {
        final int itemPos = position;

        final String imagePath = imageLst.get(position);
        Log.d(TAG, "onBindViewHolder: Image path: " + imagePath);
        final Bitmap bitmap = ImageTextReader.getUprightImage(imagePath);


        holder.image.setImageBitmap(ImageTextReader.resizeImage(bitmap, context));

        //set on click listeners
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: image clicked :"+imagePath);
                //process selected image
                startReadImageTextActivity(imagePath);
            }
        });
        holder.rotateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: rotating image ..."+imagePath);
                rotateImage();
              }

            private void rotateImage() {

                holder.rotateCounter%=4;
                new MainActivity.ImageRotator(position,holder.image,holder.rotateCounter).execute(imagePath);

            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private int rotateCounter=0;
        public ImageView image;
        public ImageView rotateImageView;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.camera_image);
            rotateImageView = (ImageView) view.findViewById(R.id.rotateImageView);


        }
    }

    public void startReadImageTextActivity(String imagePath) {
        Intent intent = new Intent(context, ImageTextActivity.class);
        intent.putExtra("IMAGE_PATH", imagePath);
        context.startActivity(intent);
    }
}
