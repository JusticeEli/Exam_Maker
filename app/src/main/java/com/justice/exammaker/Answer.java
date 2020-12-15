package com.justice.exammaker;

import java.util.Comparator;
import java.util.Objects;

public class Answer implements Comparable<Answer> {
    private int number;
    private String choice;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    @Override
    public int compareTo(Answer o) {
        return Integer.valueOf(number).compareTo(Integer.valueOf(o.getNumber()));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return number == answer.number &&
                Objects.equals(choice, answer.choice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, choice);
    }

    @Override
    public String toString() {
        return "Answer{" +
                "number=" + number +
                ", choice='" + choice + '\'' +
                '}';
    }
}
