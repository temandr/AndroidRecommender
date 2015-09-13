package com.example.pranaygp.httprequest;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by pranaygp on 9/13/15.
 */
public class  AndroidRecommenderBGTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        String url ="https://ussouthcentral.services.azureml.net/workspaces/ebf63e7ff8a347e38d368435fdbe633d/services/d7aa6f2a0afe405a9c205fa3ce0cec36/execute?api-version=2.0&details=true";
//            String url ="http://secret-fjord-5332.herokuapp.com/2011/ACRDBT.json";
        Log.i(ServerCommunication.Logger, "URL : - " + url);

        String JSONStringConstructor =
                "{" +
                        "\"Inputs\":" +
                        "{\"input1\":" +
                        "{\"ColumnNames\":" +
                        "[" +
                        "\"app\"," +
                        "\"timelog\"," +
                        "\"location\"," +
                        "\"dayy\"" +
                        "]," +
                        "\"Values\":" +
                        "[" +
                        "[" +
                        "\"\"," +
                        //                                    TODO: time
                        "12," + // change
                        "\"work\"," +
                        "7" +
                        "]," +
                        "[" +
                        "\"\"," +
                        //                                    TODO: time
                        "12," + // change
                        "\"work\"," +
                        "7" +
                        "]" +
                        "]" +
                        "}" +
                        "}," +
                        "\"GlobalParameters\":" +
                        "{" +
                        "}" +
                        "}";

        JSONObject JSONdata= null;
        try {
            JSONdata = new JSONObject(JSONStringConstructor);
            String data = ServerCommunication.post_string(url, JSONdata);
            Log.i(ServerCommunication.Logger, "URL Result: - " + data);

            JSONObject resultJSON= new JSONObject(data);
            JSONArray resultValues = resultJSON.getJSONObject("Results").getJSONObject("output1").getJSONObject("value").getJSONArray("Values");
            JSONArray resultDataValues = resultValues.getJSONArray(resultValues.length()-1);
            return resultDataValues.getString(resultDataValues.length()-1);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(ServerCommunication.Logger, "onCreate " + e.getMessage());
        } catch (IOException e){
            e.printStackTrace();
            Log.e(ServerCommunication.Logger, "onCreate " + e.getMessage());
        }
        return null;
    }

    protected void onPostExecute(String feed) {
        Log.i(ServerCommunication.Logger, "onPostExecute : feed");

        //Use feed
    }
}