package com.rupik.a5000gkquestionsanswers;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appodeal.ads.Appodeal;

import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.ads.banner.RevMobBanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Map;
import com.inmobi.ads.*;
import com.inmobi.sdk.*;

public class MainMenuActivity extends AppCompatActivity{

    RevMob revmob;
    RevMobBanner banner;
    String adJsonString = "";

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

//        adView.loadAd();
        loadBanner();
    }

    void startDisplayingAds()
    {
        if(adJsonString.contains("appodeal")) {
            Appodeal.show(this, Appodeal.BANNER_BOTTOM);
        }

        if(adJsonString.contains("revmob")) {
            revmob = RevMob.startWithListener(this, new RevMobAdsListener() {
                @Override
                public void onRevMobSessionStarted() {
                    loadBanner(); // Cache the banner once the session is started
                }
            }, "586e3005e3b2a21b72a4b5d9");

            loadBanner();
        }

        if(adJsonString.contains("inmobi")) {
            InMobiSdk.init(this, "c18237e3971d4c10b346b1f5ecdd9cbd"); //'this' is used specify context
            InMobiBanner banner = (InMobiBanner) findViewById(R.id.banner);
            banner.setRefreshInterval(30);
            banner.load();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item )
    {
        switch (item.getItemId()){
            case android.R.id.home:
                break;
        }

        return  true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        boolean showAirPushAds = false;



        //====Back Button===
        ActionBar actionBar = getActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        //===


        //==========Fetch Ad type to display===========
        //https://quarkbackend.com/getfile/sohambhowmik/gk-ad

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    String urlStr = "https://quarkbackend.com/getfile/sohambhowmik/gk-ad";


                    // Create a URL for the desired page
                    URL url = new URL(urlStr);

                    // Read all the text returned by the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str;

                    while ((str = in.readLine()) != null) {
                        adJsonString = adJsonString+str;
                    }
                    in.close();


                } catch (MalformedURLException e) {
                    Log.d("MalformedURLException", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.d("IOERR", e.getLocalizedMessage());
                }
                finally {
                    MainMenuActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            SharedPreferences sp = getSharedPreferences("your_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();

                            editor.putString("adType", adJsonString);

                            editor.commit();
                            startDisplayingAds();
                        }
                    });

                }
            }
        });

        ///-------------------

        Button mcq_button = (Button)findViewById(R.id.mcq_button);
        mcq_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, MCQActivity.class);
                i.putExtra("isQuestionsType",false);
                startActivity(i);
            }
        });

        Button gkButton = (Button)findViewById(R.id.gkButton);
        gkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, QuizScreenActivity.class);
                i.putExtra("isCurrentAffairsType",false);
                startActivity(i);
            }
        });

        Button gk_currentButton = (Button)findViewById(R.id.Gk_CurrentAffairs);
        gk_currentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, QuizScreenActivity.class);
                i.putExtra("isCurrentAffairsType",true);
                startActivity(i);
            }
        });

        Button gk_datesButton = (Button)findViewById(R.id.datesButton);
        gk_datesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, QuizScreenActivity.class);
                i.putExtra("isCurrentAffairsType",true);
                i.putExtra("DateType",1);
                startActivity(i);
            }
        });
        Button mockTest_button = (Button)findViewById(R.id.mockTest_button);
        mockTest_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenuActivity.this, MockTestActivity.class);
//                i.putExtra("isQuestionsType",true);
                startActivity(i);
            }
        });


    }

    //===Ad Methods

    public void loadBanner(){
        if(revmob!=null) {
            banner = revmob.preLoadBanner(this, new RevMobAdsListener() {
                @Override
                public void onRevMobAdReceived() {
                    showBanner();
                    Log.i("RevMob", "Banner Ready to be Displayed"); //At this point, the banner is ready to be displayed.
                }

                @Override
                public void onRevMobAdNotReceived(String message) {
                    Log.i("RevMob", "Banner Not Failed to Load");
                }

                @Override
                public void onRevMobAdDisplayed() {
                    Log.i("RevMob", "Banner Displayed");
                }
            });
        }
    }

    public void showBanner(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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

}