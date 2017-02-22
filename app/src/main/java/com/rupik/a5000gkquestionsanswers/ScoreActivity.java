package com.rupik.a5000gkquestionsanswers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {

    ArrayList<MCQItem> mcqDataList;

    @Override
    public void onResume()
    {
        super.onResume();

        Appodeal.show(this, Appodeal.BANNER_BOTTOM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Appodeal.show(this, Appodeal.BANNER_BOTTOM);
        Appodeal.show(this, Appodeal.INTERSTITIAL);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mcqDataList = (ArrayList<MCQItem>)bundle.getSerializable("mcqDataList");

        //calculate score
        int score = 0;
        for(int i=0;i<mcqDataList.size();i++)
        {
            MCQItem item = mcqDataList.get(i);
            if(item.getAnswer()!=null && item.getMockTestUserAnswer()!=null) {
                if (item.getAnswer().toLowerCase().contains(item.getMockTestUserAnswer().toLowerCase()) && item.getMockTestUserAnswer().length() > 0) {
                    score += 1;
                }
            }
        }

        TextView scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        scoreTextView.setText(Integer.toString(score)+" Correct Answers of " + Integer.toString(mcqDataList.size()));

        int totalQuestions = mcqDataList.size();


        double percent = ((double)score/(double)totalQuestions)*100.0f;
        TextView percentageTextView = (TextView) findViewById(R.id.percentageTextView);
        percentageTextView.setText("Percentage: " + Double.toString(percent) + "%" );

        String title = "Quiz Master";
        if(percent<10.0)
        {
            title = "Novice";
        }
        else if(percent >=10 && percent<30)
        {
            title = "Amature";
        }
        else if(percent >=30 && percent<50)
        {
            title = "Amature+";
        }
        else if(percent >=50 && percent<60)
        {
            title = "Quiz Pro";
        }
        else if(percent >=60 && percent<70)
        {
            title = "Quiz Master";
        }
        else if(percent >=70 && percent<80)
        {
            title = "Quiz King";
        }
        else if(percent >=80 && percent<90)
        {
            title = "BADASS";
        }
        else if(percent >=90 && percent<98)
        {
            title = "BADASS Pro";
        }
        else if(percent >=98)
        {
            title = "Quiz GOD";
        }

        TextView scoreTitleTextView = (TextView) findViewById(R.id.scoreTitleTextView);
        scoreTitleTextView.setText("Title: " + title );

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

        Button retakeQuizButton = (Button)findViewById(R.id.retakeQuizBtn);
        retakeQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result","retake");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        ImageButton shareBtn = (ImageButton)findViewById(R.id.shareBtn);
        shareBtn.setVisibility(View.GONE);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RelativeLayout scoreLayout = (RelativeLayout)findViewById(R.id.scoreLayout);
                Bitmap bitmap = Bitmap.createBitmap(
                        scoreLayout.getWidth(),
                        scoreLayout.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bitmap);
                scoreLayout.draw(c);

//                String filePath = Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg";
                String filePath = ScoreActivity.this.getFilesDir().getPath() + "/" + "tempShare.png";
                File f = new File(filePath);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent shareIntent = new Intent();
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "GK Mock Test");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Find your GK Knowledge using 5000+ GK Questions & Answers https://play.google.com/store/apps/details?id=com.rupik.a5000gkquestionsanswers&hl=en");
                startActivity(Intent.createChooser(shareIntent, "Share Your Score"));
            }
        });
    }
}
