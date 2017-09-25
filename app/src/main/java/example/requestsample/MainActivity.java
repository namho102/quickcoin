package example.requestsample;

import android.content.Intent;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity implements RecyclerRefreshLayout.OnRefreshListener, RecyclerViewAdapter.ItemListener {

    private String url =  "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,LTC,DASH,ETH&tsyms=USD";
    private RecyclerRefreshLayout swipeLayout;
//    private String coinIDs[] = {"BTC", "LTC", "DASH", "ETH"};
    private ArrayList<DataModel> arrayList;
    private RecyclerView recyclerView;
    private AutoFitGridLayoutManager layoutManager;
    private RecyclerViewAdapter adapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.content_hamburger)
    View contentHamburger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //fake data
        arrayList = new ArrayList<DataModel>() {{
            add(new DataModel("BTC", "Bitcoin", 3573.27));
            add(new DataModel("LTC", "Litecoin", 45.35));
            add(new DataModel("DASH", "Dash", 332.33));
            add(new DataModel("ETH", "Ethereum", 256.65));
        }};

        adapter = new RecyclerViewAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        layoutManager = new AutoFitGridLayoutManager(this, 500);
        recyclerView.setLayoutManager(layoutManager);

        swipeLayout = (RecyclerRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setRefreshStyle(RecyclerRefreshLayout.RefreshStyle.FLOAT);
//        swipeLayout.setRefreshInitialOffset(30);
        swipeLayout.setOnRefreshListener(this);

//        updateData();

    }

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
                                item.price = getPrice(jsonObj, item.coinId);
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
        System.out.println(item);
        Intent myIntent = new Intent(MainActivity.this, ChartActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString("coinName", item.coinName);
        myIntent.putExtra("packBundle", bundle);

        startActivity(myIntent);


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
