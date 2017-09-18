package example.requestsample;

import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.media.session.IMediaSession;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RecyclerViewAdapter.ItemListener {

    private String url =  "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,LTC,DASH,ETH&tsyms=USD";
    private SwipeRefreshLayout swipeLayout;
    private String coinIDs[] = {"BTC", "LTC", "DASH", "ETH"};
    private ArrayList<DataModel> arrayList;
    private RecyclerView recyclerView;
    private AutoFitGridLayoutManager layoutManager;
    private RecyclerViewAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();

        for(String coinID: coinIDs) {
            arrayList.add(new DataModel(coinID, "0.0 USD", "#373936"));
        }


        adapter = new RecyclerViewAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        layoutManager = new AutoFitGridLayoutManager(this, 500);
        recyclerView.setLayoutManager(layoutManager);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        updateData();

    }

//
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRefresh() {
        updateData();

    }

    private void updateData() {
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
//                            arrayList = new ArrayList<>();
//
//
//                            for(String coinID: coinIDs) {
//                                arrayList.add(new DataModel(coinID, String.format("%.2f", getPrice(jsonObj, coinID)) + " USD", "#09A9FF"));
//                            }

                            for(DataModel item: arrayList) {
                                item.price = String.format("%.2f", getPrice(jsonObj, item.coinName)) + " USD";
                            }

                            adapter.updateList(arrayList);

                            swipeLayout.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });


    }


    @Override
    public void onItemClick(DataModel item) {

    }

    public double getPrice(JSONObject jsonObj, String coinName) {
        try {
            JSONObject tickerObj = new JSONObject(jsonObj.getString(coinName));
            return tickerObj.getDouble("USD");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }



}
