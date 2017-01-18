package com.rupik.a5000gkquestionsanswers;

import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.ads.banner.RevMobBanner;
import com.revmob.ads.interstitial.RevMobFullscreen;

import java.io.InputStream;
import java.util.ArrayList;

import java.util.Map;
import com.inmobi.ads.*;
import com.inmobi.sdk.*;
//https://quarkbackend.com/getfile/sohambhowmik/gk-ad
public class MCQActivity extends AppCompatActivity {

    ArrayList <MCQItem> mcqDataList;
    int page = 0;
    RevMob revmob;
    RevMobBanner banner;
    private RevMobFullscreen fullscreen;
    private boolean fullscreenIsLoaded;

    @Override
    public void  onPause()
    {
        super.onPause();

        releaseBanner();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        loadBanner();
    }

    static  int revmobCount = 0;
    String adTypeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq);


        SharedPreferences sp = getSharedPreferences("your_prefs", MODE_PRIVATE);
        adTypeString = sp.getString("adType","appodeal");


        if(adTypeString.contains("appodeal")) {
            Appodeal.show(this, Appodeal.BANNER_BOTTOM);
        }

        if(adTypeString.contains("revmob")) {
            revmob = RevMob.startWithListener(this, new RevMobAdsListener() {
                @Override
                public void onRevMobSessionStarted() {
                    loadBanner(); // Cache the banner once the session is started
                    loadFullscreen(); // pre-cache it without showing it
                }
            }, "586e3005e3b2a21b72a4b5d9");

            loadBanner();
            loadFullscreen();
        }

        if(adTypeString.contains("inmobi")) {
            InMobiSdk.init(this, "c18237e3971d4c10b346b1f5ecdd9cbd"); //'this' is used specify context
            InMobiBanner banner = (InMobiBanner) findViewById(R.id.banner);
            banner.setRefreshInterval(30);
            banner.load();

            InMobiSdk.init(this, "9d961a7d067f485a8c512f0389f7cab9"); //'this' is used specify context
        }

            // ‘this’ is used to specify context, replace it with the appropriate context as needed.
            final InMobiInterstitial interstitial = new InMobiInterstitial(this, 1485272359976L, new InMobiInterstitial.InterstitialAdListener() {
                @Override
                public void onAdRewardActionCompleted(InMobiInterstitial ad, Map rewards) {
                }

                @Override
                public void onAdDisplayed(InMobiInterstitial ad) {
                }

                @Override
                public void onAdDismissed(InMobiInterstitial ad) {
                }

                @Override
                public void onAdInteraction(InMobiInterstitial ad, Map params) {
                }

                @Override
                public void onAdLoadSucceeded(final InMobiInterstitial ad) {
                }

                @Override
                public void onAdLoadFailed(InMobiInterstitial ad, InMobiAdRequestStatus requestStatus) {
                }

                @Override
                public void onUserLeftApplication(InMobiInterstitial ad) {
                }
            });

        if(adTypeString.contains("inmobi")) {
            interstitial.load();
        }




        parseMCQRawFile();

        Button prevMCQQuestionBtn = (Button)findViewById(R.id.prevMCQQuestionBtn);
        prevMCQQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page -= 1;
                displayMCQ(page);
                reloadBanner();
                if(revmobCount >= 9)
                {
                    if(adTypeString.contains("appodeal")) {
                        Appodeal.show(MCQActivity.this, Appodeal.INTERSTITIAL);
                        revmobCount = 0;
                    }
                    if(adTypeString.contains("inmobi")) {
                        if (interstitial.isReady()) {
                            interstitial.show();
                            revmobCount=0;
                            interstitial.load();
                        }
                    }

                    if(adTypeString.contains("revmob")) {
                        if (fullscreenIsLoaded) {
                            showFullscreen();
                            revmobCount = 0;
                        }
                    }
                }
                revmobCount++;
            }
        });

        Button nextMCQQuestionBtn = (Button)findViewById(R.id.nextMCQQuestionBtn);
        nextMCQQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page += 1;
                displayMCQ(page);
                reloadBanner();

                if(revmobCount >= 9)
                {
                    if(adTypeString.contains("appodeal")) {
                        Appodeal.show(MCQActivity.this, Appodeal.INTERSTITIAL);
                        revmobCount = 0;
                    }
                    if(adTypeString.contains("inmobi")) {
                        if (interstitial.isReady()) {
                            interstitial.show();
                            revmobCount = 0;
                            interstitial.load();
                        }
                    }

                    if(adTypeString.contains("revmob")) {
                        if (fullscreenIsLoaded) {
                            showFullscreen();
                            revmobCount = 0;
                        }
                    }
                }
                revmobCount++;
            }
        });

        Button mcqAnswersButton = (Button)findViewById(R.id.mcqAnswersButton);
        mcqAnswersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnswers();
            }
        });
    }

    void parseMCQRawFile()
    {
        mcqDataList = new ArrayList<>();
        try {

            //===Parse Answers===
            InputStream isA = this.getResources().openRawResource(R.raw.gk_mcq_answers);
            byte[] bufferA = new byte[isA.available()];
            while (isA.read(bufferA) != -1) ;
            String rawMCQTextA = new String(bufferA);
            rawMCQTextA = rawMCQTextA.replace(" ","");
            rawMCQTextA = rawMCQTextA.replace("\n","");
            rawMCQTextA = rawMCQTextA.replace("\t","");
            String splitAnswers[] = rawMCQTextA.split("[.]");
            int counter = 0;
//            ArrayList<String> answerArrayList = new ArrayList<>(600);
            String answerArrayList[] = new String[600];
            while (counter<splitAnswers.length-1)
            {
                String slNo ;
                String answer;
                if(counter==0)
                {
                    slNo = splitAnswers[counter];
                    String nextSplit = splitAnswers[counter+1];
                    //now extract the first character
                    answer = nextSplit.substring(0,1);

                }
                else {
                    String split = splitAnswers[counter];
                    slNo = split.substring(1,split.length());
                    String nextSplit = splitAnswers[counter+1];
                    answer = nextSplit.substring(0,1);

                }
                try {
                    int index = Integer.parseInt(slNo);
//                    answerArrayList.add(index, answer);
                    answerArrayList[index] = answer;
                }
                catch (Exception e)
                {
                    Log.d("Ex",e.getLocalizedMessage());
                }
                counter = counter+1;
            }
            //==============


            InputStream is = this.getResources().openRawResource(R.raw.gk_mcq);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1) ;
            String rawMCQText = new String(buffer);

            //now split the full string by 1., 2., 3. etc. ie: [0-9]+[.][ ] Each of these are questions with options
            String splitString[] = rawMCQText.split("[0-9]+[.][ ]");
            for(int i=0; i<splitString.length; i++)
            {
                MCQItem item = new MCQItem();
                if(splitString[i].length()>0) {
                    item.setMcqQuestion(splitString[i]);
                    item.setAnswer(answerArrayList[i]);
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
            Log.d("Ex",e.getLocalizedMessage());
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
        editor.commit();

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

    void showAnswers()
    {
        MCQItem item = mcqDataList.get(page);
        String answer = item.getAnswer();

        ImageView answerA = (ImageView)findViewById(R.id.answerA);
        answerA.setVisibility(View.VISIBLE);
        ImageView answerB = (ImageView)findViewById(R.id.answerB);
        answerB.setVisibility(View.VISIBLE);
        ImageView answerC = (ImageView)findViewById(R.id.answerC);
        answerC.setVisibility(View.VISIBLE);
        ImageView answerD = (ImageView)findViewById(R.id.answerD);
        answerD.setVisibility(View.VISIBLE);

        switch (answer)
        {
            case "A":
                answerA.setImageResource(R.drawable.green_tick);
                answerB.setImageResource(R.drawable.red_cross);
                answerC.setImageResource(R.drawable.red_cross);
                answerD.setImageResource(R.drawable.red_cross);
                break;
            case "B":
                answerA.setImageResource(R.drawable.red_cross);
                answerB.setImageResource(R.drawable.green_tick);
                answerC.setImageResource(R.drawable.red_cross);
                answerD.setImageResource(R.drawable.red_cross);
                break;
            case "C":
                answerA.setImageResource(R.drawable.red_cross);
                answerB.setImageResource(R.drawable.red_cross);
                answerC.setImageResource(R.drawable.green_tick);
                answerD.setImageResource(R.drawable.red_cross);
                break;
            case "D":
                answerA.setImageResource(R.drawable.red_cross);
                answerB.setImageResource(R.drawable.red_cross);
                answerC.setImageResource(R.drawable.red_cross);
                answerD.setImageResource(R.drawable.green_tick);
                break;
        }
    }

    void validatePrevNextBtns()
    {
        Button prevMCQQuestionBtn = (Button)findViewById(R.id.prevMCQQuestionBtn);
        Button nextMCQQuestionBtn = (Button)findViewById(R.id.nextMCQQuestionBtn);
        if(page == 0)
        {
            prevMCQQuestionBtn.setVisibility(View.INVISIBLE);
        }
        else {
            prevMCQQuestionBtn.setVisibility(View.VISIBLE);
        }
        if(page == mcqDataList.size()-1)
        {
            nextMCQQuestionBtn.setVisibility(View.INVISIBLE);
        }
        else {
            nextMCQQuestionBtn.setVisibility(View.VISIBLE);
        }
    }

    //===Ad Methods

    public void loadBanner(){
        if(revmob==null)
            return;
        banner = revmob.preLoadBanner(this, new RevMobAdsListener(){
            @Override
            public void onRevMobAdReceived() {
                showBanner();
                Log.i("RevMob","Banner Ready to be Displayed"); //At this point, the banner is ready to be displayed.
            }
            @Override
            public void onRevMobAdNotReceived(String message) {
                Log.i("RevMob","Banner Not Failed to Load");
            }
            @Override
            public void onRevMobAdDisplayed() {
                Log.i("RevMob","Banner Displayed");
            }
        });
    }

    public void showBanner(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Appodeal.hide(MCQActivity.this,Appodeal.BANNER_BOTTOM);
                ViewGroup view = (ViewGroup) findViewById(R.id.bannerLayout);
                if(banner.getParent()!=null)
                    ((ViewGroup)banner.getParent()).removeView(banner);
                view.addView(banner);
                banner.show(); //This method must be called in order to display the ad.
            }
        });
    }

    public void releaseBanner(){
        if(banner!=null)
            banner.release();
    }

    public void reloadBanner()
    {
        if(adTypeString.contains("inmobi")) {
            InMobiBanner banner = (InMobiBanner) findViewById(R.id.banner);
            banner.setRefreshInterval(30);
            banner.load();
        }
        if(adTypeString.contains("appodeal")) {
            Appodeal.show(this, Appodeal.BANNER_BOTTOM);
        }
        if(adTypeString.contains("revmob")) {
            releaseBanner();
            loadBanner();
        }
    }

    public void loadFullscreen() {
        if(revmob==null)
            return;
        //load it with RevMob listeners to control the events fired
        fullscreen = revmob.createFullscreen(this,  new RevMobAdsListener() {
            @Override
            public void onRevMobAdReceived() {
                Log.i("RevMob", "Fullscreen loaded.");
                fullscreenIsLoaded = true;
            }
            @Override
            public void onRevMobAdNotReceived(String message) {
                Log.i("RevMob", "Fullscreen not received.");
            }
            @Override
            public void onRevMobAdDismissed() {
                Log.i("RevMob", "Fullscreen dismissed.");
            }
            @Override
            public void onRevMobAdClicked() {
                Log.i("RevMob", "Fullscreen clicked.");
            }
            @Override
            public void onRevMobAdDisplayed() {
                Log.i("RevMob", "Fullscreen displayed.");
            }
        });
    }

    public void showFullscreen() {

        if(fullscreenIsLoaded) {
            fullscreen.show(); // call it wherever you want to show the fullscreen ad
        } else {
            Log.i("RevMob", "Ad not loaded yet.");
        }
    }
}
