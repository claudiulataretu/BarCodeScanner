package com.cilatare.barcodescanner.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.model.Product;
import com.cilatare.barcodescanner.utils.Handles;
import com.cilatare.barcodescanner.utils.MySharedPreferences;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android-sdk on 7/14/16.
 */
public class UpdateProductTask extends AsyncTask<Product, Void, Void> {

    private Activity activity;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private ProgressBar progressBar;
    private MySharedPreferences mySharedPreferences;

    public UpdateProductTask(Activity activity, GoogleAccountCredential credential, ProgressBar progressBar) {
        this.activity = activity;
        this.progressBar = progressBar;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        mySharedPreferences = new MySharedPreferences(activity.getApplicationContext());

        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Sheets API Android Quickstart")
                .build();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Product... params) {
        try {
            String range = "Sheet1!A2:D";
            List<Product> results = new ArrayList<>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(mySharedPreferences.getSpreadsheetId(), range)
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

            updateDataToApi(results, params[0]);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
        }
        return null;
    }

    private void updateDataToApi(List<Product> products, Product product) throws IOException {
        List<Request> requests = new ArrayList<>();

        for (Product iter : products) {
            if (iter.getBarcode().equals(product.getBarcode())) {
                List<CellData> values = new ArrayList<>();

                values.add(new CellData()
                        .setUserEnteredValue(new ExtendedValue().setStringValue(product.getBarcode())));
                values.add(new CellData()
                        .setUserEnteredValue(new ExtendedValue().setStringValue(product.getName())));
                values.add(new CellData()
                        .setUserEnteredValue(new ExtendedValue().setNumberValue(product.getPrice())));
                values.add(new CellData()
                        .setUserEnteredValue(new ExtendedValue().setNumberValue(product.getQuantity())));

                List<RowData> rows = new ArrayList<>();
                rows.add(new RowData()
                        .setValues(values));

                requests.add(new Request()
                        .setUpdateCells(new UpdateCellsRequest()
                                .setRows(rows)
                                .setFields("userEnteredValue")
                                .setStart(new GridCoordinate()
                                        .setRowIndex(products.indexOf(iter) + 1))));

                BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                        .setRequests(requests);
                this.mService.spreadsheets().batchUpdate(mySharedPreferences.getSpreadsheetId(), batchUpdateRequest).execute();
            }
        }


    }

    @Override
    protected void onPostExecute(Void aVoid) {
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
