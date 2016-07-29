package com.cilatare.barcodescanner;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.SheetsScopes;

/**
 * Created by android-sdk on 7/13/16.
 */
public class Constants {

    public static final int RESOLVE_CONNECTION_REQUEST_CODE = 1005;
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final int REQUEST_SELECT_FILE = 1004;
    public static final int REQUEST_GOOGLE_SIGNIN = 1005;
    public static final int REQUEST_SCAN_BARCODE = 49374;

    public final static int ADD_NEW_PRODUCT_CODE = 2000;
    public final static int SEARCH_PRODUCT_CODE = 2001;
    public final static int UPDATE_PRODUCT_CODE = 2002;

    public final static String EXTRA_SPREADSHEET_ID = "spreadsheetId";
    public final static String EXTRA_GOOGLE_ACCOUT = "mGoogleApiClient";
    public final static String EXTRA_GOOGLE_NAME = "googleNameAccount";

    public static GoogleApiClient mGoogleApiClient = null;
    public static GoogleAccountCredential mCredential = null;

    public static final String SHARED_SPREADSHEET_ID = "spreadsheetId";
    public static final String SHARED_PROFILE_NAME = "profileName";
    public static final String SHARED_PROFILE_PHOTO = "profilePhoto";

    public static final String PREFS = "MyPrefsFile";
    public static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    public static final String TAG = "BarCode Scanner";
}
