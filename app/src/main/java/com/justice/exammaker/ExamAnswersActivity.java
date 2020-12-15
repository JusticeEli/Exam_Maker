package com.justice.exammaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Collections;

import static com.justice.exammaker.MyApplication.teachersAnswers;

public class ExamAnswersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExamAnswerAdapter answerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_answers);
        recyclerView=findViewById(R.id.recyclerView);


        create_a_list_of_answers();
        Collections.sort(MyApplication.teachersAnswers);

        answerAdapter=new ExamAnswerAdapter(teachersAnswers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(answerAdapter);



    }

    private void create_a_list_of_answers() {

        for (int i=0;i<=50;i++){

            Answer answer=new Answer();
            answer.setNumber(i);
            answer.setChoice("b");
            teachersAnswers.add(answer);
        }
    }
}