package com.tower.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 梦-屿-千-寻
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
        long l = byDayTime(0);
        long currentTimeMillis = System.currentTimeMillis();
        System.out.println(l);
        System.out.println(currentTimeMillis);
        System.out.println(currentTimeMillis - l);
    }
}
