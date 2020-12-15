package com.justice.exammaker;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExamAnswerAdapter extends RecyclerView.Adapter<ExamAnswerAdapter.ViewHolder> {
    private List<Answer> answerList;
    private static final String TAG = "ExamAnswerAdapter";

    ExamAnswerAdapter(List<Answer> answerList) {
        this.answerList = answerList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_answer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(answerList.get(position));

    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView numberTxtView;
        private RadioGroup radioGroup;
        private RadioButton A_rb;
        private RadioButton B_rb;
        private RadioButton C_rb;
        private RadioButton D_rb;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            numberTxtView = itemView.findViewById(R.id.numberTxtView);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            A_rb = itemView.findViewById(R.id.aRB);
            B_rb = itemView.findViewById(R.id.bRB);
            C_rb = itemView.findViewById(R.id.cRB);
            D_rb = itemView.findViewById(R.id.dRB);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton=itemView.findViewById(checkedId);

                    Log.d(TAG, "onCheckedChanged: Question "+getAdapterPosition()+" radiobutton "+radioButton.getText().toString()+" checked");

                    answerList.get(getAdapterPosition()).setChoice(radioButton.getText().toString().toLowerCase());
                }
            });

        }

        private void bind(Answer answer) {
            numberTxtView.setText(answer.getNumber() + "");

            switch (answer.getChoice().toLowerCase()) {
                case "a":
                    A_rb.setChecked(true);
                    break;
                case "b":
                    B_rb.setChecked(true);
                    break;
                case "c":
                   C_rb.setChecked(true);
                    break;
                case "d":
                    D_rb.setChecked(true);
                    break;
            }


        }
    }
}
