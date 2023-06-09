package com.prplmnstr.drops.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import com.prplmnstr.drops.R;

public  class Constants {
    public static String  OUTLETS = "Outlets";
    public static String  PLANTS = "Plants";
    public static String  NAME = "name";
    public static String ADMIN_MAIL = "admin@gmail.com";
    public static String USERS = "Users";
    public static  int PICK_IMAGE_REQUEST_CODE = 123;

    public  enum DBTASK {
        SUCCESS,FAIL
    }
    public static String getPlaceHolderUri(Activity activity){
        String imageUri = "android.resource://" + activity.getPackageName() + "/" + R.drawable.add_image;
        return imageUri;
    }
    public static Bitmap stringToBitmap(String encodedString) {
        byte[] decodedBytes = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
