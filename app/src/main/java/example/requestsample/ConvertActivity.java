package example.requestsample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.attr.key;

public class ConvertActivity extends AppCompatActivity {

    private ArrayList<DataModel> listCoin;
    private String coinIDs[] = {"BTC", "LTC", "DASH", "ETH"};
    private ArrayList<String> listNameCoin = new ArrayList<String>() {{
        add("BTC");
        add("LTC");
        add("DASH");
        add("ETH");
    }};


    TextView tvRs;
    EditText etInput;
    Spinner spiner;
    Button btnOk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        tvRs = (TextView) findViewById(R.id.tvResult);
        etInput = (EditText) findViewById(R.id.etInput);
        spiner = (Spinner) findViewById(R.id.spinner);
        btnOk = (Button) findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "";
                int price = Integer.parseInt(etInput.getText().toString());
                tvRs.setText(convertCoinToUsd(type, price));
            }
        });

        getMainActivityData();
    }

    public void binddingCombobox(){
        //bad design and tight couple
        //spiner.get
    }

    public String convertCoinToUsd(String type, int price){
        return "";
    }

    public void getMainActivityData(){
        Intent callerIntent = getIntent();

        Bundle packBundle = callerIntent.getBundleExtra("packBundle");
        String s = packBundle.getString("fullListCoinData");
        try {
            JSONObject jsonObj = new JSONObject(s);

            for(int i = 0; i<jsonObj.length(); i++) {

            }

//            for(DataModel item: arrayList) {
//                item.price = getPrice(jsonObj, item.coinId);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

