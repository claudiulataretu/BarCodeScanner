package com.cilatare.barcodescanner.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.activities.MainActivity;
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
public class ProductsNameTask extends AsyncTask<Void, Void, List<String>> {
    private MainActivity mainActivity;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;

    private final String TAG = ProductsNameTask.class.getCanonicalName();

    public ProductsNameTask(MainActivity mainActivity, GoogleAccountCredential credential) {
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
    protected List<String> doInBackground(Void... params) {
        try {
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
    private List<String> getDataFromApi() throws IOException {
        String range = "Sheet1!A2:D";

        ValueRange response = this.mService.spreadsheets().values()
                .get(MainActivity.spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();

        List<String> results = new ArrayList<>();

        if (values != null) {
            for (List row : values) {
                results.add(row.get(1).toString());
            }
        }
        return results;
    }

    @Override
    protected void onPostExecute(List<String> output) {
        mainActivity.progressBar.setVisibility(View.INVISIBLE);

        /*ArrayAdapter adapter = new ArrayAdapter(mainActivity, android.R.layout.simple_dropdown_item_1line, output);
        mainActivity.nameEditText.setAdapter(adapter);
        mainActivity.nameEditText.setThreshold(0);*/
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
