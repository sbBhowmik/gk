package com.rupik.a5000gkquestionsanswers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.RevMobUserGender;
import com.revmob.ads.banner.RevMobBanner;
import com.revmob.ads.interstitial.RevMobFullscreen;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import java.util.Map;
import com.inmobi.ads.*;
import com.inmobi.sdk.*;

public class QuizScreenActivity extends AppCompatActivity {

    ArrayList<GKItem> dataList;
    boolean isDataFiltered = false;
    boolean isCurrentAffairsType=false;
    static  int adCount = 0;
    int dateType = 0;
    private RevMobFullscreen fullscreen;
    private boolean fullscreenIsLoaded;

    RevMob revmob;
    RevMobBanner banner;

    String adTypeString;
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
        if(adTypeString.contains("appodeal")) {
            Appodeal.show(this, Appodeal.BANNER_BOTTOM);
        }
        if(adTypeString.contains("revmob")) {
            loadBanner();
        }

        if(adTypeString.contains("inmobi")) {
            InMobiBanner banner = (InMobiBanner) findViewById(R.id.banner);
            banner.setRefreshInterval(30);
            banner.load();
        }
    }

//    @Override
//    public void onBackPressed() {
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_screen);

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

        //====Ads====End ====


        boolean isProfileUpdated = sp.getBoolean("isProfileUpdated", false);
        isProfileUpdated = false;
        if(isProfileUpdated) {
            String email = sp.getString("usr_mail", "email");
            String age = sp.getString("usr_age", "ageStr");
            String interestsStr = sp.getString("usr_interests", "interestsString");
            String bday = sp.getString("usr_bday", "bDayText");
            String page = sp.getString("usr_page", "page");


            revmob.setUserEmail(email);
            revmob.setUserGender(RevMobUserGender.FEMALE);
            revmob.setUserPage(page);
            revmob.setUserAgeRangeMax(21);
            revmob.setUserAgeRangeMin(18);
            revmob.setUserBirthday(new GregorianCalendar(1990, 01, 01));
            ArrayList<String> interests = new ArrayList<String>();
            interests.add("games");
            interests.add("mobile");
            interests.add("advertising");
            revmob.setUserInterests(interests);
        }

//        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
//        int videoAdCount = sp.getInt("videoAdCount",0);
//        if(videoAdCount==3) {
//            videoAdCount = 0;
//            Appodeal.show(this, Appodeal.NON_SKIPPABLE_VIDEO);
//        }
//        else {
//            videoAdCount++;
//        }
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putInt("videoAdCount", videoAdCount);
//        editor.commit();

        //---
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 19890, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 15);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        }
        //---

        isCurrentAffairsType = this.getIntent().getExtras().getBoolean("isCurrentAffairsType");
        dateType = this.getIntent().getExtras().getInt("DateType",0);

        if(isCurrentAffairsType)
        {
            populateDataSourceForCurrentAffairs();
        }
        else {
            populateDatasource();
        }

        Button prevBtn = (Button)findViewById(R.id.prevBtn);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pageNo = fetchPageNumber();
                pageNo -= 1;
                savePageNoToSharedPrefs(pageNo);
                displayquestionsForPage(pageNo);

                if(adTypeString.contains("inmobi")) {
                    InMobiBanner banner = (InMobiBanner) findViewById(R.id.banner);
                    banner.setRefreshInterval(30);
                    banner.load();
                }

                if(adCount==9)
                {
                    adCount=0;
                    if(adTypeString.contains("inmobi")) {
                        if (interstitial.isReady())
                            interstitial.show();
                    }

                    if(adTypeString.contains("appodeal")) {
                        Appodeal.show(QuizScreenActivity.this, Appodeal.INTERSTITIAL);
                    }
                }
                else {
                    adCount++;
                }
            }
        });

        Button nextBtn = (Button)findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pageNo = fetchPageNumber();
                pageNo += 1;
                savePageNoToSharedPrefs(pageNo);
                displayquestionsForPage(pageNo);

                if(adTypeString.contains("inmobi")) {
                    InMobiBanner banner = (InMobiBanner) findViewById(R.id.banner);
                    banner.setRefreshInterval(30);
                    banner.load();
                }

                if(adCount==9)
                {
                    adCount=0;

                    if(adTypeString.contains("inmobi")) {
                        if (interstitial.isReady())
                            interstitial.show();
                    }

                    if(adTypeString.contains("appodeal")) {
                        Appodeal.show(QuizScreenActivity.this, Appodeal.INTERSTITIAL);
                    }
                }
                else {
                    adCount++;
                }
            }
        });

        final ImageButton favFilterIB = (ImageButton)findViewById(R.id.filterFavIB);
        favFilterIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //

                if(adTypeString.contains("inmobi")) {
                    InMobiBanner banner = (InMobiBanner) findViewById(R.id.banner);
                    banner.setRefreshInterval(30);
                    banner.load();
                }

                if(adTypeString.contains("appodeal")) {
                    Appodeal.show(QuizScreenActivity.this, Appodeal.BANNER_BOTTOM);
                }

                SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
