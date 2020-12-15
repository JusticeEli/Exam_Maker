package com.justice.exammaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ImageTextActivity extends AppCompatActivity {
    String rollNumber = new String();
    TextView textView;
    FirebaseVisionText firebaseVisionText;
    private static final String TAG = "ImageTextActivity";
    List<Answer> studentAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_text);

        textView = findViewById(R.id.image_text);

        //get the selected image path from the intent
        String imagePath = getIntent().getStringExtra("IMAGE_PATH");
        //get upright image
        Bitmap bitmap = ImageTextReader.getUprightImage(imagePath);

        ImageTextReader.passContext(this);
        //extract text from image using Firebase ML kit on-device or cloud api
        ImageTextReader.readTextFromImage(bitmap, textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.answersItem:
                startActivity(new Intent(this, ExamAnswersActivity.class));

                break;
            case R.id.markItem:
                startMarking();
                break;

        }
        return super.onOptionsItemSelected(item);


    }

    private void startMarking() {
        int counter = 0;
        StringBuilder sb = new StringBuilder();
        Log.d(TAG, "startMarking: started marking");
        for (FirebaseVisionText.TextBlock textBlock : firebaseVisionText.getTextBlocks()) {
            for (FirebaseVisionText.Line line : textBlock.getLines()) {
                if (counter >= 4 && counter <= 13) {
                    Log.d(TAG, "startMarking: started " + line.getText());
                    //we have reached the roll numbers
                    //extract them

                    sb.append(line.getText() + ":");

                    Log.d(TAG, "startMarking: string builder" + sb.toString());
                }
                if (counter == 13) {
                    startGettingRollNumber(sb.toString());
                }

                if (counter >= 15 && counter <= 65) {
                    startMarkingExam(line.getText());

                }

                counter++;
            }

        }

        markPapersUsingTeachersAnswers();
    }

    private void markPapersUsingTeachersAnswers() {
        Collections.sort(MyApplication.teachersAnswers);
        Collections.sort(studentAnswers);

        Log.d(TAG, "markPapersUsingTeachersAnswers:" + MyApplication.teachersAnswers);
        Log.d(TAG, "markPapersUsingTeachersAnswers:" + studentAnswers);
        int totalMarks = 0;
        Log.d(TAG, "markPapersUsingTeachersAnswers: started marking using teachers and student answers");

        for (int i = 0; i < 50; i++) {

            if (MyApplication.teachersAnswers.get(i).getChoice().equals(studentAnswers.get(i).getChoice())) {
                totalMarks++;
                Log.d(TAG, "markPapersUsingTeachersAnswers: Answer correct for: " + i);
            } else {
                Log.d(TAG, "markPapersUsingTeachersAnswers: Answer Wrong for: " + i);

            }


        }

        Log.d(TAG, "markPapersUsingTeachersAnswers: total marks is: " + totalMarks);
    }

    private void startMarkingExam(String line) {
        Log.d(TAG, "startMarkingExam: line :" + line);

        String data[] = line.split(",", 5);
        Answer answer = new Answer();
        answer.setNumber(Integer.valueOf(data[0].trim()));
        List<String> choices = new ArrayList<>();

        try {
            choices.add(data[1].trim().toLowerCase());
        } catch (Exception e) {
            Log.d(TAG, "startMarkingExam: Error: " + e.getMessage());
        }
        try {
            choices.add(data[2].toLowerCase().trim());
        } catch (Exception e) {
            Log.d(TAG, "startMarkingExam: Error: " + e.getMessage());
        }
        try {
            choices.add(data[3].toLowerCase().trim());
        } catch (Exception e) {
            Log.d(TAG, "startMarkingExam: Error: " + e.getMessage());
        }
        try {
            choices.add(data[4].toLowerCase().trim());
        } catch (Exception e) {
            Log.d(TAG, "startMarkingExam: Error: " + e.getMessage());
        }

         if (!choices.contains("a")) {
            answer.setChoice("a");
        } else if (!choices.contains("b")) {
            answer.setChoice("b");
        } else if (!choices.contains("c")) {
            answer.setChoice("c");
        } else if (!choices.contains("d")) {
            answer.setChoice("d");
        } else {
            answer.setChoice(null);
        }

        Log.d(TAG, "startMarkingExam: answer for question :" + answer.getNumber() + " is : " + answer.getChoice());
        studentAnswers.add(answer);

    }


    private void startGettingRollNumber(String data) {
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>();
        List<String> list4 = new ArrayList<>();


        List<List<String>> lists = new ArrayList<>();
        Log.d(TAG, "startGettingRollNumber: started getting roll no" + data);

        String[] rows = data.split(":", 10);
        Log.d(TAG, "startGettingRollNumber: print rows " + rows);
        for (String row : rows) {
            Log.d(TAG, "startGettingRollNumber: print row" + row);
            lists.add(Arrays.asList(row.split(",", 4)));
        }
        for (List<String> list : lists) {
            list1.add(list.get(0));
            list2.add(list.get(1));
            list3.add(list.get(2));
            list4.add(list.get(3));
        }
        Log.d(TAG, "startGettingRollNumber: list1 :" + list1 + " list2 " + list2
                + " list3 " + list3 + " list4 " + list4);

        fetchNumberFrom(list1);
        fetchNumberFrom(list2);
        fetchNumberFrom(list3);
        fetchNumberFrom(list4);


    }

    private void fetchNumberFrom(List<String> list1) {
        for (int i = 0; i < 10; i++) {
            try {
                if (i == 5 && list1.get(i).toUpperCase().equals("S")) {

                } else {
                    if ((Integer.parseInt(list1.get(i))) != i) {
                        throw new Exception("Number At Invalid Index");
                    }
                    int number = (Integer.parseInt(list1.get(i)));

                }
            } catch (Exception e) {

                ////Exception occurs on the number which is part of the roll number
                Log.d(TAG, "startGettingRollNumber: Error at index " + i + " " + e.getMessage());
                rollNumber += i;
                Log.d(TAG, "startGettingRollNumber: Roll number " + rollNumber);
                return;
            }
        }
    }


}
