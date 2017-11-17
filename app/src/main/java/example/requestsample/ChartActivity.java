package example.requestsample;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.wang.avi.AVLoadingIndicatorView;

public class ChartActivity extends AppCompatActivity {
    private LineChart mChart;
    private String coinName;
    private String urlFormat = "https://graphs.coinmarketcap.com/currencies/%s/%d/%d/";
    private String url;
    private AVLoadingIndicatorView avi;
    private IAxisValueFormatter hourformatter;
    private IAxisValueFormatter dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chart);

        String indicator = getIntent().getStringExtra("indicator");
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.setIndicator(indicator);

        Intent callerIntent = getIntent();

        Bundle packBundle = callerIntent.getBundleExtra("packBundle");
        coinName = packBundle.getString("coinName");

//        url = String.format(urlFormat, coinName.toLowerCase());
//        System.out.println(url);

        mChart = (LineChart) findViewById(R.id.chart);

        mChart.setDrawGridBackground(false);

//        loadData();

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        hourformatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Date date = new Date((long) value);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int min = cal.get(Calendar.MINUTE);
                String minStr = min < 10 ? "0" + min : "" + min;
                return cal.get(Calendar.HOUR_OF_DAY) + ":" + minStr;
            }

        };


        dateFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Date date = new Date((long) value);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int min = cal.get(Calendar.MINUTE);
                String minStr = min < 10 ? "0" + min : "" + min;
                return cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH);
            }


        };



        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(dateFormatter);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line


//        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.setAxisMaximum(200f);
//        leftAxis.setAxisMinimum(-50f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        loadData(7);
//        setData(45, 100);

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(2500);
        //mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);

        // // dont forget to refresh the drawing
        // mChart.invalidate();
    }

    private void setData(ArrayList<String[]> priceList) {
        ArrayList<Entry> values = new ArrayList<Entry>();

        int i = 0;
        for (String[] pair: priceList) {
            float time = Float.parseFloat(pair[0]);
            float price = Float.parseFloat(pair[1]);
            values.add(new Entry(time, price, getResources().getDrawable(R.drawable.star)));
        }

        System.out.println(i);
        LineDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            //redraw
            mChart.invalidate();

            mChart.animateX(1000);


            System.out.println("redraw done");
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, coinName);

            set1.setDrawIcons(false);
            set1.setDrawCircles(false);
            // set the line to be drawn like this "- - - - - -"
//            set1.enableDashedLine(10f, 5f, 0f);
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
//            set1.setValueTextSize(0);
//            set1.setColor(Color.WHITE);
//            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
//            set1.setCircleRadius(3f);
//            set1.setDrawCircleHole(true);
//            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
//            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.chart_background);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }

        avi.hide();
    }

    private void loadData(int range) {

        long currentTime = System.currentTimeMillis();
        long startTime = currentTime - range * 24 * 60 * 60 * 1000L;

        url = String.format(urlFormat, coinName.toLowerCase(), startTime, currentTime);
        System.out.println(url);

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

                ChartActivity.this.runOnUiThread(new Runnable() {


                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObj = new JSONObject(myResponse);
                            JSONArray priceUsd = jsonObj.getJSONArray("price_usd");
                            ArrayList<String[]> priceList = new ArrayList<String[]>();

                            for (int i = 0; i < priceUsd.length(); i++){
//                                priceList.add(priceUsd.getJSONArray(i));
//                                System.out.println(priceUsd.getString(i)[0]);
//                                JSONObject itemObj = new JSONObject(priceUsd.getString(i));
                                JSONArray item = priceUsd.getJSONArray(i);
//                                System.out.println(item);
                                priceList.add(new String[]{item.getString(0), item.getString(1)});
                            }
                            System.out.println("load data done");
                            setData(priceList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });

    }


    public void loadDataOnClick(View view) {
        avi.show();
        int range = Integer.parseInt(view.getTag().toString());

        XAxis xAxis = mChart.getXAxis();
        if(range >= 7) {
            xAxis.setValueFormatter(dateFormatter);
        }
        else {
            xAxis.setValueFormatter(hourformatter);
        }


//        System.out.println(range);
        loadData(range);

        //redraw
//        mChart.invalidate();
    }
}
