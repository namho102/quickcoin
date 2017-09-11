package example.requestsample;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by foo on 07/09/17.
 */

public class RequestDataTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://api.coindesk.com/v1/bpi/currentprice.json";
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;

        try {
            response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
