package com.homecloud.utils;

import com.homecloud.entity.enums.DateTimePatternEnum;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DateUtil {

    private static final ConcurrentMap<String, DateTimeFormatter> formatterMap = new ConcurrentHashMap<>();

    private static DateTimeFormatter getFormatter(String pattern) {
        return formatterMap.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }

    public static String format(Date date, String pattern) {
        LocalDateTime ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return getFormatter(pattern).format(ldt);
    }


    public static Date getAfterDate(int days) {
        LocalDateTime ldt = LocalDateTime.now().plusDays(days);
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        System.out.println(format(getAfterDate(1), DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
