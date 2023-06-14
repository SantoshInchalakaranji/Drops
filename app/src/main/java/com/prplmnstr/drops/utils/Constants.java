package com.prplmnstr.drops.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import com.prplmnstr.drops.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public  class Constants {
    public  static  int PRESENT = 1;
    public  static  int ABSENT = 0;
    public  static  int NO_ATTENDANCE = 2;
    public  static  String  ATTENDANCE = "Attendances";
    public static String  OUTLETS = "Outlets";
    public static String  PLANTS = "Plants";
    public static String  NAME = "name";
    public static String  DATES = "dates";
    public static String  RECHARGE_UNIT = "Recharge Unit";
    public static String  STATIONARY_UNIT = "Stationary Unit";
    public static String  MOBILE_UNIT = "Mobile Unit";
    public static String  WORKER = "Worker";
    public static String  INVESTOR = "Investor";
    public static String  ADMIN = "Admin";
    public static String    PLANT_NAME = "plantName";
    public static String    UNIT_NAME = "unitName";
    public static String    UNIT_TYPE_STRING = "unitType";
    public static String    RECORDS = "Records";
    public static String  PLANT_IMAGE = "image";
    public static String ADMIN_MAIL = "admin@gmail.com";
    public static String USERS = "Users";
    public static String SHARED_PREFERENCE = "Myprefs";
    public static String SAVED_USER_TYPE = "userType";
    public static  int PICK_IMAGE_REQUEST_CODE = 123;
    public static  int CHANGE_IMAGE_REQUEST_CODE = 111;
    public static List<String> UNIT_TYPE = Arrays.asList(STATIONARY_UNIT,MOBILE_UNIT,RECHARGE_UNIT);
    public static List<String> USER_TYPE = Arrays.asList(WORKER,INVESTOR);
    public static List<String> USER_TYPE_SIGN_IN = Arrays.asList(WORKER,INVESTOR,ADMIN);





}
