package com.cilatare.barcodescanner.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cilatare.barcodescanner.AsyncTasks.GetProductsTask;
import com.cilatare.barcodescanner.AsyncTasks.MyProfilePhotoTask;
import com.cilatare.barcodescanner.AsyncTasks.SearchProductTask;
import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.NavigationDrawerFragment;
import com.cilatare.barcodescanner.R;
import com.cilatare.barcodescanner.utils.GenerateToolbar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private Toolbar toolbar;

    public EditText barcodeEditText = null;
    public AutoCompleteTextView nameEditText = null;
    public EditText priceEditText = null;
    public EditText quantityEditText = null;

    private Button scanButton = null;
    private Button querySheet = null;
    private Button searchButton = null;
    private Button updateButton = null;

    public ProgressBar progressBar = null;

    GoogleAccountCredential mCredential;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    private int PRODUCT_CODE;

    public static String spreadsheetId = null;
    private String profileName;
    private String profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //barcodeEditText = (EditText) findViewById(R.id.barode_editText);
        //nameEditText = (AutoCompleteTextView) findViewById(R.id.prodName_editText);
        //priceEditText = (EditText) findViewById(R.id.price_editText);
        //quantityEditText = (EditText) findViewById(R.id.quant_editText);
        //scanButton = (Button) findViewById(R.id.scan_button);
        //querySheet = (Button) findViewById(R.id.query_button);
        searchButton = (Button) findViewById(R.id.search_button);
        //updateButton = (Button) findViewById(R.id.update_button);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        GenerateToolbar generateToolbar = new GenerateToolbar(this, toolbar);
        generateToolbar.inflateToolbar();


        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
            setUpRecyclerView();
        }
        else {
            setUpRecyclerView();
        }

        setUpDrawer();


        /*if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
            new ProductsNameTask(this, mCredential).execute();
        }
        else {
            new ProductsNameTask(this, mCredential).execute();
        }*/

        /*nameEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new SearchProductTask(MainActivity.this, mCredential).execute(parent.getItemAtPosition(position).toString());
            }
        });

        scanButton.setOnClickListener(new ScanBarcode());
        querySheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PRODUCT_CODE = Constants.ADD_NEW_PRODUCT_CODE;
                getResultsFromApi();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PRODUCT_CODE = Constants.SEARCH_PRODUCT_CODE;
                getResultsFromApi();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PRODUCT_CODE = Constants.UPDATE_PRODUCT_CODE;
                getResultsFromApi();
            }
        });*/

    }


    private void setUpDrawer() {
        TextView profileNameTextView = (TextView) findViewById(R.id.profile_name);
        ImageView profileImageView = (ImageView) findViewById(R.id.profile_picture);
        profileNameTextView.setText(profileName);
        new MyProfilePhotoTask(profileImageView).execute(profilePhoto);
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        new GetProductsTask(this, mCredential, recyclerView, progressBar).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case Constants.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this,
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG).show();
                } else {
                    getResultsFromApi() ;
                }
                break;
            case Constants.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case Constants.REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
            case Constants.REQUEST_SCAN_BARCODE:
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

                if (scanningResult != null) {
                    String scanContent = scanningResult.getContents();
                    barcodeEditText.setText(scanContent);
                    new SearchProductTask(this, mCredential).execute(scanContent);
                }
                else {
                    Toast.makeText(this, "No scan data received", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG).show();
        } else {
/*            String barcode = barcodeEditText.getText().toString();
            String name = nameEditText.getText().toString();
            String price = priceEditText.getText().toString();
            String quantity = quantityEditText.getText().toString();
            Product product = null;
            if (!barcode.isEmpty() &&
                    !name.isEmpty() &&
                    !price.isEmpty() &&
                    !quantity.isEmpty()) {
                product = new Product(
                        barcode,
                        name,
                        Double.valueOf(price),
                        Double.valueOf(quantity)
                );
            }


                switch (PRODUCT_CODE) {
                case Constants.ADD_NEW_PRODUCT_CODE:
                    if (product != null) {
                        new AddNewProductTask(this, mCredential, product).execute();
                    }
                    else {
                        Toast.makeText(this, "One of the product field is empty", Toast.LENGTH_LONG).show();
                    }
                    break;
                case Constants.SEARCH_PRODUCT_CODE:
                    if (!barcode.isEmpty()) {
                        new SearchProductTask(this, mCredential).execute(barcode);
                    }
                    break;
                case Constants.UPDATE_PRODUCT_CODE:
                    if (product != null) {
                        new UpdateProductTask(this, mCredential)
                                .execute(product);
                    }
                    break;
            }*/
        }
    }

    @AfterPermissionGranted(Constants.REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        Constants.REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    Constants.REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                Constants.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    class ScanBarcode implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            barcodeEditText.setText("");
            nameEditText.setText("");
            priceEditText.setText("");
            quantityEditText.setText("");

            IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
            scanIntegrator.initiateScan();
        }
    }

}
