package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.sam_chordas.android.stockhawk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class DetailActivity extends Activity {

    private LineChart mLineChart;
    private String mUrl;
    final String URL_SCHEME = "http";
    final String BASE_URL = "chartapi.finance.yahoo.com";
    final String INSTRUMENT_PATH = "instrument";
    final String VERSION_PATH = "1.0";
    final String CHART_DATA_PATH = "chartdata";
    final String URL_PARAMS = ";type=close;range=1d/json";
    private String mSymbol;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mLineChart = (LineChart)findViewById(R.id.line_chart);
        mRequestQueue = Volley.newRequestQueue(this);

        Intent i = getIntent();
        mSymbol = i.getStringExtra("symbol");

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URL_SCHEME)
                .authority(BASE_URL)
                .appendPath(INSTRUMENT_PATH)
                .appendPath(VERSION_PATH)
                .appendPath("goog")
                .appendPath(CHART_DATA_PATH);

        mUrl = builder.build().toString();
        mUrl = mUrl + URL_PARAMS;

        getJsonFromApi(mUrl);

    }

    public void getJsonFromApi(String url){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, mUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray array = response.getJSONArray("series");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject json = array.getJSONObject(i);

                                String timestamp = json.getString("Timestamp");
                                String closeAmount = json.getString("close");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                    }
                });
        mRequestQueue.add(request);
    }
}


