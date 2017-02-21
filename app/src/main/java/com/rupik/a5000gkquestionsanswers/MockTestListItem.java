package com.rupik.a5000gkquestionsanswers;

import java.util.ArrayList;

/**
 * Created by macmin5 on 21/02/17.
 */

public class MockTestListItem {
    String title;
    ArrayList<MCQItem> mcqItemArrayList;

    public ArrayList<MCQItem> getMcqItemArrayList() {
        return mcqItemArrayList;
    }

    public String getTitle() {
        return title;
    }

    public void setMcqItemArrayList(ArrayList<MCQItem> mcqItemArrayList) {
        this.mcqItemArrayList = mcqItemArrayList;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
