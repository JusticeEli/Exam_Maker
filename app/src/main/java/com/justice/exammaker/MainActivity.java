package com.justice.exammaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BoardiesITSolutions.FileDirectoryPicker.DirectoryPicker;
import com.codekidlabs.storagechooser.StorageChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final int REQUEST_DIRECTORY_PICKER = 1;
    private static ImagesRecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.cam_images);

        LinearLayoutManager recyclerLayoutManager =
                new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this,
                        recyclerLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources()
                .getDrawable(android.R.drawable.divider_horizontal_dark, null));
        recyclerView.addItemDecoration(dividerItemDecoration);

        //to access camera photos, get READ_EXTERNAL_STORAGE permission
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_EXTERNAL_STORAGE}, 2);
        }

        startDirectoryPickerTwo();
        setUpScrollToDelete();
        //  startDirectoryPickerOne();
        //  displayPhotos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         switch (item.getItemId()){
            case R.id.answersItem:
                startActivity(new Intent(this,ExamAnswersActivity.class));

                break;
            case R.id.markItem:

                break;

        }
        return super.onOptionsItemSelected(item);


    }

    private void setUpScrollToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new File(recyclerViewAdapter.imageLst.get(viewHolder.getAdapterPosition())).delete();
                recyclerViewAdapter.imageLst.remove(viewHolder.getAdapterPosition());
                recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    private void startDirectoryPickerTwo() {
        // 1. Initialize dialog
        final StorageChooser chooser = new StorageChooser.Builder()
                // Specify context of the dialog
                .withActivity(MainActivity.this)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                // Define the mode as the FOLDER/DIRECTORY CHOOSER
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();

// 2. Handle what should happend when the user selects the directory !
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {

                displayPhotosFromDirectoryPickerTwo(path);
                Log.d(TAG, "onSelect: path: " + path);
            }
        });

// 3. Display File Picker whenever you need to !
        chooser.show();
    }

    private void displayPhotosFromDirectoryPickerTwo(String path) {
        recyclerViewAdapter = new
                ImagesRecyclerViewAdapter(getCameraImagesFromDirectoryPicker(this, path), this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void startDirectoryPickerOne() {
        Intent intent = new Intent(this, DirectoryPicker.class);
        startActivityForResult(intent, REQUEST_DIRECTORY_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.REQUEST_DIRECTORY_PICKER:
                if (resultCode == Activity.RESULT_OK) {
                    String currentPath = data.getStringExtra(DirectoryPicker.BUNDLE_CHOSEN_DIRECTORY);
                    displayPhotosFromDirectoryPickerOne(currentPath);
                }
                break;
        }
    }

    private void displayPhotos() {
        ImagesRecyclerViewAdapter recyclerViewAdapter = new
                ImagesRecyclerViewAdapter(getCameraImages(this), this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //  displayPhotos();

                }
            }
        }
    }

    private void displayPhotosFromDirectoryPickerOne(String path) {
        ImagesRecyclerViewAdapter recyclerViewAdapter = new
                ImagesRecyclerViewAdapter(getCameraImagesFromDirectoryPicker(this, path), this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    //get list of camera photos urls
    public static List<String> getCameraImages(Context context) {
        final String CAMERA_IMAGES = Environment
                .getExternalStorageDirectory().toString() + "/DCIM/Camera";
//////////////////////////////////
        final String CAMERA_IMAGES_ID = String.valueOf(
                CAMERA_IMAGES.toLowerCase().hashCode());

        final String[] projection = {MediaStore.Images.Media.DATA};
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {CAMERA_IMAGES_ID};
        final Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        ArrayList<String> result = new ArrayList<String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public static List<String> getCameraImagesFromDirectoryPicker(Context context, String path) {
        final String CAMERA_IMAGES = path;

//////////////////////////////////
        final String CAMERA_IMAGES_ID = String.valueOf(
                CAMERA_IMAGES.toLowerCase().hashCode());

        final String[] projection = {MediaStore.Images.Media.DATA};
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {CAMERA_IMAGES_ID};
        final Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        ArrayList<String> result = new ArrayList<String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }


    static public class ImageRotator extends AsyncTask<String, Void, Bitmap> {
        private ImageView myImageView;
        private int rotateCounter;
        private int position;

        public ImageRotator(int position, ImageView mImageView, int rotateCounter) {
            this.position = position;
            this.myImageView = mImageView;
            this.rotateCounter = rotateCounter;
        }

        protected Bitmap doInBackground(String... image_path) {
            String imgUrl = image_path[0];
            Log.d(TAG, "doInBackground: image rotating has started...");

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imgUrl);
            } catch (IOException e) {
            }

            int rotation = 0;
            switch (rotateCounter) {
                case 0:
                    rotation = 0;
                    break;
                case 1:
                    rotation = 90;
                    break;
                case 2:
                    rotation = 180;
                    break;
                case 3:
                    rotation = 270;
                    break;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);

            Bitmap bitmap  = BitmapFactory.decodeFile(imgUrl);
            //rotate image
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);


            Log.d(TAG, "doInBackground: rotate count"+rotateCounter);
            return bitmap;

        }



        protected void onPostExecute(Bitmap result) {
            Log.d(TAG, "onPostExecute: image rotated");
            myImageView.setImageBitmap(result);
            recyclerViewAdapter.notifyItemChanged(position);
            Log.d(TAG, "onPostExecute: rotate count"+rotateCounter);
        }
    }


    

}


