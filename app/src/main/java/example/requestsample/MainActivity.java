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


//    public String url = "https://api.cryptonator.com/api/ticker/BTC-USD";
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
//        arrayList.add(new DataModel("Item 1", R.drawable.battle, "#09A9FF"));
//        arrayList.add(new DataModel("Item 2", R.drawable.beer, "#3E51B1"));
//        arrayList.add(new DataModel("Item 3", R.drawable.ferrari, "#673BB7"));
//        arrayList.add(new DataModel("Item 4", R.drawable.jetpack_joyride, "#4BAA50"));

        adapter = new RecyclerViewAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        layoutManager = new AutoFitGridLayoutManager(this, 500);
        recyclerView.setLayoutManager(layoutManager);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);



        runMultiTask();
    }

//
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRefresh() {
        runMultiTask();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void runRequestTask(final String coinID) throws IOException {

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

//                            Resources res = getResources();
//                            int id = res.getIdentifier(coinID, "id", getPackageName());
//
//                            TextView priceTextView = (TextView)findViewById(id);
//
//                            priceTextView.setText(String.format("%.2f", price) + " USD");

                            arrayList.add(new DataModel(coinID, String.format("%.2f", price) + " USD", "#09A9FF"));

                            updateData(arrayList);

                            swipeLayout.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void runMultiTask() {
        arrayList = new ArrayList<>();

        for (String coinID : coinIDs) {
            try {
                runRequestTask(coinID);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

//        try {
//            runRequestTask("BTC");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        System.out.println(arrayList);
//
//        layoutManager = new AutoFitGridLayoutManager(this, 500);
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new RecyclerViewAdapter(arrayList);
//        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onItemClick(DataModel item) {

    }

    void updateData(ArrayList<DataModel> values) {
        layoutManager = new AutoFitGridLayoutManager(this, 500);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(values);
        recyclerView.setAdapter(adapter);
    }
}
