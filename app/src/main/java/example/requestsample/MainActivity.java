package example.requestsample;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    TextView txtString;
    TextView rateString;
    public String url = "https://api.coindesk.com/v1/bpi/currentprice.json";
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        txtString = (TextView)findViewById(R.id.textView);
        rateString = (TextView)findViewById(R.id.rate);
        try {
            runRequestTask();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//
    @Override
    public void onRefresh() {
        try {
            runRequestTask();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void runRequestTask() throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObj = new JSONObject(myResponse);
                            String chartName = jsonObj.getString("chartName");
                            JSONObject bpi = new JSONObject(jsonObj.getString("bpi"));
                            JSONObject USD = new JSONObject(bpi.getString("USD"));
                            String rate = USD.getString("rate");
                            System.out.println(rate);

                            txtString.setText(chartName);
                            rateString.setText(rate + "USD");

                            swipeLayout.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });
    }

    void load(View view) {
        try {
            runRequestTask();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
