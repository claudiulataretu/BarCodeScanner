package com.cilatare.barcodescanner.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cilatare.barcodescanner.Constants;

/**
 * Created by LightSpark on 7/28/2016.
 */
public class MySharedPreferences {

    private Boolean isConnected;

    private String spreadsheetId;

    private String profileName;
    private String profilePhoto;
    private String profileEmail;
    private String language;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public MySharedPreferences(Context context) {
        this.settings = context.getSharedPreferences(Constants.PREFS, 0);
        this.editor = this.settings.edit();
    }

    public Boolean getConnected() {
        return settings.getBoolean(Constants.SHARED_CONNECTED, false);
    }

    public void setConnected(Boolean connected) {
        editor.putBoolean(Constants.SHARED_CONNECTED, connected);
        editor.commit();
    }

    public String getSpreadsheetId() {
        spreadsheetId = settings.getString(Constants.SHARED_SPREADSHEET_ID, null);
        return spreadsheetId;
    }

    public void setSpreadsheetId(String spreadsheetId) {
        editor.putString(Constants.SHARED_SPREADSHEET_ID, spreadsheetId);
        editor.commit();
    }

    public String getProfileName() {
        profileName = settings.getString(Constants.SHARED_PROFILE_NAME, null);
        return profileName;
    }

    public void setProfileName(String profileName) {
        editor.putString(Constants.SHARED_PROFILE_NAME, profileName);
        editor.commit();
    }

    public String getProfilePhoto() {
        profilePhoto = settings.getString(Constants.SHARED_PROFILE_PHOTO, null);
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        editor.putString(Constants.SHARED_PROFILE_PHOTO, profilePhoto);
        editor.commit();
    }

    public String getProfileEmail() {
        profileEmail = settings.getString(Constants.SHARED_PROFILE_EMAIL, null);
        return profileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        editor.putString(Constants.SHARED_PROFILE_EMAIL, profileEmail);
        editor.commit();
    }

    public String getLanguage() {
        return settings.getString(Constants.SHARED_LANGUAGE, null);
    }

    public void setLanguage(String language) {
        editor.putString(Constants.SHARED_LANGUAGE, language);
        editor.commit();
    }
}
