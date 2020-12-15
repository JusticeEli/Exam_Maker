package com.justice.exammaker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class ImageTextReader {
    private static final String TAG = "ImageTextReader";
static      FirebaseVisionText mfirebaseVisionText;
static ImageTextActivity imageTextActivity;

    //get orientation of an image from exif data of image
    //and perform rotation as required to make it upright
    public static Bitmap getUprightImage(String imgUrl){

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgUrl);
        } catch (IOException e) {
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        int rotation = 0;
        switch (orientation) {
            case 3:
                rotation = 180;
                break;
            case 6:
                rotation = 90;
                break;
            case 8:
                rotation = 270;
                break;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);

        Bitmap bitmap  = BitmapFactory.decodeFile(imgUrl);
        //rotate image
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return bitmap;
    }
    //resize image to device width
    public static Bitmap resizeImage(Bitmap bitmap, Context ctx){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)ctx).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        return Bitmap.createScaledBitmap(bitmap, width, width, true);
    }
    //read text from image using Firebase ML kit api
    //on-device api
    public static void readTextFromImage(Bitmap bitmap, final TextView textView){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        final FirebaseVisionText[] firebaseVisionTextReturn = new FirebaseVisionText[1];
        FirebaseVisionTextRecognizer textRecognizer  = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        Log.d(TAG, "readTextFromImage: started reading text from image");
        Task<FirebaseVisionText> result =
                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                Log.d(TAG, "onSuccess:  success reading text");
                                setDataOnTextview(textView,firebaseVisionText);
                                imageTextActivity.firebaseVisionText=firebaseVisionText;             }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: failed reading text"+e.getMessage());

                                    }
                                });

        Log.d(TAG, "readTextFromImage: returned firebase vision text");


    }
    private static void setDataOnTextview(TextView textview, FirebaseVisionText firebaseVisionText){


        for (FirebaseVisionText.TextBlock textBlock :firebaseVisionText.getTextBlocks())
        {
            for (FirebaseVisionText.Line line:textBlock.getLines()){
                Log.d(TAG, line.getText()+"\n");
            }

        }


        textview.setText(firebaseVisionText.getText());

    }


    public static void passContext(ImageTextActivity mimageTextActivity) {
       imageTextActivity=mimageTextActivity;
    }
}
