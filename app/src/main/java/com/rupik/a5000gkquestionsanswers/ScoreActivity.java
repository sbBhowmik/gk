package com.rupik.a5000gkquestionsanswers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {

    ArrayList<MCQItem> mcqDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mcqDataList = (ArrayList<MCQItem>)bundle.getSerializable("mcqDataList");

        //calculate score
        int score = 0;
        for(int i=0;i<mcqDataList.size();i++)
        {
            MCQItem item = mcqDataList.get(i);
            if(item.getAnswer().toLowerCase().contains(item.getMockTestUserAnswer().toLowerCase()))
            {
                score+=1;
            }
        }

        TextView scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        scoreTextView.setText(Integer.toString(score)+" Correct Answers of " + Integer.toString(mcqDataList.size()));

        Button showAnswersBtn = (Button)findViewById(R.id.MockTestShowAnswersButton);
        showAnswersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ScoreActivity.this, MCQAllAnswersActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mcqDataList", mcqDataList);
                i.putExtras(bundle);
                ScoreActivity.this.startActivity(i);
            }
        });
    }
}
