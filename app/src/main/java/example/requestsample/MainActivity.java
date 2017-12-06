package example.requestsample;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


public class MainActivity extends Fragment implements RecyclerRefreshLayout.OnRefreshListener, RecyclerViewAdapter.ItemListener {

    private String url = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,LTC,DASH,ETH&tsyms=USD";
    private RecyclerRefreshLayout swipeLayout;
    //private String coinIDs[] = {"BTC", "LTC", "DASH", "ETH"};
    private ArrayList<DataModel> arrayList;
    private RecyclerView recyclerView;
    private AutoFitGridLayoutManager layoutManager;
    private RecyclerViewAdapter adapter;
    private SharedPreferences sharedPreListCoin;

    TextView btnConvert;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.content_hamburger)
    View contentHamburger;

    @Override
//    protected void onCreate(Bundle savedInstanceState) {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);
//
//        if (toolbar != null) {
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setTitle(null);
//        }
//
//        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
//        root.addView(guillotineMenu);
//
//        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
//                .setActionBarViewForAnimation(toolbar)
//                .setClosedOnStart(true)
//                .build();
        View view  = inflater.inflate(R.layout.activity_main,null);
        initApp(view);

        updateData();

        adapter = new RecyclerViewAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        layoutManager = new AutoFitGridLayoutManager(this.getContext(), 500);
        recyclerView.setLayoutManager(layoutManager);

        swipeLayout.setRefreshStyle(RecyclerRefreshLayout.RefreshStyle.FLOAT);
        //swipeLayout.setRefreshInitialOffset(30);
        swipeLayout.setOnRefreshListener(this);

//        addEventMainMenu();
        return view;
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
                updateListCoinFromSharedPre();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();
                //save data every time activity reload
                saveListCoinToSharedPre(myResponse);
                //addition to avoid Runnable error: on a null object reference
                if(getActivity()==null)return;
                MainActivity.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateListCoinFormStringJson(myResponse);
                        adapter.updateList(arrayList);
                        swipeLayout.setRefreshing(false);
                    }
                });
            }
        });
    }
    @Override
    public void onItemClick(DataModel item) {
//        System.out.println(item);
//        Intent myIntent = new Intent(MainActivity.this, ChartFragment.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("coinName", item.coinName);
//        myIntent.putExtra("packBundle", bundle);
//        startActivity(myIntent);
        Fragment chartFragment = new ChartActivity();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("coinName",item.coinName);
        chartFragment.setArguments(bundle);
        transaction.replace(R.id.frContainer, chartFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
    public void initApp(View view) {
        //mapping view
        swipeLayout = (RecyclerRefreshLayout) view.findViewById(R.id.swipe_container);
        btnConvert = (TextView) view.findViewById(R.id.btn_MainMenu_Convert);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //init sharedPre
        sharedPreListCoin = this.getActivity().getSharedPreferences("dataListCoin",MODE_PRIVATE);
        //init list coin data
        arrayList = new ArrayList<DataModel>() {{
            add(new DataModel("BTC", "Bitcoin", 0));
            add(new DataModel("LTC", "Litecoin", 0));
            add(new DataModel("DASH", "Dash", 0));
            add(new DataModel("ETH", "Ethereum", 0));
        }};
        updateListCoinFromSharedPre();
    }
    public void saveListCoinToSharedPre(String s){
        SharedPreferences.Editor editor = sharedPreListCoin.edit();
        editor.putString("fullListCoinData", s);
        editor.commit();
    }
    public void updateListCoinFromSharedPre(){
        String listCoin = sharedPreListCoin.getString("fullListCoinData", "");
        if(!listCoin.isEmpty()){
        updateListCoinFormStringJson(listCoin);
        }
    }
    public void updateListCoinFormStringJson(String s){
        try {
            JSONObject jsonObj = new JSONObject(s);
            for(DataModel item: arrayList) {
                item.price = getPrice(jsonObj, item.coinId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//    public void addEventMainMenu()
//    {
//        btnConvert.setOnClickListener(new MyButtonEvent());
//    }
//
//    private class MyButtonEvent implements View.OnClickListener {
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//            case R.id.btn_MainMenu_Convert:
//                Toast.makeText(getApplicationContext(), "Chuyen Sang Convert Activity", Toast.LENGTH_LONG);
//                String listCoin = sharedPreListCoin.getString("fullListCoinData", "");
//                Intent i = new Intent(getApplicationContext(), ConvertActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("fullListCoinData",listCoin);
//                i.putExtra("packBundle", bundle);
//                startActivity(i);
//                break;
//            }
//        }
//    }
}
