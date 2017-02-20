package com.rupik.a5000gkquestionsanswers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MCQAllAnswersActivity extends AppCompatActivity {

    ArrayList<MCQItem> mcqDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcqall_answers);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mcqDataList = (ArrayList<MCQItem>)bundle.getSerializable("mcqDataList");

        MockTestAllAnswersAdapter adapter = new MockTestAllAnswersAdapter(this, mcqDataList);
        ListView mockTestAllAnswersListView = (ListView)findViewById(R.id.mockTestAllAnswersListView);
        mockTestAllAnswersListView.setAdapter(adapter);
    }
}