//                boolean isProfileUpdated = sp.getBoolean("isProfileUpdated", false);
//                if(!isProfileUpdated)
//                {
//                    Intent i = new Intent(QuizScreenActivity.this, UserProfileActivity.class);
//                    startActivity(i);
//
                    reloadBanner();
//                    return;
//                }
                //
                if(isDataFiltered)
                {
                    validateFavouritesBtn();
                    favFilterIB.setImageResource(R.drawable.fav_icon_selected);
                    isDataFiltered = false;
                    int pageNo = fetchPageNumber();
                    displayquestionsForPage(pageNo);
                    displayControls(true);
                }
                else {
                    favFilterIB.setImageResource(R.drawable.close_btn);
                    isDataFiltered = true;
                    displayFavouriteQuestions();
                }
            }
        });
    }

    void savePageNoToSharedPrefs(int pageNo)
    {
        String prefsKey = "pageNumber";
        if(isCurrentAffairsType) {
            if (dateType == 1) {
                prefsKey = "pageNumber_CA_DateType";
            } else {
                prefsKey = "pageNumber_CA";
            }
        }
        validatePrevNextButtons(pageNo);
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(prefsKey, pageNo);
        editor.commit();
    }

    void validatePrevNextButtons(int pageNo)
    {
        Button prevBtn = (Button)findViewById(R.id.prevBtn);
        Button nextBtn = (Button)findViewById(R.id.nextBtn);
        if(pageNo == 1)
        {
            prevBtn.setEnabled(false);
        }
        else{
            prevBtn.setEnabled(true);
        }

        int questionEndId = pageNo * 4;
        if(questionEndId >= dataList.size())
        {
            nextBtn.setEnabled(false);
        }
        else {
            nextBtn.setEnabled(true);
        }
    }

    void updatePageNumber(int pageNo){
        TextView pageNoTV = (TextView)findViewById(R.id.pageNoTextView);
        pageNoTV.setText("Page "+Integer.toString(pageNo));
    }

    int fetchPageNumber ()
    {
        String prefsKey = "pageNumber";
        if(isCurrentAffairsType)
        {
            if(dateType==1)
            {
                prefsKey = "pageNumber_CA_DateType";   
            }
            else {
                prefsKey = "pageNumber_CA";
            }
        }
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        int pNo = sp.getInt(prefsKey, 1);
        validatePrevNextButtons(pNo);
        return  pNo;
    }

    void displayControls(boolean shouldDisplay)
    {
        TextView pageNoTV = (TextView)findViewById(R.id.pageNoTextView);
        Button prevBtn = (Button)findViewById(R.id.prevBtn);
        Button nextBtn = (Button)findViewById(R.id.nextBtn);
        if(shouldDisplay)
        {
            pageNoTV.setVisibility(View.VISIBLE);
            prevBtn.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.VISIBLE);
        }
        else {
            pageNoTV.setVisibility(View.INVISIBLE);
            prevBtn.setVisibility(View.INVISIBLE);
            nextBtn.setVisibility(View.INVISIBLE);
        }
    }

    void displayFavouriteQuestions()
    {
        displayControls(false);
        ArrayList<GKItem> questionsList = new ArrayList<>();
        for(int i=0;i<dataList.size();i++)
        {
            GKItem item = dataList.get(i);
            if(item.isFavourite())
            {
                questionsList.add(item);
            }
        }

        ListView gkListView = (ListView)findViewById(R.id.gkListView);
        QuizListAdapter quizListAdapter = new QuizListAdapter(this, questionsList);
        gkListView.setAdapter(quizListAdapter);
    }

    static int revmobCount = 0;
    void displayquestionsForPage(int page)
    {

        if(revmobCount >= 7)
        {
            if(fullscreenIsLoaded)
            {
                showFullscreen();
                revmobCount=0;
            }
        }
        revmobCount++;

        validateFavouritesBtn();
        updatePageNumber(page);

        int questionStartId = (page-1) * 4;
        int questionEndId = page * 4;

        ArrayList<GKItem> questionsList = new ArrayList<>(4);
        if(questionStartId < dataList.size())
        {

            if(questionEndId >= dataList.size())
            {
                questionEndId = dataList.size()-1;
            }
        }
        else {
            //invalid - Ideally next page button should be disabled here...
            return;
        }

        for(int i=questionStartId; i<questionEndId; i++)
        {
            GKItem item = dataList.get(i);
            questionsList.add(item);
        }

        ListView gkListView = (ListView)findViewById(R.id.gkListView);
        QuizListAdapter quizListAdapter = new QuizListAdapter(this, questionsList);
        gkListView.setAdapter(quizListAdapter);
    }

    void validateFavouritesBtn()
    {
        ArrayList<String> favouritesArr = fetchAllFavourites(isCurrentAffairsType);
        ImageButton favFilterIB = (ImageButton)findViewById(R.id.filterFavIB);
        if(favouritesArr.size()==0)
        {
            favFilterIB.setVisibility(View.INVISIBLE);
        }
        else {
            favFilterIB.setVisibility(View.VISIBLE);
        }
    }

    ArrayList<String> fetchAllFavourites(boolean isCAType)
    {
        String favSize;
        String favName;
        if(isCAType)
        {
            if(dateType==1)
            {
                favSize = "fav_size_CA_DateType";
                favName = "favourite_CA_DateType";
            }
            else {
                favSize = "fav_size_CA";
                favName = "favourite_CA_";
            }
        }
        else {
            favSize = "fav_size";
            favName = "favourite_";
        }
        ArrayList<String> favouritesArr = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        int fav_size = sp.getInt(favSize,0);
        for(int i=1;i<=fav_size;i++) {
            String storedId = sp.getString(favName + Integer.toString(i), "");
            favouritesArr.add(storedId);
        }

        return favouritesArr;
    }

    int fetchFileResource()
    {
        int fileResource = R.raw.gk_dates_wh;
        switch (dateResourceFileIndex){
            case 0:
                fileResource = R.raw.gk_dates_bc;
                break;
            case 1:
                fileResource = R.raw.gk_dates_1;
                break;
            case 2:
                fileResource = R.raw.gk_dates_wh_bc;
                break;
            case 3:
                fileResource = R.raw.gk_dates_wh;
                break;
        }

        return fileResource;
    }

    int dateResourceFileIndex = -1;
    int counter = 0;

    void populateDataSourceForCurrentAffairs()
    {
        int fileResource = R.raw.gk_1;


        boolean shouldRefreshDataList = true;
        if(dateType==1 && dateResourceFileIndex==-1)
        {
            dateResourceFileIndex = 0;
            fileResource = fetchFileResource();
            dateResourceFileIndex++;
        }
        else if(dateType==1 && dateResourceFileIndex>0){
            shouldRefreshDataList = false;
            fileResource = fetchFileResource();
            dateResourceFileIndex++;
        }


        if(shouldRefreshDataList) {
            dataList = new ArrayList<>();
            counter=0;
        }

        try {
            InputStream is = this.getResources().openRawResource(fileResource);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1) ;
            final String rawGKText = new String(buffer);

            String[] separated = rawGKText.split("~");
            int tmp =0;
            for(int i=0;i<separated.length; i++)
            {
                String combinedString = separated[i];
                if(dateType == 1) {
                    if(dateResourceFileIndex == 1 || dateResourceFileIndex == 3)
                    {
                        combinedString = combinedString.replace(",", " BC - ");
                    }
                    else {
                        combinedString = combinedString.replace(",", " AD - ");
                    }
                }
                GKItem item = new GKItem(combinedString, "", Integer.toString(i+counter),true, dateType);
                tmp=i;

                ArrayList<String> favouritesArr = fetchAllFavourites(true);

                //---
                item.setFavourite(false);
                for(int j=0;j<favouritesArr.size();j++)
                {
                    if(item.getIdentifier().equals(favouritesArr.get(j)))
                    {
                        item.setFavourite(true);
                        break;
                    }
                }
                //---

                dataList.add(item);
            }

            counter+=tmp;

            if(dateType==0) {
                int pageNo = fetchPageNumber();
                displayquestionsForPage(pageNo);
            }
            else if(dateType==1 && dateResourceFileIndex==3)
            {
                int pageNo = fetchPageNumber();
                displayquestionsForPage(pageNo);
            }
        }
        catch (Exception e)
        {

        }

        if(dateType==1 && dateResourceFileIndex<=3)
        {
            populateDataSourceForCurrentAffairs();
        }
        if(dateResourceFileIndex==4)
        {
            dateResourceFileIndex = -1;
        }
    }

    void populateDatasource()
    {
        try {
            dataList = new ArrayList<>();

            InputStream is = this.getResources().openRawResource(R.raw.final_edited_gk);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1);
            final String rawGKText = new String(buffer);
//            final String rawGKText = fetchRawDataString();

            //

            String[] separated = rawGKText.split("~");
            for(int i=0;i<separated.length; i++)
            {
                String combinedString = separated[i];
                combinedString = combinedString.replace("\r","");
                String[] partStrings = combinedString.split(">,");
                if(partStrings.length == 3)
                {
                    GKItem item = new GKItem(partStrings[1], partStrings[2],partStrings[0],false, 0);
                    ArrayList<String> favouritesArr = fetchAllFavourites(false);

                    //---
//                    boolean shouldAdd = false;
                    item.setFavourite(false);
                    for(int j=0;j<favouritesArr.size();j++)
                    {
                        if(item.getIdentifier().equals(favouritesArr.get(j)))
                        {
//                            shouldAdd = true;
                            item.setFavourite(true);
                            break;
                        }
                    }
                    //---

//                    if(isFiltered && shouldAdd) {
//                        dataList.add(item);
//                    }
//                    else if(!isFiltered)
//                    {
                    dataList.add(item);
//                    }
                }
                else {
                    Log.d("ERR","Err in source");
                }
            }

            int pageNo = fetchPageNumber();
            displayquestionsForPage(pageNo);

            //


        } catch (Exception e) {

            Log.e("ERR", ""+e.toString());
        }
    }

    //===Ad Methods

    public void loadBanner(){
        if(revmob==null)
        {
            return;
        }
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
        if(banner!=null)
         banner.release();
    }

    public void reloadBanner()
    {
        if(adTypeString.contains("appodeal")) {
            Appodeal.show(this, Appodeal.BANNER_BOTTOM);
        }
        if(adTypeString.contains("inmobi")) {
            InMobiBanner banner = (InMobiBanner) findViewById(R.id.banner);
            banner.setRefreshInterval(30);
            banner.load();
        }
        if(adTypeString.contains("revmob")) {
            releaseBanner();
            loadBanner();
        }
    }

    public void loadFullscreen() {
        //load it with RevMob listeners to control the events fired
        if(revmob==null)
        {
            return;
        }
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