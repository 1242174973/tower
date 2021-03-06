package com.tower.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author xxxx
 * @date2021/3/18 15:35
 */
public class DateUtils {
    public static String getDate(int day) { // //获取前后日期 i为正数 向后推迟i天，负数时向前提前i天
        Calendar cd = Calendar.getInstance();
        cd.add(Calendar.DATE, day);
        Date dat = cd.getTime();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
        return dformat.format(dat);
    }

    public static String getNowDate() {
        LocalDateTime now = LocalDateTime.now();
        StringBuilder sb = new StringBuilder();
        sb.append(now.getYear())
                .append(now.getMonthValue() < 10 ? "0" + now.getMonthValue() : now.getMonthValue())
                .append(now.getDayOfMonth() < 10 ? "0" + now.getDayOfMonth() : now.getDayOfMonth())
                .append(now.getHour() < 10 ? "0" + now.getHour() : now.getHour())
                .append(now.getMinute() < 10 ? "0" + now.getMinute() : now.getMinute())
                .append(now.getSecond() < 10 ? "0" + now.getSecond() : now.getSecond());
        return sb.toString();
    }

    public static String getYearAndMonthAndDay() {
        LocalDateTime now = LocalDateTime.now();
        StringBuilder sb = new StringBuilder();
        sb.append(now.getYear())
                .append(now.getMonthValue() < 10 ? "0" + now.getMonthValue() : now.getMonthValue())
                .append(now.getDayOfMonth() < 10 ? "0" + now.getDayOfMonth() : now.getDayOfMonth());
        return sb.toString();
    }

    public static long byDayTime(int day) {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.plusDays(day);
        LocalDateTime dateTime = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 0, 0, 0);
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime byDayLocalDateTime(int day) {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.plusDays(day);
        return LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 0, 0, 0);
    }

    public static boolean isBeforeDay(int day, long timestamp) {
        return timestamp < byDayTime(day);
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        now = now.plusMonths(-1);
        System.out.println(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    /**
     * 获得上上周期
     *
     * @return 上上周期时间
     */
    public static String getLastPeriod() {
        return getLastPeriod(LocalDateTime.now());
    }

    /**
     * 获得上周期
     *
     * @return 上周期时间
     */
    public static String getPeriod() {
        return getPeriod(LocalDateTime.now());
    }

    public static String getPeriod(LocalDateTime now) {
        int ten = 10;
        StringJoiner sj = new StringJoiner("-");
        sj.add(String.valueOf(now.getYear()));
        sj.add(now.getMonthValue() < ten ? "0" + now.getMonthValue() : String.valueOf(now.getMonthValue()));
        if (now.getDayOfMonth() <= ten) {
            sj.add("01");
        } else if (now.getDayOfMonth() <= 20) {
            sj.add("11");
        } else {
            sj.add("21");
        }
        return sj.toString();
    }

    public static String getLastPeriod(LocalDateTime now) {
        String day;
        String month = now.getMonthValue() < 10 ? "0" + now.getMonthValue() : String.valueOf(now.getMonthValue());
        if (now.getDayOfMonth() - 10 <= 10 && now.getDayOfMonth() - 10 > 0) {
            day = "01";
        } else if (now.getDayOfMonth() - 10 <= 20 && now.getDayOfMonth() - 10 > 0) {
            day = "11";
        } else {
            day = "21";
            month = now.getMonthValue() - 1 < 10 ? "0" + (now.getMonthValue() - 1) : String.valueOf(now.getMonthValue() - 1);
        }
        StringJoiner sj = new StringJoiner("-");
        sj.add(String.valueOf(now.getYear()));
        sj.add(month).add(day);
        return sj.toString();
    }

    public static boolean isDay(int day) {
        LocalDateTime now = LocalDateTime.now();
        int dayOfMonth = now.getDayOfMonth();
        return day == dayOfMonth;
    }

    public static String getLastMonthDay() {
        LocalDateTime now = LocalDateTime.now();
        now = now.plusMonths(-1);
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
