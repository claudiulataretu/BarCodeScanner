package com.cilatare.barcodescanner.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cilatare.barcodescanner.AsyncTasks.GetProductsTask;
import com.cilatare.barcodescanner.AsyncTasks.MyProfilePhotoTask;
import com.cilatare.barcodescanner.Constants;
import com.cilatare.barcodescanner.NavigationDrawerFragment;
import com.cilatare.barcodescanner.R;
import com.cilatare.barcodescanner.utils.GenerateToolbar;
import com.cilatare.barcodescanner.utils.MySharedPreferences;

public class ListProductsActivity extends AppCompatActivity {

    private Toolbar toolbar = null;
    private ProgressBar progressBar = null;

    MySharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_products);

        mySharedPreferences = new MySharedPreferences(this);
        if (mySharedPreferences.getConnected()) {
            Constants.mGoogleApiClient.connect();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        GenerateToolbar generateToolbar = new GenerateToolbar(this, toolbar);
        generateToolbar.inflateToolbar();

        setUpDrawer();

        setUpRecyclerView();
    }

    private void setUpDrawer() {
        TextView profileNameTextView = (TextView) findViewById(R.id.profile_name);
        ImageView profileImageView = (ImageView) findViewById(R.id.profile_picture);

        profileNameTextView.setText(mySharedPreferences.getProfileName());
        if (mySharedPreferences.getProfilePhoto() != null) {
            new MyProfilePhotoTask(profileImageView).execute(mySharedPreferences.getProfilePhoto());
        }

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        new GetProductsTask(this, Constants.mCredential, recyclerView, progressBar).execute();
    }
}
