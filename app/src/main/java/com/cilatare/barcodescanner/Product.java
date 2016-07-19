package com.cilatare.barcodescanner;

/**
 * Created by android-sdk on 7/13/16.
 */
public class Product {

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

}
