package com.rupik.a5000gkquestionsanswers;

import java.io.Serializable;

/**
 * Created by macmin5 on 17/01/17.
 */

public class MCQItem implements Serializable {
    String mcqQuestion;
    String answer;
    String mockTestUserAnswer;
    String detailedExplanation;


    public String getMockTestUserAnswer() {
        return mockTestUserAnswer;
    }

    public void setMockTestUserAnswer(String mockTestUserAnswer) {
        this.mockTestUserAnswer = mockTestUserAnswer;
    }

    public void setDetailedExplanation(String detailedExplanation) {
        this.detailedExplanation = detailedExplanation;
    }

    public String getDetailedExplanation() {
        return detailedExplanation;
    }

    public String getAnswer() {
        return answer;
    }

    public String getMcqQuestion() {
        return mcqQuestion;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setMcqQuestion(String mcqQuestion) {
        this.mcqQuestion = mcqQuestion;
    }
}
