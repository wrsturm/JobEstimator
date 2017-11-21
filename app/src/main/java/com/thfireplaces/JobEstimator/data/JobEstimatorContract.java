package com.thfireplaces.JobEstimator.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class JobEstimatorContract {

    static final String CONTENT_AUTHORITY = "com.thfireplaces.jobestimator";
    static final String PATH_PRODUCTS = "products";
    static final String PATH_PRODUCT_TYPE = "product_type";
    static final String PATH_ACCESSORIES = "accessories";
    static final String PATH_JOB = "job";
    static final String PATH_SUPPLIER = "supplier";
    static final String PATH_CATEGORY = "category";
    static final String PATH_JOB_NUMBER = "job_number";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    private JobEstimatorContract() {
    }

    public static class ProductTable implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        public static final String _ID = BaseColumns._ID;
        public static final String COL_PRODUCT_TYPE = "product_type";
        public static final String COL_PRODUCT_PARENT = "parent";
        public static final String COL_PRODUCT_MATERIAL = "material";
        public static final String COL_PRODUCT_MODEL = "model";
        public static final String COL_PRODUCT_DESCRIPTION = "description";
        public static final String COL_PRODUCT_PRICE = "price";
        public static final String COL_PRODUCT_INSTRUCTIONS = "instructions";
        public static final String COL_PRODUCT_SUPPLIER = "supplier";
        public static final String COL_PRODUCT_BTUS = "btu";
        public static final String COL_PRODUCT_WEIGHT = "weight";
        public static final String COL_PRODUCT_VOLUME = "volume";
        public static final String COL_PRODUCT_PICTURE = "picture";
        public static final String COL_PRODUCT_ACCESSORIES = "accessories";
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        static final String TABLE_NAME = "products";
        public static final String[] productProjection = {
                _ID,
                COL_PRODUCT_TYPE,
                COL_PRODUCT_PARENT,
                COL_PRODUCT_MATERIAL,
                COL_PRODUCT_MODEL,
                COL_PRODUCT_DESCRIPTION,
                COL_PRODUCT_PRICE,
                COL_PRODUCT_INSTRUCTIONS,
                COL_PRODUCT_SUPPLIER,
                COL_PRODUCT_BTUS,
                COL_PRODUCT_WEIGHT,
                COL_PRODUCT_VOLUME,
                COL_PRODUCT_PICTURE,
                COL_PRODUCT_ACCESSORIES
        };
    }

    public static class ProductTypeTable implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT_TYPE);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT_TYPE;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT_TYPE;
        public static final String _ID = BaseColumns._ID;
        public static final String COL_PT_MODEL = "model";
        public static final String COL_SERIES = "series";
        public static final String COL_PT_PRODUCT_TYPE = "product_type";
        public static final String COL_PT_SHORT_DESC = "short_description";
        public static final String COL_PT_LONG_DESC = "long_description";
        static final String TABLE_NAME = "product_type";
        public static final String[] productTypeProjection = {
                _ID,
                COL_PT_MODEL,
                COL_SERIES,
                COL_PT_PRODUCT_TYPE,
                COL_PT_SHORT_DESC,
                COL_PT_LONG_DESC
        };
        }

    public static class AccessoriesTable implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ACCESSORIES);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACCESSORIES;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACCESSORIES;
        public final static String _ID = BaseColumns._ID;
        public static final String COL_ACC_TYPE = "product_type";
        public static final String COL_ACC_PARENT = "parent";
        public static final String COL_ACC_MATERIAL = "material";
        public static final String COL_ACC_MODEL = "model";
        public static final String COL_ACC_DESCRIPTION = "description";
        public static final String COL_ACC_PRICE = "price";
        public static final String COL_ACC_INSTRUCTIONS = "instructions";
        public static final String COL_ACC_SUPPLIER = "supplier";
        public static final String COL_ACC_BTUS = "btu";
        public static final String COL_ACC_WEIGHT = "weight";
        public static final String COL_ACC_VOLUME = "volume";
        public static final String COL_ACC_PICTURE = "picture";
        public static final String COL_ACC_ACCESSORIES = "accessories";
        static final String TABLE_NAME = "accessories";
        public static final String[] accessoriesProjection = {
                _ID,
                COL_ACC_TYPE,
                COL_ACC_PARENT,
                COL_ACC_MATERIAL,
                COL_ACC_MODEL,
                COL_ACC_DESCRIPTION,
                COL_ACC_PRICE,
                COL_ACC_INSTRUCTIONS,
                COL_ACC_SUPPLIER,
                COL_ACC_BTUS,
                COL_ACC_WEIGHT,
                COL_ACC_VOLUME,
                COL_ACC_PICTURE,
                COL_ACC_ACCESSORIES
        };
    }

    public static class JobTable implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_JOB);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_JOB;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_JOB;
        public static final String _ID = BaseColumns._ID;
        public static final String COL_JOB_NUMBER = "job_number";
        public static final String COL_INSTALL_DATE = "install_date";
        public static final String COL_DATE = "date";
        public static final String COL_NAME = "name";
        public static final String COL_ADDRESS = "address";
        public static final String COL_CITY = "city";
        public static final String COL_PROVINCE = "province";
        public static final String COL_POST_CODE = "post_code";
        public static final String COL_PHONE_RES = "phone_res";
        public static final String COL_PHONE_BUS = "phone_bus";
        public static final String COL_PHONE_MOB = "phone_mob";
        public static final String COL_EMAIL = "email";
        public static final String COL_CONSTRUCTION_TYPE = "construction_type";
        public static final String COL_DISCOUNT = "discount";
        public static final String COL_JOB_INSTRUCTIONS = "job_instructions";
        public static final String COL_PRODUCT_SELECTIONS = "product_selection";
        public static final String COL_PICTURES = "pictures";
        public static final int CONSTRUCTION_TYPE_TWO_STOREY = 0b000000001;//1
        public static final int CONSTRUCTION_TYPE_SINGLE_STOREY = 0b000000010;//2
        public static final int CONSTRUCTION_TYPE_MAIN_FLOOR = 0b000000100;//4
        public static final int CONSTRUCTION_TYPE_BASEMENT = 0b000001000;//8
        public static final int CONSTRUCTION_TYPE_CROSS_CORNER = 0b000010000;//16
        public static final int CONSTRUCTION_TYPE_THROUGH_WOOD = 0b000100000;//32
        public static final int CONSTRUCTION_TYPE_THROUGH_CONCRETE = 0b001000000;//64
        public static final int CONSTRUCTION_TYPE_INSIDE_WALL = 0b010000000;//128
        public static final int CONSTRUCTION_TYPE_OUTSIDE_WALL = 0b100000000;//256
        public static final String[] jobProjection = {
                _ID,
                COL_JOB_NUMBER,
                COL_INSTALL_DATE,
                COL_NAME,
                COL_DATE,
                COL_ADDRESS,
                COL_CITY,
                COL_PROVINCE,
                COL_POST_CODE,
                COL_PHONE_RES,
                COL_PHONE_BUS,
                COL_PHONE_MOB,
                COL_EMAIL,
                COL_CONSTRUCTION_TYPE,
                COL_DISCOUNT,
                COL_JOB_INSTRUCTIONS,
                COL_PRODUCT_SELECTIONS,
                COL_PICTURES
        };
        static final String TABLE_NAME = "job";
    }

    public static class JobNumberTable implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_JOB_NUMBER);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_JOB_NUMBER;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_JOB_NUMBER;
        public static final String _ID = BaseColumns._ID;
        public static final String COL_JOB_NUMBER = "job_number";
        public static final String COL_DATE = "date";
        static final String TABLE_NAME = "job_number";
        public static final String[] jobNumProjection = {_ID,COL_JOB_NUMBER,COL_DATE};

    }

    public static class CategoryTable implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CATEGORY);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String _ID = BaseColumns._ID;
        public static final String COL_CATEGORY = "category";
        static final String TABLE_NAME = "category";
        public static final String[] categoryProjection = {_ID,COL_CATEGORY};
    }

    public static class SupplierTable implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUPPLIER);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIER;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIER;
        public static final String _ID = BaseColumns._ID;
        public static final String COL_SUPPLIER_NAME = "name";
        static final String TABLE_NAME = "supplier";
        public static final String[] supplierProjection = {_ID,COL_SUPPLIER_NAME};

    }

}
