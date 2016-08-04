package com.cilatare.barcodescanner.model;

import android.content.Context;

import com.cilatare.barcodescanner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LightSpark on 7/19/2016.
 */
public class NavigationDrawerItem {

    private String title;
    private int imageId;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public static List<NavigationDrawerItem> getData(Context context) {
        List<NavigationDrawerItem> dataList = new ArrayList<>();

        int[] imageIds = getImages();
        String[] titles = getTitles(context);

        for (int i = 0; i < titles.length; ++ i) {
            NavigationDrawerItem navItem = new NavigationDrawerItem();
            navItem.setTitle(titles[i]);
            navItem.setImageId(imageIds[i]);
            dataList.add(navItem);
        }

        return dataList;
    }

    private static String[] getTitles(Context context) {
        return context.getResources().getStringArray(R.array.nav_drawer_labels);
    }

    private static int[] getImages() {
        return new int[]{
                R.mipmap.ic_list_black_36dp, R.mipmap.ic_search_black_36dp, R.mipmap.ic_scanner_black_36dp
        };
    }
}
