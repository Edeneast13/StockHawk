package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DetailActivity extends AppCompatActivity {

    private String mUrl;
    final String URL_SCHEME = "http";
    final String BASE_URL = "chartapi.finance.yahoo.com";
    final String INSTRUMENT_PATH = "instrument";
    final String VERSION_PATH = "1.0";
    final String CHART_DATA_PATH = "chartdata";
    final String URL_PARAMS = ";type=close;range=1d/json";
    private String mSymbol;
    private ArrayList<Entry> mEntries = new ArrayList<Entry>();
    private ArrayList<String> mLabels = new ArrayList<String>();
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chart = new LineChart(getApplicationContext());

        setContentView(chart);

        Intent i = getIntent();
        mSymbol = i.getStringExtra("symbol");

        getSupportActionBar().setTitle(mSymbol);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URL_SCHEME)
                .authority(BASE_URL)
                .appendPath(INSTRUMENT_PATH)
                .appendPath(VERSION_PATH)
                .appendPath(mSymbol)
                .appendPath(CHART_DATA_PATH);

        mUrl = builder.build().toString();
        mUrl = mUrl + URL_PARAMS;

        getJsonFromApi(mUrl);
    }

    public void getJsonFromApi(String url){

        try {

            FetchDetailsTask task = new FetchDetailsTask();

            String jsonpData = task.execute(url).get();
            String jsonData = jsonpData.substring(jsonpData.indexOf("(") + 1, jsonpData.lastIndexOf(")"));

            String json = convertJSONString(jsonData);

            Log.i("PARSE DATA ", json);

            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray("series");

            for (int i = 0; i < array.length(); i++) {

                JSONObject series = array.getJSONObject(i);
                String timestamp = series.getString("Timestamp");
                String closeAmount = series.getString("close");

                Entry data = new Entry(Float.parseFloat(closeAmount), i);
                mEntries.add(data);

                mLabels.add(timestamp);
            }

            LineDataSet dataSet = new LineDataSet(mEntries, "5");
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            LineData data = new LineData(mLabels, dataSet);

            chart.setData(data);
            chart.setDescription(getString(R.string.graph_description));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String convertJSONString(String jsonData) {
        jsonData = jsonData.replaceAll("/n", "");

        return jsonData;
    }

    public class FetchDetailsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            HttpURLConnection urlConnection = null;
            String result = null;
            BufferedReader bufferedReader = null;

            try {

                URL url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(inputStream);

                bufferedReader = new BufferedReader(reader);

                StringBuffer stringBuffer = new StringBuffer();

                String line;
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line + "/n");
                }

                while (stringBuffer.length() == 0) {

                    //empty string no reason to parse
                    Log.i("StringBuffer", "Empty");
                    return null;
                }

                result = stringBuffer.toString();
            }
            catch(MalformedURLException e){
                e.printStackTrace();
            }
            catch (IOException e ){
                e.printStackTrace();
            }
            finally {

                if (urlConnection != null) {

                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {

                    try {
                        bufferedReader.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try{
                return result;
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(String url){

        }
    }
}


