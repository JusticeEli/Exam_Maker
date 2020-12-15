package com.justice.exammaker;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {


    public static List<Answer> teachersAnswers =new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

       for (int i=0;i<50;i++){
           Answer answer=new Answer();
           answer.setChoice("a");
           answer.setNumber(i);
           teachersAnswers.add(answer);
       }
    }
}
