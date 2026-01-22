package com.floodguard.backend.util;


import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Date utility
 *
 * @author INIT
 */
@Slf4j
public class DateUtil {
    public static final String FORMAT_YEAR_MONTH = "yyyyMM";
    public static final String FORMAT_YEAR_MONTH_DAY = "yyyyMMdd";
    public static final String FORMAT_YEAR_MONTH_DAY_HOUR = "yyyyMMddHH";
    public static final String FORMAT_YEAR_MONTH_DAY_MINUTE = "yyyyMMddHHmm";
    public static final String FORMAT_YEAR_MONTH_DAY_SEC = "yyyyMMddHHmmss";
    public static final String FORMAT_YEAR_MONTH_DAY_SEC_HYPHEN = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YEAR_MONTH_DAY_HYPHEN = "yyyy-MM-dd";
    public static final String FORMAT_MINUTE_DAY_MONTH_YEAR_SLASH = "HH:mm dd/MM/yyyy";
    public static final String FORMAT_DAY_MONTH_YEAR_SLASH = "dd/MM/yyyy";
    public static final String FORMAT_MONTH_YEAR_SLASH = "MM/yyyy";
    public static final String FORMAT_DAY_MONTH_YEAR_SLASH_HOUR_MINUTE = "dd/MM/yyyy HH:mm";

    /**
     * Lấy ngày giờ hiện tại hệ thống
     *
     * @return
     */
    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * get timestamp by current date
     *
     * @return Timestamp
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Lấy ngày giờ hiện tại hệ thống
     *
     * @return
     */
    public static Date getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Lấy giờ bắt đầu trong ngày
     *
     * @return
     */
    public static Date getStartDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Lấy 3 tháng gần nhất
     *
     * @return
     */
    public static List<String> getLast3Month() {
        List<String> lsMonth = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        while (lsMonth.size() < 3) {
            lsMonth.add(toString(cal.getTime(), FORMAT_YEAR_MONTH));
            cal.set(Calendar.MONTH, -1);
        }
        return lsMonth;
    }

    /**
     * group by month
     *
     * @return
     */
    public static Date groupByMonth(Date date, Integer month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, (DateUtil.getMonth(cal.getTime()) / month) * month);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * group by day
     *
     * @return
     */
    public static Date groupByDay(Date date, Integer day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, -1);
        Double value = Math.ceil((float) DateUtil.getDay(cal.getTime()) / day) * day;
        cal.set(Calendar.DAY_OF_MONTH, value.intValue());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * group by hour
     *
     * @return
     */
    public static Date groupByHour(Date date, Integer hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Double value = Math.ceil((float) DateUtil.getHour(cal.getTime()) / hour) * hour;
        cal.set(Calendar.HOUR_OF_DAY, value.intValue());
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * group by minute
     *
     * @return
     */
    public static Date groupByMinute(Date date, Integer minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Double value = Math.ceil((float) DateUtil.getMinute(cal.getTime()) / minute) * minute;
        cal.set(Calendar.MINUTE, value.intValue());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Lấy giờ bắt đầu trong ngày
     *
     * @return
     */
    public static Date getEndDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * Lấy giờ hiện tại hệ thống
     *
     * @return
     */
    public static Date getCurrentHour() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Lấy ngày trước
     *
     * @param date
     * @return
     */
    public static Date getPreDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    /**
     * Lấy ngày trước
     *
     * @return
     */
    public static Date getLast3Day() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -3);
        return cal.getTime();
    }

    /**
     * Lấy ngày kế tiếp
     *
     * @param date
     * @return
     */
    public static Date getNextDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }

    public static Date getNextDay(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, day);
        return cal.getTime();
    }

    /**
     * Lấy tháng kế tiếp
     *
     * @param date
     * @return
     */
    public static Date getNextMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    /**
     * Lấy phút kế tiếp
     *
     * @param date
     * @return
     */
    public static Date getNextMinute(Date date, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }

    /**
     * Lấy phút trước đó
     *
     * @param date
     * @return
     */
    public static Date getPreMinute(Date date, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute * -1);
        return cal.getTime();
    }

    /**
     * Lấy giờ kế tiếp
     *
     * @param date
     * @return
     */
    public static Date getNextHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, 1);
        return cal.getTime();
    }

    /**
     * Lấy giờ trước
     *
     * @param date
     * @return
     */
    public static Date getPreHour(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hour * -1);
        return cal.getTime();
    }

    /**
     * Lấy tháng
     *
     * @param date date
     * @return data
     */
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    /**
     * Lấy ngày
     *
     * @param date date
     * @return data
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Lấy ngày trong tuần
     *
     * @param date date
     * @return data
     */
    public static Integer getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Lấy giờ
     *
     * @param date date
     * @return data
     */
    public static int getHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Lấy phút
     *
     * @param date date
     * @return data
     */
    public static int getMinute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }


    /**
     * Convert string to date with any format
     *
     * @param strDate the string date input
     * @param format  date format
     * @return date object
     */
    public static Date toDate(String strDate, String format) {
        Date result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            result = sdf.parse(strDate);
        } catch (ParseException e) {
            log.error("" + e);
        }
        return result;
    }

    /**
     * Convert date to date with any format
     *
     * @param date   the string date input
     * @param format date format
     * @return date object
     */
    public static Date toDate(Date date, String format) {
        return toDate(toString(date, format), format);
    }

    /**
     * Convert Date to String
     *
     * @param date   the date input
     * @param format the format to convert
     * @return String
     */
    public static String toString(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Get time seconds
     *
     * @param date the date input
     * @return long
     */
    public static long getTimeSeconds(Date date) {
        return date.getTime() / 1000;
    }

    /**
     * Lấy list tháng
     *
     * @param startDate startDate
     * @param endDate   endDate
     * @return data
     */
    public static List<String> getListMonth(Date startDate, Date endDate) {
        List<String> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(DateUtil.toDate(startDate, DateUtil.FORMAT_YEAR_MONTH));

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(DateUtil.toDate(endDate, DateUtil.FORMAT_YEAR_MONTH));

        while (calendar.compareTo(endCalendar) <= 0) {
            Date result = calendar.getTime();
            datesInRange.add(DateUtil.toString(result, DateUtil.FORMAT_YEAR_MONTH));
            calendar.add(Calendar.MONTH, 1);
        }
        return datesInRange;
    }

    /**
     * remove second
     *
     * @param date date format
     * @return date object
     */
    public static Date removeSecond(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Boolean compareStartDateEndDate(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            return true;
        }
        return false;
    }
    public static Date getNextYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, 1);
        return getEndDate(calendar.getTime());
    }

}
