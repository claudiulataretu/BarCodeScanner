package com.cilatare.barcodescanner.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.activities.MainActivity;
import com.cilatare.barcodescanner.model.Product;
import com.cilatare.barcodescanner.adapter.RecyclerAdapter;
import com.cilatare.barcodescanner.utils.MySharedPreferences;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LightSpark on 7/27/2016.
 */
public class GetProductsTask extends AsyncTask<Void, Void, List<Product>> {

    private Context context;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;

    private MySharedPreferences mySharedPreferences;

    public GetProductsTask(Context context, GoogleAccountCredential credential, RecyclerView recyclerView, ProgressBar progressBar) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.mySharedPreferences = new MySharedPreferences(this.context);

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Sheets API Android Quickstart")
                .build();
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<Product> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    private List<Product> getDataFromApi() throws IOException {
        String range = "Sheet1!A2:D";

        ValueRange response = this.mService.spreadsheets().values()
                .get(mySharedPreferences.getSpreadsheetId(), range)
                .execute();

        List<List<Object>> values = response.getValues();

        List<Product> results = new ArrayList<>();

        if (values != null) {
            for (List row : values) {
                results.add(new Product(row.get(0).toString(),
                        row.get(1).toString(),
                        Double.valueOf(row.get(2).toString()),
                        Double.valueOf(row.get(3).toString())));
            }
        }

        return results;
    }

    @Override
    protected void onPostExecute(List<Product> products) {

        RecyclerAdapter adapter = new RecyclerAdapter(context, products);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(context); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCancelled() {
        if (mLastError != null) {
                Log.i(Constants.TAG, "he following error occurred:\n"
                        + mLastError.getMessage());
        } else {
            Toast.makeText(context, "Request cancelled.", Toast.LENGTH_LONG).show();
        }

        progressBar.setVisibility(View.INVISIBLE);
    }
}
