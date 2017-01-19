package com.rupik.a5000gkquestionsanswers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by boom on 1/12/16.
 */

public class AlarmReceiverGK extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Intent notificationIntent = new Intent(context, QuizScreenActivity.class);
        notificationIntent.putExtra("isCurrentAffairsType",false);

        Intent notificationIntent_MainMenu = new Intent(context, MainMenuActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(QuizScreenActivity.class);
        stackBuilder.addNextIntent(notificationIntent_MainMenu);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        String question = populateDatasource();
        Notification notification = builder.setContentTitle("Question of the Hour")
                .setContentText(question)
                .setTicker("GK")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).setPriority(2).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

        setNextNotification();
    }

    void setNextNotification()
    {
        //---
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(context, 19890, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 15);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        }
        //---
    }


    int fetchPageNumber ()
    {
        SharedPreferences sp = context.getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        int pNo = sp.getInt("pageNumber", 1);
        return  pNo;
    }

    String populateDatasource()
    {
        String question = "Which forms of coal is the oldest ?";
        ArrayList<GKItem> dataList;

        try {
            dataList = new ArrayList<>();

            InputStream is = context.getResources().openRawResource(R.raw.final_edited_gk);
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

                    dataList.add(item);
                }
                else {
                    Log.d("ERR","Err in source");
                }
            }

            int pageNo = fetchPageNumber();
            int questionStartId = (pageNo-1) * 4;
            if(questionStartId<dataList.size())
            {
                GKItem item = dataList.get(questionStartId);
                question = item.getQuestion();
            }

        } catch (Exception e) {

            Log.e("ERR", ""+e.toString());
        }

        return question;
    }
}
