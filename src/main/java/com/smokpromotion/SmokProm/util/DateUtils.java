package com.smokpromotion.SmokProm.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Pattern;

public class DateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);


    public static boolean  isValid(String date, String format) {
        boolean ret = false;
        if (format == null || "".equals(format) ) {
            format ="yyyy-MM-dd";
        }
        try {

            Date date1=new SimpleDateFormat(format).parse(date);
            ret = true;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static Date  fromStringToDate(String date, String format) {
        boolean ret = false;
        Date dateConvert = null;
        if (!"".equals(date) && null != date) {
            if (format == null || "".equals(format)) {
                format = "yyyy-MM-dd";
            }
            try {

                dateConvert = new SimpleDateFormat(format).parse(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return dateConvert;
    }

    public static String  fromDateToString(LocalDate date, String format) {
        return fromDateToString(fromLocalDateToDate(date), format);
    }

    public static String  fromDateToString(Date date, String format) {
        boolean ret = false;
        String stringConvert = null;
        if (format == null || "".equals(format) ) {
            format ="yyyy-MM-dd";
        }
        try {

            stringConvert  = DateFormatUtils.format(date, format);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringConvert ;
    }


    public static Date fromLocalDateToDate(LocalDate in) {
        Date date =null;
        if (in != null) {
            try {

                date = java.sql.Date.valueOf(in);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return date;

    }

    public static LocalDate fromDateToLocalDate(Date in) {
        LocalDate date =null;
        if (in != null) {
            try {
                // conversion that handles java.util.Date and subclass java.sql.Date
                ZoneId defaultZoneId = ZoneId.systemDefault();
                date = Instant.ofEpochMilli(in.getTime()).atZone(defaultZoneId).toLocalDate();

            } catch (Exception e) {
               // e.printStackTrace();
                try {
                 date =  ((java.sql.Date) in).toLocalDate();
                } catch (Exception ee) {
                  //  e.printStackTrace();
                }
            }
        }

        return date;

    }


    public static LocalDateTime fromLocalDateToLocalDateTime(LocalDate in) {
        LocalDateTime ret =null;
        if (in != null) {
            try {

                ret = LocalDateTime.of(in.getYear(), in.getMonth(), in.getDayOfMonth(), 0, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }


    /**
     * Tries to convert the input string into a LocalDate instance, trying various formats.
     * @param dateStr the string to convert
     * @return an equivalent LocalDate instance, or null if no conversion was possible.
     */
    public static LocalDate fromStringToLocalDate(String dateStr) {
        // similar to ReportCriteriaForm.getStartDateLdt
        LocalDate ldt = null;
        if (dateStr != null && dateStr.length() > 0) {

            String dateFormat = null;
            String trimDateStr = dateStr.trim();

            // base validation of the string that should be in some kind of date format - digits & hyphens or slashes
            // Allow for an optional time element, assumed to be in colon-separated form
            final String REGEX_DATE_BASE_FORMAT = "^\\d+[/-]\\d+[/-]\\d+\\s*(\\d+:\\d+:\\d+){0,1}$";

            if (Pattern.matches(REGEX_DATE_BASE_FORMAT, trimDateStr)) {
                if (trimDateStr.contains("/")) {
                    // Format coming from fallback datepicker where browser does not support HTML5 date input.
                    dateFormat = "dd/MM/yyyy HH:mm:ss";
                } else {
                    // Default format coming from brower HTML5 input datepicker.
                    dateFormat = "yyyy-MM-dd HH:mm:ss";
                }

                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
                    ldt = LocalDate.parse(trimDateStr + " 00:00:00", formatter);
                } catch (java.time.DateTimeException e) {
                    LOGGER.error(String.format("DateUtils.parseDateString Could not parse [%s] as date in format %s.", trimDateStr, dateFormat));
                }
            }
        }
        return ldt;
    }


}
