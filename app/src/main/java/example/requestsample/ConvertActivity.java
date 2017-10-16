package example.requestsample;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.guillotine.animation.GuillotineAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConvertActivity extends AppCompatActivity {
    private String coinIDs1[] = {"BTC", "LTC", "DASH", "ETH", "USD", "EUR"};
    private String coinIDs2[] = {"USD", "EUR" ,"BTC", "LTC", "DASH", "ETH"};
    private String coin1, coin2;
    private double coinValue;

    EditText et1, et2;
    TextView tv2;
    Spinner spinner1, spinner2;
    Button btnOk;
    Toolbar toolbar;
    RelativeLayout root;
    View contentHamburger;
    FloatingActionButton btnEx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        mapping();

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

        bindingSpinner(spinner1, coinIDs1, 1);
        bindingSpinner(spinner2, coinIDs2, 2);

        btnEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isDouble(et1.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Please input number only!", Toast.LENGTH_LONG).show();
                }else if(!et1.getText().toString().isEmpty()){
                    lockUI();
                    coin1 = spinner1.getSelectedItem().toString();
                    coin2 = spinner2.getSelectedItem().toString();
                    coinValue = Double.parseDouble(et1.getText().toString());
                    updateData();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please input coin value!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void mapping(){
        //et2 = (EditText) findViewById(R.id.et2);
        et1 = (EditText) findViewById(R.id.et1);
        tv2 = (TextView) findViewById(R.id.tv2);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.getBackground().setColorFilter(getResources().getColor(R.color.blue_bright), PorterDuff.Mode.SRC_ATOP);
        btnEx = (FloatingActionButton) findViewById(R.id.btnEx);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        root = (RelativeLayout) findViewById(R.id.root);
        contentHamburger = (View) findViewById(R.id.content_hamburger);
    }

    public void bindingSpinner(Spinner spinner, String[] coinIDs, int type){
        ArrayAdapter<String> adapter = null;
        if(type == 1)
        {
             adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_black,coinIDs);
        }else {
            adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_white,coinIDs);
        }

        adapter.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        spinner.setAdapter(adapter);
    }

    private void updateData() {
        OkHttpClient client = new OkHttpClient();
        String url = String.format("https://min-api.cryptocompare.com/data/pricemulti?fsyms=%s&tsyms=%s", coin1, coin2);
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ConvertActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openUI();
                        Toast.makeText(getApplicationContext(), "Please check your connection!", Toast.LENGTH_LONG).show();
                    }
                });
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();

                ConvertActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        convertCoin(myResponse);
                        openUI();
                    }
                });
            }
        });
    }

    private void convertCoin(String myRes) {
        //string -> json object
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(myRes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //get price
        double price = 0;
        try {
            JSONObject tickerObj = new JSONObject(jsonObj.getString(coin1));
            price = tickerObj.getDouble(coin2);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        price *= coinValue;
        if(coin1.equals(coin2)){price = coinValue;}
        //binding to view
        double round = round(price, 2);
        try {
            String rs = Double.toString(round);
            float a = tv2.getWidth()/rs.length();
            float b = et1.getTextSize()- 25;
            float textSize = a > b ? b : a;
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            tv2.setText(rs);
        }catch (Exception e){}
    }

    public boolean isDouble(String s){
        try {
            double num = Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public void lockUI(){
        findViewById(R.id.loadDing).setVisibility(View.VISIBLE); //show progressbar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); //disable touch
    }
    public void openUI(){
        findViewById(R.id.loadDing).setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}

