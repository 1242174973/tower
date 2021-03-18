package com.tower.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/18 15:35
 */
public class DateUtils {
    public static String getDate(int day) // //获取前后日期 i为正数 向后推迟i天，负数时向前提前i天
    {
        Calendar cd = Calendar.getInstance();
        cd.add(Calendar.DATE, day);
        Date dat = cd.getTime();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
        return dformat.format(dat);
    }

    public static void main(String[] args) {
        System.out.println(getDate(-3));
    }
}
