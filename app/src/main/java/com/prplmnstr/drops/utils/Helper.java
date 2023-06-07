package com.prplmnstr.drops.utils;

import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Map;

public class Helper {



    public String getDateInStringFormat(int day, int month, int year){
        String monthString = new DateFormatSymbols().getMonths()[month-1].substring(0,3);
        return String.format("%d %s,%d",day,monthString,year);
    }
}
