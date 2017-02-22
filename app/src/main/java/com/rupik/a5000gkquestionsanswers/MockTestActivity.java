package com.rupik.a5000gkquestionsanswers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MockTestActivity extends AppCompatActivity {

    ArrayList<MockTestListItem> mockTestLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_test);

        Appodeal.show(this, Appodeal.BANNER_BOTTOM);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected) {

            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBarID);
            progressBar.setVisibility(View.VISIBLE);
            TextView progressText = (TextView)findViewById(R.id.progressText);
            progressText.setVisibility(View.VISIBLE);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    prepareMockTestLists();
                }
            });
        }
        else {
            Toast.makeText(this,"Unable to Fetch Data From Server !!! Please Check Your Internet Connection !",Toast.LENGTH_LONG).show();
        }

        ListView mockTestListView = (ListView) findViewById(R.id.mockTestList);
        mockTestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<MCQItem> mcqDataList = mockTestLists.get(i).getMcqItemArrayList();
                Intent intent = new Intent(MockTestActivity.this, MCQActivity.class);
                intent.putExtra("isQuestionsType",true);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mcqDataList", mcqDataList);
                intent.putExtras(bundle);
                MockTestActivity.this.startActivity(intent);
            }
        });

    }

    void prepareMockTestLists()
    {
        mockTestLists = new ArrayList<>();

        try{
            final SharedPreferences prefs = this.getSharedPreferences("your_prefs", MODE_PRIVATE);

            final int mockTestFileVersion = prefs.getInt("MOCK_TEST_FILE_VERSION",0);

//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });

            //

            try {

                String urlStr = "https://quarkbackend.com/getfile/sohambhowmik/gk-mock-test-file-version";

                // Create a URL for the desired page
                URL url = new URL(urlStr);

                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                String mockTestJSON = "";
                while ((str = in.readLine()) != null) {
                    mockTestJSON = mockTestJSON+str;
                }
                in.close();

                //parse here...simple version check parser

                JSONObject jsonObject = new JSONObject(mockTestJSON);
                int serverVersion = jsonObject.optInt("version");

                if(mockTestFileVersion<serverVersion)
                {
                    //We need to fetch latest mock Test Here and save it to files. Also save the latest version to sharedprefs

                    urlStr = "https://quarkbackend.com/getfile/sohambhowmik/gkmocktest";

                    // Create a URL for the desired page
                    url = new URL(urlStr);

                    // Read all the text returned by the server
                    in = new BufferedReader(new InputStreamReader(url.openStream()));
                    str = ""; mockTestJSON = "";

                    while ((str = in.readLine()) != null) {
                        mockTestJSON = mockTestJSON+str;
                    }
                    in.close();

//                    parseMockTestJson(mockTestJSON);

                    int totalFiles = prefs.getInt("TOTAL_MOCK_TEST_FILES",0);
                    totalFiles+=1;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("TOTAL_MOCK_TEST_FILES",totalFiles);
                    saveData(mockTestJSON, "MockTest"+Integer.toString(totalFiles));

                    editor.putInt("MOCK_TEST_FILE_VERSION",serverVersion);
                    editor.commit();

                    //

                    //reload the adapter in Finally IF any data has been added.
                    MockTestActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            displayData();
                        }
                    });
                }

            } catch (MalformedURLException e) {
                Log.d("MalformedURLException", e.getLocalizedMessage());
            } catch (IOException e) {
                Log.d("IOERR", e.getLocalizedMessage());
            }
            catch (JSONException e)
            {
                Log.d("IOERR", e.getLocalizedMessage());
            }
            finally {


            }



            String rawMCQTextA = "";

            int totalFiles = prefs.getInt("TOTAL_MOCK_TEST_FILES",0);

            for(int i=totalFiles;i>0;i--)
            {
                String fileName = "MockTest"+Integer.toString(i);
                File f = new File(this.getFilesDir().getPath() + "/" + fileName);
                //check whether file exists
                FileInputStream is = new FileInputStream(f);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                rawMCQTextA = new String(buffer);
                parseMockTestJson(rawMCQTextA);
            }

            //

            InputStream isA = this.getResources().openRawResource(R.raw.mocktest1);
            byte[] bufferA = new byte[isA.available()];
            while (isA.read(bufferA) != -1) ;
            rawMCQTextA = new String(bufferA);
            isA.close();
            parseMockTestJson(rawMCQTextA);

            isA = this.getResources().openRawResource(R.raw.mocktest2);
            bufferA = new byte[isA.available()];
            while (isA.read(bufferA) != -1) ;
            rawMCQTextA = new String(bufferA);
            isA.close();
            parseMockTestJson(rawMCQTextA);

            isA = this.getResources().openRawResource(R.raw.mocktest3);
            bufferA = new byte[isA.available()];
            while (isA.read(bufferA) != -1) ;
            rawMCQTextA = new String(bufferA);
            isA.close();
            parseMockTestJson(rawMCQTextA);

            isA = this.getResources().openRawResource(R.raw.mocktest4);
            bufferA = new byte[isA.available()];
            while (isA.read(bufferA) != -1) ;
            rawMCQTextA = new String(bufferA);
            isA.close();
            parseMockTestJson(rawMCQTextA);

            isA = this.getResources().openRawResource(R.raw.mocktest5);
            bufferA = new byte[isA.available()];
            while (isA.read(bufferA) != -1) ;
            rawMCQTextA = new String(bufferA);
            isA.close();
            parseMockTestJson(rawMCQTextA);

            MockTestActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    displayData();
                }
            });

        }
        catch (Exception e)
        {
            Log.d("",e.getLocalizedMessage());
        }
    }

    void parseMockTestJson(String mockTestJSON)
    {
        ArrayList<MCQItem> mcqDataList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(mockTestJSON);
//            noOfQuestionsinMockTest = jsonObject.optString("totalQuestions");
            String timeRemaining = jsonObject.optString("time");
            JSONArray jsonArray = jsonObject.optJSONArray("questions");
            String title = jsonObject.optString("title");
            for(int i=0; i<jsonArray.length(); i++)
            {
                MCQItem item = new MCQItem();

                JSONObject jObj = jsonArray.getJSONObject(i);
                item.setMcqQuestion(jObj.optString("Question"));
                item.setAnswer(jObj.optString("Answer"));
                item.setDetailedExplanation(jObj.optString("AnswerExplained"));

                mcqDataList.add(item);


            }

            MockTestListItem MTLItem = new MockTestListItem();
            MTLItem.setTitle(title);
            MTLItem.setMcqItemArrayList(mcqDataList);

            mockTestLists.add(MTLItem);
        }
        catch (JSONException e)
        {
            Log.d("JSONException", e.getLocalizedMessage());
        }
    }

    void saveData(String mJsonResponse, String fileName) {
        try {
            FileWriter file = new FileWriter(this.getFilesDir().getPath() + "/" + fileName);
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
    }

    void displayData()
    {
        if(mockTestLists!=null) {

            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBarID);
            progressBar.setVisibility(View.GONE);
            TextView progressText = (TextView)findViewById(R.id.progressText);
            progressText.setVisibility(View.GONE);


            ListView mockTestListView = (ListView) findViewById(R.id.mockTestList);
            MockTestListAdapter adapter = new MockTestListAdapter(this, mockTestLists);
            mockTestListView.setAdapter(adapter);
        }
    }
}
