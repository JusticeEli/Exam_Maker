package com.justice.exammaker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;

public class ImageTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_text);

        TextView textView = findViewById(R.id.image_text);

        //get the selected image path from the intent
        String imagePath = getIntent().getStringExtra("IMAGE_PATH");
        //get upright image
        Bitmap bitmap = ImageTextReader.getUprightImage(imagePath);
        //extract text from image using Firebase ML kit on-device or cloud api
        ImageTextReader.readTextFromImage(bitmap, textView);
    }
}
