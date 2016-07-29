package com.cilatare.barcodescanner.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.activities.MainActivity;
import com.cilatare.barcodescanner.model.Product;
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

    private MainActivity mainActivity;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;

    public UpdateProductTask(MainActivity mainActivity, GoogleAccountCredential credential) {
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

    @Override
    protected Void doInBackground(Product... params) {
        try {
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
                this.mService.spreadsheets().batchUpdate(MainActivity.spreadsheetId, batchUpdateRequest).execute();
            }
        }


    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mainActivity.progressBar.setVisibility(View.INVISIBLE);
    }

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
                Log.i("ResultsAPI", "he following error occurred:\n"
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
