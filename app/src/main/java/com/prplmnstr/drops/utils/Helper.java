package com.prplmnstr.drops.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.prplmnstr.drops.models.Date;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Helper {



    public static String getDateInStringFormat(int day, int month, int year){
        String monthString = new DateFormatSymbols().getMonths()[month-1].substring(0,3);
        return String.format("%d %s,%d",day,monthString,year);
    }

    public static Bitmap stringToBitmap(String encodedString) {
        byte[] decodedBytes = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    public static boolean containsOnlyAlphabets(String input) {
        // Regular expression to match alphabets
        String regex = "^[a-zA-Z]+$";

        // Check if the input matches the regular expression
        return input.matches(regex);
    }
    public static Date getTodayDateObject(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month value is 0-based, so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Date date = new Date();
        String monthString = new DateFormatSymbols().getMonths()[month-1].substring(0,3);
        String dateInStringFormat = String.format("%d %s,%d",day,monthString,year);
        date.setDay(day);
        date.setMonth(month);
        date.setYear(year);
        date.setDateInStringFormat(dateInStringFormat);
        return date;

    }
}
