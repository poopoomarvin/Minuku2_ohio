package edu.ohio.minuku.Utilities;

import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ohio.minuku.Data.DataHandler;
import edu.ohio.minuku.config.Constants;
import edu.ohio.minuku.logger.Log;

/**
 * Created by Lawrence on 2018/3/19.
 */

public class CSVHelper {

    public static final String TAG = "CSVHelper";

    public static CSVWriter csv_writer = null;

    public static void storeToCSV_IntervalSurveyUpdated(boolean clicked){

        String sFileName = "IntervalSurveyState.csv";

//        Log.d(TAG, "sFileName : " + sFileName);

        try {

            File root = new File(Environment.getExternalStorageDirectory() + Constants.PACKAGE_DIRECTORY_PATH);

            if (!root.exists()) {
                root.mkdirs();
            }

            long clickedTime = ScheduleAndSampleManager.getCurrentTimeInMillis();
            String clickedTimeString = ScheduleAndSampleManager.getTimeString(clickedTime);

            String clickedString;
            if(clicked)
                clickedString = "Yes";
            else
                clickedString = "No";

            //get overall data
            ArrayList<String> resultInArray = DataHandler.getSurveys();

            int openCount = 0;

            float rate;

            for(int index = 0; index < resultInArray.size();index++){

                String openOrNot = resultInArray.get(index).split(Constants.DELIMITER)[5];

                Log.d(TAG, "[test show link] open flag : "+ openOrNot);

                if(openOrNot.equals("1"))
                    openCount++;
            }

            Log.d(TAG, "[test show link] openCount : "+ openCount + " resultInArray size : "+resultInArray.size());

            rate = (float)openCount/resultInArray.size() * 100;

            String rateString = String.format("%.2f", rate);


            csv_writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory()+Constants.PACKAGE_DIRECTORY_PATH+sFileName,true));

            List<String[]> data = new ArrayList<String[]>();

            if(clicked)
                data.add(new String[]{"", "", clickedString, clickedTimeString, "", "", "", rateString+"%"});
            else
                data.add(new String[]{"", "", clickedString, "", "", "", "", rateString+"%"});

            csv_writer.writeAll(data);

            csv_writer.close();

        } catch(IOException e) {
//            e.printStackTrace();
//            android.util.Log.e(TAG, "exception", e);
        } catch (IndexOutOfBoundsException e2){
//            e2.printStackTrace();
//            android.util.Log.e(TAG, "exception", e2);

        }

    }

    public static void TransportationState_StoreToCSV(long timestamp, String state, String activitySofar){

        String sFileName = "TransportationState.csv"; //Static.csv

        try{
            File root = new File(Environment.getExternalStorageDirectory() + Constants.PACKAGE_DIRECTORY_PATH);
            if (!root.exists()) {
                root.mkdirs();
            }

            //Log.d(TAG, "root : " + root);

            csv_writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory()+Constants.PACKAGE_DIRECTORY_PATH+sFileName,true));

            List<String[]> data = new ArrayList<String[]>();

            String timeString = ScheduleAndSampleManager.getTimeString(timestamp);

            data.add(new String[]{String.valueOf(timestamp), timeString, state, String.valueOf(activitySofar)});

            csv_writer.writeAll(data);

            csv_writer.close();

        }catch (IOException e){
            //e.printStackTrace();
        }/*catch (Exception e){
            //e.printStackTrace();
        }*/
    }

    public static void dataUploadingCSV(String dataType, String json){

        String sFileName = "DataUploaded.csv"; //Static.csv

        try{

            File root = new File(Environment.getExternalStorageDirectory() + Constants.PACKAGE_DIRECTORY_PATH);

            if (!root.exists()) {
                root.mkdirs();
            }

            //Log.d(TAG, "root : " + root);

            csv_writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory()+Constants.PACKAGE_DIRECTORY_PATH+sFileName,true));

            List<String[]> data = new ArrayList<String[]>();

            data.add(new String[]{dataType, json});

            csv_writer.writeAll(data);

            csv_writer.close();

        }catch (IOException e){
            //e.printStackTrace();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

}