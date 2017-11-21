package com.thfireplaces.JobEstimator;

/**
 * Created by wrsturm on 26/12/16.
 * Structure for line items in the estimate
 */

class Item {
    private String mQuantity;
    private final String mMaterial;
    private final String mProductName;
    private final String mDescription;
    private final String mUnitPrice;
    private String mLinePrice;

    Item(String quantity, String material, String name, String desc, String price, String line) {
        mQuantity = quantity;
        mMaterial = material;
        mProductName = name;
        mDescription = desc;
        mUnitPrice = price;
        mLinePrice = line;
    }

    String getQuantity() {
        return mQuantity;
    }

    String getMaterial() {
        return mMaterial;
    }

    String getProductName() {
        return mProductName;
    }

    String getDescription() {
        return mDescription;
    }

    String getUnitPrice() {
        return mUnitPrice;
    }

    String getLinePrice() {
        return mLinePrice;
    }


    String setQuantity(String quantity) {
        mQuantity = quantity;
        return mQuantity;
    }
/*
// Uncomment as needed
    String setMaterial(String material) {
        mMaterial = material;
        return mMaterial;
    }

    String setProductName(String name) {
        mProductName = name;
        return mProductName;
    }

    String setDescription(String desc) {
        mDescription = desc;
        return mDescription;
    }

    String setUnitPrice(String price) {
        mUnitPrice = price;
        return mUnitPrice;
    }

*/
    String setLinePrice(String line) {
        mLinePrice = line;
        return mLinePrice;
    }

}
