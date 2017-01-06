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
import com.revmob.ads.banner.RevMobBanner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class QuizScreenActivity extends AppCompatActivity {

    ArrayList<GKItem> dataList;
    boolean isDataFiltered = false;
    boolean isCurrentAffairsType=false;
    static  int adCount = 0;
    int dateType = 0;

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
        Appodeal.show(this, Appodeal.BANNER_BOTTOM);
        loadBanner();
    }

//    @Override
//    public void onBackPressed() {
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_screen);

        Appodeal.show(this, Appodeal.BANNER_BOTTOM);

        revmob = RevMob.startWithListener(this, new RevMobAdsListener() {
            @Override
            public void onRevMobSessionStarted() {
                loadBanner(); // Cache the banner once the session is started
            }
        },"586e3005e3b2a21b72a4b5d9");

        loadBanner();

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

                if(adCount==7)
                {
                    adCount=0;
                    Appodeal.show(QuizScreenActivity.this, Appodeal.INTERSTITIAL);
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

                if(adCount==7)
                {
                    adCount=0;
                    Appodeal.show(QuizScreenActivity.this, Appodeal.INTERSTITIAL);
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
                SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                boolean isProfileUpdated = sp.getBoolean("isProfileUpdated", false);
                if(!isProfileUpdated)
                {
                    Intent i = new Intent(QuizScreenActivity.this, UserProfileActivity.class);
                    startActivity(i);

                    reloadBanner();
                    return;
                }
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
        if(isCurrentAffairsType)
        {
            
            prefsKey = "pageNumber_CA";
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

    void displayquestionsForPage(int page)
    {
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
        }

        try {
            InputStream is = this.getResources().openRawResource(fileResource);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1) ;
            final String rawGKText = new String(buffer);

            String[] separated = rawGKText.split("~");
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
                GKItem item = new GKItem(combinedString, "", Integer.toString(i),true, dateType);

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

    public void reloadBanner()
    {
        releaseBanner();
        loadBanner();
    }
}