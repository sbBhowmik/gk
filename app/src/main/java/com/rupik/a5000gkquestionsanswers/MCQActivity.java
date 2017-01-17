package com.rupik.a5000gkquestionsanswers;

import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class MCQActivity extends AppCompatActivity {

    ArrayList <MCQItem> mcqDataList;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq);

        parseMCQRawFile();

        Button prevMCQQuestionBtn = (Button)findViewById(R.id.prevMCQQuestionBtn);
        prevMCQQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page -= 1;
                displayMCQ(page);
            }
        });

        Button nextMCQQuestionBtn = (Button)findViewById(R.id.nextMCQQuestionBtn);
        nextMCQQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page += 1;
                displayMCQ(page);
            }
        });
    }

    void parseMCQRawFile()
    {
        mcqDataList = new ArrayList<>();
        try {
            InputStream is = this.getResources().openRawResource(R.raw.gk_mcq);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1) ;
            String rawMCQText = new String(buffer);


            InputStream isA = this.getResources().openRawResource(R.raw.gk_mcq_answers);
            byte[] bufferA = new byte[isA.available()];
            while (isA.read(bufferA) != -1) ;
            String rawMCQTextA = new String(bufferA);
            rawMCQTextA = rawMCQTextA.replace(" ","");
            rawMCQTextA = rawMCQTextA.replace("\n","");
            rawMCQTextA = rawMCQTextA.replace("\t","");
            String splitAnswers[] = rawMCQTextA.split("[.]");
            int counter = 0;
            while (counter<splitAnswers.length-1)
            {
                if(counter==0)
                {
                    String slNo = splitAnswers[counter];
                    String nextSplit = splitAnswers[counter+1];
                    //now extract the first character
                    String answer = nextSplit.substring(0,1);

                }
                else {
                    String split = splitAnswers[counter];
                    String slNo = split.substring(1,split.length());
                    String nextSplit = splitAnswers[counter+1];
                    String answer = nextSplit.substring(0,1);

                }
                counter = counter+1;
            }


            //now split the full string by 1., 2., 3. etc. ie: [0-9]+[.][ ] Each of these are questions with options
            String splitString[] = rawMCQText.split("[0-9]+[.][ ]");
            for(int i=0; i<splitString.length; i++)
            {
                MCQItem item = new MCQItem();
                if(splitString[i].length()>0) {
                    item.setMcqQuestion(splitString[i]);
                    mcqDataList.add(item);
                }
            }


            SharedPreferences sp = getSharedPreferences("your_prefs", MODE_PRIVATE);
            page = sp.getInt("mcqQuestionPageNo",0);
            validatePrevNextBtns();
            if(page<mcqDataList.size())
                displayMCQ(page);
        }
        catch (Exception e)
        {

        }

    }

    void displayMCQ(int page)
    {
        hideAnswers();

        TextView mcqQuestionTV = (TextView) findViewById(R.id.mcqQuestionTV);
        MCQItem item = mcqDataList.get(page);
        mcqQuestionTV.setText(item.getMcqQuestion());

        SharedPreferences sp = getSharedPreferences("your_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("mcqQuestionPageNo", page);

        validatePrevNextBtns();
    }

    void hideAnswers()
    {
        ImageView answerA = (ImageView)findViewById(R.id.answerA);
        answerA.setVisibility(View.INVISIBLE);
        ImageView answerB = (ImageView)findViewById(R.id.answerB);
        answerB.setVisibility(View.INVISIBLE);
        ImageView answerC = (ImageView)findViewById(R.id.answerC);
        answerC.setVisibility(View.INVISIBLE);
        ImageView answerD = (ImageView)findViewById(R.id.answerD);
        answerD.setVisibility(View.INVISIBLE);
    }

    void validatePrevNextBtns()
    {
        Button prevMCQQuestionBtn = (Button)findViewById(R.id.prevMCQQuestionBtn);
        Button nextMCQQuestionBtn = (Button)findViewById(R.id.nextMCQQuestionBtn);
        if(page == 0)
        {
            prevMCQQuestionBtn.setEnabled(false);
        }
        else {
            prevMCQQuestionBtn.setEnabled(true);
        }
        if(page == mcqDataList.size()-1)
        {
            nextMCQQuestionBtn.setEnabled(false);
        }
        else {
            nextMCQQuestionBtn.setEnabled(true);
        }
    }
}
