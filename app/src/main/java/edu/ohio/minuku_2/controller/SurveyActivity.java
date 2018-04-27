package edu.ohio.minuku_2.controller;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.ohio.minuku.Data.DBHelper;
import edu.ohio.minuku.Data.DataHandler;
import edu.ohio.minuku.Utilities.ScheduleAndSampleManager;
import edu.ohio.minuku.config.Constants;
import edu.ohio.minuku.manager.DBManager;
import edu.ohio.minuku_2.R;

/**
 * Created by Lawrence on 2017/9/16.
 */

public class SurveyActivity extends Activity {

    private final static String TAG = "SurveyActivity";

    private Button testButton,
        survey1_Button, survey2_Button, survey3_Button, survey4_Button, survey5_Button, survey6_Button;
    private TextView totalView, missedView, openedView, lastOpenedView, mobileMissedView, randomMissedView;
    private ListView listview;
    private ArrayList<String> data;

    private ArrayList<String> surveyDatas;

    private int notifyID = 1;

    private int test_notitypeNum = 0;

    private SharedPreferences sharedPrefs;

    private NotificationManager mNotificationManager;

    private final String TEXT_Unavailable = "Unavailable";
    private final String TEXT_Available = " Available ";/*the space is to padding the border*/
    private final String TEXT_COMPLETED = "Completed";
    private final String TEXT_MISSED = "Missed";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "daysInSurvey : "+ Constants.daysInSurvey);

        if(Constants.daysInSurvey == 0 || Constants.daysInSurvey == -1) {

            setContentView(R.layout.surveypage_day0);
        }else if(Constants.daysInSurvey > 14){

            setContentView(R.layout.surveypage_complete);
        }else {
            setContentView(R.layout.surveypage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Constants.daysInSurvey == 0 || Constants.daysInSurvey == -1) {

            setContentView(R.layout.surveypage_day0);
        }else if(Constants.daysInSurvey > 14){

            setContentView(R.layout.surveypage_complete);
        }else {
            setContentView(R.layout.surveypage);
        }

        if(Constants.daysInSurvey <= 14 && Constants.daysInSurvey >= 1)
            initlinkListohio();
    }

    private void initlinkListohio(){

        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        /* get today's data from the DB*/

        long currentTimeInMillis = ScheduleAndSampleManager.getCurrentTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_NOW_DAY);
        String todayDate = ScheduleAndSampleManager.getTimeString(currentTimeInMillis, sdf);

        SimpleDateFormat sdf2 = new SimpleDateFormat(Constants.DATE_FORMAT_NOW_NO_ZONE);
        String startTimeString = todayDate + " 00:00:00";
        long startTime = ScheduleAndSampleManager.getTimeInMillis(startTimeString, sdf2);
        long endTime = startTime + Constants.MILLISECONDS_PER_DAY;

        //format : id;link_col;generateTime_col;openTime_col;missedTime_col;openFlag_col;surveyType_col
        surveyDatas = DBHelper.querySurveyLinkBetweenTimes(startTime, endTime);

        survey1_Button = (Button) findViewById(R.id.survey1_button);
        survey2_Button = (Button) findViewById(R.id.survey2_button);
        survey3_Button = (Button) findViewById(R.id.survey3_button);
        survey4_Button = (Button) findViewById(R.id.survey4_button);
        survey5_Button = (Button) findViewById(R.id.survey5_button);
        survey6_Button = (Button) findViewById(R.id.survey6_button);

        setSurveyButtonsWork();

        setSurveyButtonsAvailable(survey1_Button, 1);
        setSurveyButtonsAvailable(survey2_Button, 2);
        setSurveyButtonsAvailable(survey3_Button, 3);
        setSurveyButtonsAvailable(survey4_Button, 4);
        setSurveyButtonsAvailable(survey5_Button, 5);
        setSurveyButtonsAvailable(survey6_Button, 6);


        //for testing, could deprecate it after we complete all the work
        testButton = (Button) findViewById(R.id.triggerButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    mNotificationManager.cancel(notifyID);
                }catch (Exception e){

                }

                triggerSurveyByButton();
                addSurveyLinkToDB();

            }
        });


    }

    private void setSurveyButtonsAvailable(Button survey_Button, int correspondingSize){

        if(surveyDatas.size() < correspondingSize){

            survey_Button.setClickable(false);
            survey_Button.setText(TEXT_Unavailable);
        }else{

            String surveyData = surveyDatas.get(correspondingSize-1);

            String openFlag = surveyData.split(Constants.DELIMITER)[5];

            if(openFlag.equals("1")){
                survey_Button.setText(TEXT_COMPLETED);
                survey_Button.setClickable(false);
            }else if(openFlag.equals("0")){
                survey_Button.setText(TEXT_MISSED);
                survey_Button.setClickable(false);
            }else{
                survey_Button.setBackgroundColor(Color.RED);
                survey_Button.setTextColor(getResources().getColor(R.color.white));
                survey_Button.setText(TEXT_Available);
            }

        }
    }

    private void setSurveyButtonsWork(){

        survey1_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                surveyButtonsWork(1);
            }
        });

        survey2_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                surveyButtonsWork(2);
            }
        });

        survey3_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                surveyButtonsWork(3);
            }
        });

        survey4_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                surveyButtonsWork(4);
            }
        });

        survey5_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                surveyButtonsWork(5);
            }
        });

        survey6_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                surveyButtonsWork(6);
            }
        });
    }

    private void surveyButtonsWork(int buttonNumber){

        //record if the user have clicked the survey button
        sharedPrefs.edit().putBoolean("Period"+buttonNumber,false).apply();

        try{

            String surveyData = surveyDatas.get(buttonNumber-1);

            //set opened time.
            //if they click it, set the openFlag to 1.
            String id = surveyData.split(Constants.DELIMITER)[0];
            DataHandler.updateSurveyOpenFlagAndTime(id);

            //get the link in the surveyLink table.
            String link = surveyData.split(Constants.DELIMITER)[1];

            //Log.d(TAG, "the "+ buttonNumber +" link is : "+link);

            Intent resultIntent = new Intent(Intent.ACTION_VIEW);
            resultIntent.setData(Uri.parse(link)); //get the link from adapter

            startActivity(resultIntent);
        }catch (IndexOutOfBoundsException e){

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something on back.
            //Log.d(TAG, " onKeyDown");

            Intent intent = getIntent();
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public ArrayList<String> getData(){

        //Log.d(TAG, " getData");

        ArrayList<String> data = new ArrayList<String>();

        long startTime = -9999;
        long endTime = -9999;
        String startTimeString = "";
        String endTimeString = "";

        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        int Year = cal.get(Calendar.YEAR);
        int Month = cal.get(Calendar.MONTH)+1;
        int Day = cal.get(Calendar.DAY_OF_MONTH);

        startTimeString = makingDataFormat(Year, Month, Day);
        endTimeString = makingDataFormat(Year, Month, Day+1);
        startTime = getSpecialTimeInMillis(startTimeString);
        endTime = getSpecialTimeInMillis(endTimeString);

        String taskTable = DBHelper.surveyLink_table;

        try {
            SQLiteDatabase db = DBManager.getInstance().openDatabase();

            Cursor tripCursor = db.rawQuery("SELECT "+ DBHelper.openFlag_col +", "+ DBHelper.link_col +" FROM " + taskTable + " WHERE " //+ DBHelper.Trip_id + " ='" + position + "'" +" AND "
                    +DBHelper.TIME+" BETWEEN"+" '"+startTime+"' "+"AND"+" '"+endTime+"' ORDER BY "+DBHelper.TIME+" DESC", null);

            //Log.d(TAG, "SELECT "+ DBHelper.openFlag_col +", "+ DBHelper.link_col +" FROM " + taskTable + " WHERE " //+ DBHelper.Trip_id + " ='" + position + "'" +" AND "
//                    +DBHelper.TIME+" BETWEEN"+" '"+startTime+"' "+"AND"+" '"+endTime+"' ORDER BY "+DBHelper.TIME+" DESC");

            //get all data from cursor
            int i = 0;
            if(tripCursor.moveToFirst()){
                do{
                    int eachdataInCursor = tripCursor.getInt(0);
                    String link = tripCursor.getString(1);

                    //Log.d(TAG, " 0 : "+ eachdataInCursor+ ", 1 : "+ link);

                    data.add(link);
                    //Log.d(TAG, " link : "+ link);

                    //Log.d(TAG, " tripCursor.moveToFirst()");
                }while(tripCursor.moveToNext());
            }else
                //Log.d(TAG, " tripCursor.moveToFirst() else");
            tripCursor.close();
        }catch (Exception e){
            //e.printStackTrace();
        }
        return data;
    }

    private long getSpecialTimeInMillis(String givenDateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_NOW_NO_ZONE_Slash);
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(givenDateFormat);
            timeInMilliseconds = mDate.getTime();
            //Log.d(TAG,"Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public String makingDataFormat(int year,int month,int date){
        String dataformat= "";

        dataformat = addZero(year)+"/"+addZero(month)+"/"+addZero(date)+" "+"00:00:00";

        return dataformat;
    }

    private String addZero(int date){
        if(date<10)
            return String.valueOf("0"+date);
        else
            return String.valueOf(date);
    }

    //for Testing
    private void triggerSurveyByButton(){

        //TODO if the last link haven't been opened, setting it into missed.
        String latestLinkData = DataHandler.getLatestSurveyData();
        //if there have data in DB
        if(!latestLinkData.equals("")) {

            String clickOrNot = latestLinkData.split(Constants.DELIMITER)[5];

            String id = latestLinkData.split(Constants.DELIMITER)[0];

            DataHandler.updateSurveyMissTime(id, DBHelper.missedTime_col);

        }

        String notiText = "You have a new random survey(Artifical)";

        //Log.d(TAG,"intervalQualtrics");

        Intent resultIntent = new Intent(SurveyActivity.this, SurveyActivity.class);
        PendingIntent pending = PendingIntent.getActivity(SurveyActivity.this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);//Context.
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle("DMS");
        bigTextStyle.bigText(notiText);

        Notification note = new Notification.Builder(this)
                .setContentTitle(Constants.APP_NAME)
                .setContentText(notiText)
                .setContentIntent(pending)
                .setStyle(bigTextStyle)
                .setSmallIcon(R.drawable.self_reflection)
                .setAutoCancel(true)
                .build();

        // using the same tag and Id causes the new notification to replace an existing one
        mNotificationManager.notify(notifyID, note); //String.valueOf(System.currentTimeMillis()),
        note.flags = Notification.FLAG_AUTO_CANCEL;

    }

    public void addSurveyLinkToDB(){
        //Log.d(TAG, "addSurveyLinkToDB");

        settingMissedClickedCount();

        String noti_type;

        test_notitypeNum++;
        if(test_notitypeNum %2 ==0)
            noti_type = "walk";
        else
            noti_type = "random";

        String link = "https://osu.az1.qualtrics.com/jfe/form/SV_6xjrFJF4YwQwuMZ";

        SharedPreferences sharedPrefs = getSharedPreferences("edu.umich.minuku_2", MODE_PRIVATE);
        String participantID = sharedPrefs.getString("userid", "NA");
        String groupNum = sharedPrefs.getString("groupNum", "NA");

        String linktoShow = link + "?p="+participantID + "&g=" + groupNum + "&w=" + "1" + "&d=" + "1" + "&r=" + "1" + "&m=" + "0";

        ContentValues values = new ContentValues();

        try {
            SQLiteDatabase db = DBManager.getInstance().openDatabase();

            values.put(DBHelper.generateTime_col, new Date().getTime());
            values.put(DBHelper.link_col, linktoShow);
            values.put(DBHelper.surveyType_col, noti_type);
            values.put(DBHelper.openFlag_col, -1); //they can't enter the link by the notification.

            db.insert(DBHelper.surveyLink_table, null, values);

        }
        catch(NullPointerException e){
            //e.printStackTrace();
        }
        finally {
            values.clear();
            DBManager.getInstance().closeDatabase(); // Closing database connection
        }
    }

    private void settingMissedClickedCount(){
        //TODO to check the missing count and clicked
        //setting the
        long startTime = -9999;
        long endTime = -9999;
        String startTimeString = "";
        String endTimeString = "";

        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        int Year = cal.get(Calendar.YEAR);
        int Month = cal.get(Calendar.MONTH)+1;
        int Day = cal.get(Calendar.DAY_OF_MONTH);

        startTimeString = makingDataFormat(Year, Month, Day);
        endTimeString = makingDataFormat(Year, Month, Day+1);
        startTime = getSpecialTimeInMillis(startTimeString);
        endTime = getSpecialTimeInMillis(endTimeString);

        ArrayList<String> data = new ArrayList<String>();
        data = DataHandler.getSurveyData(startTime, endTime);

        //Log.d(TAG, "SurveyData : "+ data.toString());

        int mobileMissedCount = 0;
        int randomMissedCount = 0;
        int missCount = 0;
        int openCount = 0;

        for(String datapart : data){
            //Log.d(TAG, "datapart : " + datapart);
            //Log.d(TAG, "datapart [5] : " + datapart.split(Constants.DELIMITER)[5]);
            //Log.d(TAG, "datapart [5] == 1 : " + datapart.split(Constants.DELIMITER)[5].equals("1"));

            //if the link havn't been opened.
            if(datapart.split(Constants.DELIMITER)[5].equals("0")){
                missCount++;
                if(datapart.split(Constants.DELIMITER)[6].equals("walk"))
                    mobileMissedCount++;
                else if(datapart.split(Constants.DELIMITER)[6].equals("random"))
                    randomMissedCount++;
            }
            else if(datapart.split(Constants.DELIMITER)[5].equals("1"))
                openCount++;
        }

        String previousMobileMissedCount = sharedPrefs.getString("mobileMissedCount", "");
        String previousRandomMissedCount = sharedPrefs.getString("randomMissedCount", "");
        String previousOpenCount = sharedPrefs.getString("OpenCount", "");

        previousMobileMissedCount += " "+mobileMissedCount;
        previousRandomMissedCount += " "+randomMissedCount;
        previousOpenCount += " "+openCount;

        sharedPrefs.edit().putString("mobileMissedCount", previousMobileMissedCount).apply();
        sharedPrefs.edit().putString("randomMissedCount", previousRandomMissedCount).apply();
        sharedPrefs.edit().putString("OpenCount", previousOpenCount).apply();

    }

}