package com.cilatare.barcodescanner.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android-sdk on 7/13/16.
 */
public class Product implements Parcelable {

    private String barcode;
    private String name;
    private Double price;
    private Double quantity;

    public Product(String barcode,
                   String name,
                   Double price,
                   Double quantity) {
        this.barcode = barcode;
        this.name =  name;
        this.price = price;
        this.quantity = quantity;
    }


    protected Product(Parcel in) {
        this.barcode = in.readString();
        this.name = in.readString();
        this.price = in.readDouble();
        this.quantity = in.readDouble();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(barcode);
        parcel.writeString(name);
        parcel.writeDouble(price);
        parcel.writeDouble(quantity);
    }


}
