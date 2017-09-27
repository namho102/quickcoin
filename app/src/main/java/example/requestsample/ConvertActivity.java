package example.requestsample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.attr.key;

public class ConvertActivity extends AppCompatActivity {

    private String coinIDs[] = {"BTC", "LTC", "DASH", "ETH"};

    TextView tvRs;
    EditText etInput;
    Spinner spiner;
    Button btnOk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        mapping();

        bindingSpinner();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = spiner.getSelectedItem().toString();
                if(!etInput.getText().toString().isEmpty()){
                    double price = Integer.parseInt(etInput.getText().toString());
                    tvRs.setText(String.valueOf(convertCoinToUsd(type, price)));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please input coin value", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void mapping(){
        tvRs = (TextView) findViewById(R.id.tvResult);
        etInput = (EditText) findViewById(R.id.etInput);
        spiner = (Spinner) findViewById(R.id.spinner);
        btnOk = (Button) findViewById(R.id.btnOk);
    }

    public void bindingSpinner(){
        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (
                        this,
                        android.R.layout.simple_spinner_item,
                        coinIDs
                );
        adapter.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        spiner.setAdapter(adapter);
    }

    public double convertCoinToUsd(String type,double price){
        double coinValue = getValueFromCoinName(type);
        return coinValue * price;
    }

    public JSONObject getMainActivityData(){
        Intent callerIntent = getIntent();
        Bundle packBundle = callerIntent.getBundleExtra("packBundle");
        String s = packBundle.getString("fullListCoinData");
        try {
            JSONObject jsonObj = new JSONObject(s);
            return jsonObj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public double getValueFromCoinName(String coinName){
        JSONObject data = getMainActivityData();
            try {
                JSONObject tickerObj = new JSONObject(data.getString(coinName));
                return tickerObj.getDouble("USD");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }

}

