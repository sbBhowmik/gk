package com.rupik.a5000gkquestionsanswers;
import java.io.Serializable;

/**
 * Created by macmin5 on 14/11/16.
 */
public class GKItem implements Serializable {
    String question;
    String answer;
    String identifier;
    boolean isFavourite;
    boolean isCurrentAffairsType;
    int dateType = 0;

    public GKItem (String question, String ans, String id, boolean isCurrentAffairsType, int dateType)
    {
        this.question = question;
        this.answer = ans;
        this.identifier = id;
        this.isCurrentAffairsType = isCurrentAffairsType;
        this.dateType = dateType;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getDateType() {
        return dateType;
    }

    public void setDateType(int dateType) {
        this.dateType = dateType;
    }
}