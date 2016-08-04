package com.cilatare.barcodescanner.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.adapter.RecyclerAdapter;
import com.cilatare.barcodescanner.model.Product;
import com.cilatare.barcodescanner.utils.Handles;
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

    private Activity activity;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;

    private MySharedPreferences mySharedPreferences;

    public GetProductsTask(Activity activity, GoogleAccountCredential credential, RecyclerView recyclerView, ProgressBar progressBar) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.mySharedPreferences = new MySharedPreferences(this.activity.getApplicationContext());

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

        Log.i(Constants.TAG, "getDataFromApi: " + mySharedPreferences.getSpreadsheetId());

        ValueRange response = this.mService.spreadsheets().values()
                .get(mySharedPreferences.getSpreadsheetId(), range)
                .execute();

        Log.i(Constants.TAG, "getDataFromApi: " + response.toString());

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

        RecyclerAdapter adapter = new RecyclerAdapter(activity.getApplicationContext(), products);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(activity.getApplicationContext()); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCancelled() {
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                Handles.showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode(), activity);
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        Constants.REQUEST_AUTHORIZATION);
            } else {
                Log.i(Constants.TAG, "The following error occurred:\n"
                        + mLastError.getMessage());
            }
        } else {
            Log.i(Constants.TAG, "Request cancelled.");
        }

        progressBar.setVisibility(View.INVISIBLE);
    }
}
