package com.cilatare.barcodescanner.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.MainActivity;
import com.cilatare.barcodescanner.Product;
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
 * Created by android-sdk on 7/14/16.
 */
public class SearchProductTask extends AsyncTask<String, Void, List<Product>> {
    private MainActivity mainActivity;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;

    private String query;

    private final String TAG = SearchProductTask.class.getCanonicalName();

    public SearchProductTask(MainActivity mainActivity, GoogleAccountCredential credential) {
        this.mainActivity = mainActivity;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Sheets API Android Quickstart")
                .build();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mainActivity.progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Background task to call Google Sheets API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<Product> doInBackground(String... params) {
        try {
            query = params[0];
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Fetch a list of names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     *
     * @return List of names and majors
     * @throws IOException
     */
    private List<Product> getDataFromApi() throws IOException {
        String range = "Sheet1!A2:D";
        List<Product> results = new ArrayList<>();
        ValueRange response = this.mService.spreadsheets().values()
                .get(MainActivity.spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();

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
    protected void onPostExecute(List<Product> output) {
        mainActivity.progressBar.setVisibility(View.INVISIBLE);

        for (Product product : output) {
            if (product.getName().equals(query) || product.getBarcode().equals(query)) {
                Log.i(TAG, "Product found");
                mainActivity.barcodeEditText.setText(product.getBarcode());
                mainActivity.nameEditText.setText(product.getName());
                mainActivity.priceEditText.setText(product.getPrice().toString());
                mainActivity.quantityEditText.setText(product.getQuantity().toString());

            }
        }
    }

    @Override
    protected void onCancelled() {
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                mainActivity.showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                mainActivity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        Constants.REQUEST_AUTHORIZATION);
            } else {
                Log.i(TAG, "he following error occurred:\n"
                        + mLastError.getMessage());
                Toast.makeText(mainActivity, "The following error occurred:\n"
                        + mLastError.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mainActivity, "Request cancelled.", Toast.LENGTH_LONG).show();
        }
        mainActivity.progressBar.setVisibility(View.INVISIBLE);
    }
}
