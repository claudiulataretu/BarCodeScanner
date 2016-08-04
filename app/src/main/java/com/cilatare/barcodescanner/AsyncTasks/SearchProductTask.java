package com.cilatare.barcodescanner.AsyncTasks;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.activities.ProductActivity;
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
import java.util.List;

/**
 * Created by LightSpark on 7/27/2016.
 */

public class SearchProductTask extends AsyncTask<String, Void, Product> {

    private Activity activity;
    private ProgressBar progressBar;
    private int searchCode;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;

    private MySharedPreferences mySharedPreferences;

    public SearchProductTask(Activity activity, GoogleAccountCredential credential, ProgressBar progressBar, int searchCode) {
        this.activity = activity;
        this.progressBar = progressBar;
        this.searchCode = searchCode;
        this.mySharedPreferences = new MySharedPreferences(activity.getApplicationContext());

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
    protected Product doInBackground(String... params) {
        try {
            return getDataFromApi(params[0]);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    private Product getDataFromApi(String searchString) throws IOException {
        String range = "Sheet1!A2:D";

        ValueRange response = this.mService.spreadsheets().values()
                .get(mySharedPreferences.getSpreadsheetId(), range)
                .execute();

        List<List<Object>> values = response.getValues();

        if (values != null) {
            for (List row : values) {
                switch (searchCode) {
                    case Constants.SEARCH_BY_BARCODE_PRODUCT_CODE:
                        if (row.get(0).toString().equals(searchString)) {
                            return new Product(row.get(0).toString(),
                                    row.get(1).toString(),
                                    Double.valueOf(row.get(2).toString()),
                                    Double.valueOf(row.get(3).toString()));
                        }
                        break;

                    case Constants.SEARCH_BY_NAME_PRODUCT_CODE:
                        Log.i(Constants.TAG, "getDataFromApi: " + row.get(1).toString() + " " + searchString);
                        if (row.get(1).toString().equals(searchString)) {
                            return new Product(row.get(0).toString(),
                                    row.get(1).toString(),
                                    Double.valueOf(row.get(2).toString()),
                                    Double.valueOf(row.get(3).toString()));
                        }
                        break;
                }
            }
            switch (searchCode) {
                case Constants.SEARCH_BY_BARCODE_PRODUCT_CODE:
                    return new Product(searchString, "", Double.NaN, Double.NaN);
            }
        }

        return new Product("", "", Double.NaN, Double.NaN);
    }

    @Override
    protected void onPostExecute(Product product) {

            Intent intent = new Intent(activity.getApplicationContext(), ProductActivity.class);
            intent.putExtra(Constants.EXTRA_PRODUCT, product);

            activity.startActivity(intent);

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
