package com.example.android.whatsnext.utils;

import org.joda.time.DateTime;

/**
 * Created by Gerics on 2017.12.09..
 */

public class DateUtils {

    //get UNIX timestamp from date
    public static long getUnixTime(int year, int month, int day, int hour, int minute, int seconds) {
        DateTime dateTime = new DateTime(year, month, day, hour, minute, seconds);
        return dateTime.getMillis();
    }

    //get DateTime object from unix time
    public static DateTime getDateTime(long unixTime) {
        return new DateTime(unixTime);
    }

    //create date string
    public static String getDateString(long unixTime) {
        DateTime dt = new DateTime(unixTime);

        return dt.getYear() + "." +
                String.format("%02d", dt.getMonthOfYear()) + "." +
                String.format("%02d", dt.getDayOfMonth()) + " " +
                String.format("%02d", dt.getHourOfDay()) + ":" +
                String.format("%02d", dt.getMinuteOfHour());
    }

    //put zero infront of single digit number
    public static String addZero(String singleDigit) {
        return "0" + singleDigit;
    }
}
