package com.elcom.vitalsign.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author anhdv
 */
public class DateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);
    
    public static String getPartitionsByDateRange(String startTime, String endTime) {
        String partitions = "";
        String yyymmddStartTime = startTime.split(" ")[0];
        String yyymmddEndTime = endTime.split(" ")[0];
        if( yyymmddStartTime.equals(yyymmddEndTime) )
            partitions = "p_" + yyymmddStartTime;
        else {
            String dateFormat = "yyyy_MM_dd";
            Date dateStart = convertStringToJavaDate(yyymmddStartTime, dateFormat);
            Date dateEnd = convertStringToJavaDate(yyymmddEndTime, dateFormat);
            List<Date> lstDate = getDatesBetween(dateStart, dateEnd);
            partitions = lstDate.stream().map(
                (date) -> "p_" + convertDateToString(date, dateFormat) + ", "
            ).reduce(partitions, String::concat);
            if( partitions.endsWith(", ") )
                partitions = partitions.substring(0, partitions.length()-2);
        }
        return partitions;
    }
    
    // convert a string to java.util.Date
    public static Date convertStringToJavaDate(String date, String format) {
        if( date==null || format==null )
            return null;
        try {
            DateFormat dataFormat = new SimpleDateFormat(format);
            return dataFormat.parse(date);
        }catch(ParseException ex) {
            LOGGER.error(ex.toString());
        }
        return null;
    }
    
    public static String convertDateToString(Date date, String format) {
        if( date==null || format==null )
            return null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);  
            return dateFormat.format(date);
        }catch(Exception ex) {
            LOGGER.error(ex.toString());
        }
        return null;
    }

    // plus days to a date
    public static Date plusJavaDays(Date date, int days) {
        // convert to jata-time
        DateTime fromDate = new DateTime(date);
        DateTime toDate = fromDate.plusDays(days);
        // convert back to java.util.Date
        return toDate.toDate();
    }

    // return a list of dates between the fromDate and toDate
    public static List<Date> getDatesBetween(Date fromDate, Date toDate) {
        List<Date> dates = new ArrayList<>(0);
        Date date = fromDate;
        while (date.before(toDate) || date.equals(toDate)) {
            dates.add(date);
            date = plusJavaDays(date, 1);
        }
        return dates;
    }
}
