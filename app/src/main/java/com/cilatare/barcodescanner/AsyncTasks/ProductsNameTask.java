package com.cilatare.barcodescanner.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.cilatare.barcodescanner.Constants;
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
 * Created by android-sdk on 7/14/16.
 */
public class ProductsNameTask extends AsyncTask<Void, Void, List<String>> {

    private Activity activity;
    private ProgressBar progressBar;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private AutoCompleteTextView nameEditText;

    private MySharedPreferences mySharedPreferences;


    public ProductsNameTask(Activity activity, GoogleAccountCredential credential, ProgressBar progressBar,
                            AutoCompleteTextView nameEditText) {
        this.activity = activity;
        this.progressBar = progressBar;
        this.nameEditText = nameEditText;
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
                .get(mySharedPreferences.getSpreadsheetId(), range)
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

        ArrayAdapter adapter = new ArrayAdapter(activity.getApplicationContext(), android.R.layout.simple_dropdown_item_1line, output);
        nameEditText.setAdapter(adapter);
        nameEditText.setThreshold(0);

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
