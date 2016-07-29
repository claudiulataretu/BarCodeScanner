package com.cilatare.barcodescanner.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cilatare.barcodescanner.AsyncTasks.MyProfilePhotoTask;
import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.R;
import com.cilatare.barcodescanner.utils.MySharedPreferences;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.firebase.auth.AuthCredential;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class LaunchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, EasyPermissions.PermissionCallbacks {

    private Toolbar toolbar;

    private Button spreadsheetButton = null;
    private SignInButton signIn = null;
    private String spreadSheetId = null;

    private ImageView profileImageView = null;
    private ProgressBar progressBar =  null;

    private GoogleSignInOptions gso;
    private Uri profileImage;
    private String profileName;

    private MySharedPreferences mySharedPreferences;

    protected void chooseFile() {
        IntentSender openFileIS;

        // TODO: invoke the file chooser and display the file info
        openFileIS = new OpenFileActivityBuilder()
                .setActivityTitle("Select a File")
                .setMimeType(new String[] {"application/vnd.google-apps.spreadsheet"})
                .build(Constants.mGoogleApiClient);

        // This code will open the file picker Activity, and the result will
        // be passed to the onActivityResult function.
        try {
            startIntentSenderForResult(openFileIS, Constants.REQUEST_SELECT_FILE, null, 0,0,0);
        }
        catch (IntentSender.SendIntentException e) {
            Log.e(Constants.TAG, "Problem starting the OpenFileActivityBuilder");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mySharedPreferences = new MySharedPreferences(this);

        spreadsheetButton = (Button) findViewById(R.id.spreadsheet_button);
        signIn = (SignInButton) findViewById(R.id.sign_in);

        profileImageView = (ImageView) findViewById(R.id.launch_profile_picture);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        Auth.GoogleSignInApi.signOut(Constants.mGoogleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {
                                        Log.i(Constants.TAG, "Signed Out");
                                        signIn.setVisibility(View.VISIBLE);
                                        profileImageView.setImageResource(R.drawable.ic_profile);
                                        Constants.mCredential.setSelectedAccountName(null);
                                    }
                                }
                        );
                        break;
                }
                return true;
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Drive.SCOPE_FILE)
                .build();

        Constants.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Drive.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Constants.mGoogleApiClient.connect();

        Constants.mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(Constants.SCOPES))
                .setBackOff(new ExponentialBackOff());

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(Constants.mGoogleApiClient);
                startActivityForResult(signInIntent, Constants.REQUEST_GOOGLE_SIGNIN);
            }
        });

        spreadsheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.mGoogleApiClient.isConnected() &&
                        Constants.mCredential.getSelectedAccountName() != null) {
                    chooseFile();
                }
                else {
                    Toast.makeText(LaunchActivity.this, "Sign in first", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (Constants.mGoogleApiClient.isConnected()) {
            signIn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_GOOGLE_SIGNIN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    profileImage = account.getPhotoUrl();
                    profileName = account.getDisplayName();
                    Constants.mCredential.setSelectedAccountName(account.getEmail());
                    new MyProfilePhotoTask(profileImageView).execute(profileImage.toString());

                    signIn.setVisibility(View.GONE);
                }
                break;

            case Constants.REQUEST_SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    DriveId chosenFileID = data
                            .getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    // Given a Drive ID, we can convert it to a file reference
                    DriveFile theFile = chosenFileID.asDriveFile();

                    // Once we have the file reference, we can get the file's metadata
                    // TODO: display the file metadata
                    theFile.getMetadata(Constants.mGoogleApiClient).setResultCallback(
                            new ResultCallback<DriveResource.MetadataResult>() {
                                @Override
                                public void onResult(@NonNull DriveResource.MetadataResult metadataResult) {
                                    Metadata theData = metadataResult.getMetadata();

                                    spreadSheetId = theData.getAlternateLink().split("/")[5];

                                    mySharedPreferences.setSpreadsheetId(spreadSheetId);
                                    mySharedPreferences.setProfileName(profileName);
                                    mySharedPreferences.setProfilePhoto(profileImage.toString());

                                    Intent intent = new Intent(LaunchActivity.this, ListProductsActivity.class);
                                    startActivity(intent);
                                }
                            }
                    );
                }
                break;

            case Constants.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this,
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG).show();
                }
                break;

            case Constants.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Constants.PREFS, accountName);
                        editor.apply();
                        Constants.mCredential.setSelectedAccountName(accountName);
                    }
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(Constants.TAG, "onConnected: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(Constants.TAG, "Connection was suspended for some reason");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(Constants.TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, Constants.RESOLVE_CONNECTION_REQUEST_CODE);
            }
            catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        }
        else {
            GoogleApiAvailability gAPI = GoogleApiAvailability.getInstance();
            gAPI.getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
        }
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
                LaunchActivity.this,
                connectionStatusCode,
                Constants.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}
