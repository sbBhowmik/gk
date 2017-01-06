package com.rupik.a5000gkquestionsanswers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appodeal.ads.Appodeal;
import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.ads.banner.RevMobBanner;

public class MainMenuActivity extends AppCompatActivity {

    RevMob revmob;
    RevMobBanner banner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Appodeal.show(this, Appodeal.BANNER_BOTTOM);

        revmob = RevMob.startWithListener(this, new RevMobAdsListener() {
            @Override
            public void onRevMobSessionStarted() {
                loadBanner(); // Cache the banner once the session is started
            }
        },"586e3005e3b2a21b72a4b5d9");

        loadBanner();

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
    }

    //===Ad Methods

    public void loadBanner(){
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
                ViewGroup view = (ViewGroup) findViewById(R.id.bannerLayout);
                if(banner.getParent()!=null)
                    ((ViewGroup)banner.getParent()).removeView(banner);
                view.addView(banner);
                banner.show(); //This method must be called in order to display the ad.
            }
        });
    }

    public void releaseBanner(){
        banner.release();
    }
}