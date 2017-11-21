package com.thfireplaces.JobEstimator;

/**
 * Created by wrsturm on 26/12/16.
 * Structure for line items in the estimate
 */

class Accessory {
    private final String mMaterial;
    private final String mProductName;
    private final String mDescription;
    private final String mUnitPrice;

    Accessory(String material, String name, String desc, String price) {
        mMaterial = material;
        mProductName = name;
        mDescription = desc;
        mUnitPrice = price;
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

}
