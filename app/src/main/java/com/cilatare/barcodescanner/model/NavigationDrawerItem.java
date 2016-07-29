package com.cilatare.barcodescanner.model;

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

    public static List<NavigationDrawerItem> getData() {
        List<NavigationDrawerItem> dataList = new ArrayList<>();

        int[] imageIds = getImages();
        String[] titles = getTitles();

        for (int i = 0; i < titles.length; ++ i) {
            NavigationDrawerItem navItem = new NavigationDrawerItem();
            navItem.setTitle(titles[i]);
            navItem.setImageId(imageIds[i]);
            dataList.add(navItem);
        }

        return dataList;
    }

    private static String[] getTitles() {
        return new String[] {
                "List Products", "Search Products", "Scan Product"};
    }

    private static int[] getImages() {
        return new int[]{
                R.mipmap.ic_list_black_36dp, R.drawable.ic_search, R.mipmap.ic_scanner_black_36dp
        };
    }
}
