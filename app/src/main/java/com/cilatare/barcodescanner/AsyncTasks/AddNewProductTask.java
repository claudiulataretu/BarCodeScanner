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
import com.google.api.services.sheets.v4.model.AppendCellsRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android-sdk on 7/14/16.
 */
public class AddNewProductTask extends AsyncTask<Product, Void, Void> {
    private Activity activity;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private ProgressBar progressBar;
    private MySharedPreferences mySharedPreferences;

    public AddNewProductTask(Activity activity, GoogleAccountCredential credential, ProgressBar progressBar) {
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

    /**
     * Background task to call Google Sheets API.
     *
     * @param params no parameters needed for this task.
     */


    @Override
    protected Void doInBackground(Product... params) {
        try {
            writeDataToApi(params[0]);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
        }
        return null;
    }

    private void writeDataToApi(Product product) throws IOException {
        List<Request> requests = new ArrayList<>();

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
                .setAppendCells(new AppendCellsRequest()
                        .setSheetId(0)
                        .setRows(rows)
                        .setFields("userEnteredValue")));

        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(requests);
        this.mService.spreadsheets().batchUpdate(mySharedPreferences.getSpreadsheetId(), batchUpdateRequest).execute();
    }

    @Override
    protected void onPostExecute(Void output) {
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
