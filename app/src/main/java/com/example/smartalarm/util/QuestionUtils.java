package com.example.smartalarm.util;

import java.util.ArrayList;
import java.util.List;

public class QuestionUtils {
    public String query;
    public String[] answer;
    public String rightAnswer;

    public QuestionUtils() {
    }

    public QuestionUtils(String query, String[] answer, String rightAnswer) {
        this.query = query;
        this.answer = answer;
        this.rightAnswer = rightAnswer;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public String getQuery() {
        return query;
    }

    public String[] getAnswer() {
        return answer;
    }

    public List<QuestionUtils> getListQuestions() {

        List<QuestionUtils> listQuestions = new ArrayList<>();
            listQuestions.add(new QuestionUtils("1 + 1 = ?", new String[]{"0", "1", "2"}, "2"));
            listQuestions.add(new QuestionUtils("6 + 2 = ?", new String[]{"7", "-8", "8"}, "8"));
            listQuestions.add(new QuestionUtils("2 + 5 = ?", new String[]{"9", "7", "2"}, "7"));
            listQuestions.add(new QuestionUtils("8 + 7 = ?", new String[]{"15", "8", "13"}, "15"));
        return listQuestions;
    }
}
