package example.requestsample;

import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.media.session.IMediaSession;
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

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


//    public String url = "https://api.cryptonator.com/api/ticker/BTC-USD";
    private SwipeRefreshLayout swipeLayout;
    private String coinIDs[] = {"BTC", "LTC", "DASH", "ETH"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);



        runMultiTask();
    }

//
    @Override
    public void onRefresh() {
        runMultiTask();

    }

    void runRequestTask(final String coinID) throws IOException {

        OkHttpClient client = new OkHttpClient();
        String url =  "https://api.cryptonator.com/api/ticker/" + coinID + "-USD";

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
                            JSONObject tickerObj = new JSONObject(jsonObj.getString("ticker"));
                            double price = tickerObj.getDouble("price");

                            Resources res = getResources();
                            int id = res.getIdentifier(coinID, "id", getPackageName());

                            TextView priceTextView = (TextView)findViewById(id);

                            priceTextView.setText(String.format("%.2f", price) + " USD");

                            swipeLayout.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });
    }

    void runMultiTask() {
        for (String coinID : coinIDs) {
            try {
                runRequestTask(coinID);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



}
