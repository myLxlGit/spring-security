package com.spring.security.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author lxl
 * @date 2023/9/14 17:16
 */
public class DateUtils {

    private static final String YYYY = "yyyy";
    private static final String YYYY_MM = "yyyy-MM";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final List<String> PARSE_PATTERNS = Arrays.asList(YYYY_MM_DD, YYYY_MM_DD_HH_MM_SS, "yyyy-MM-dd HH:mm", YYYY_MM,
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM");


    /**
     * 获取年，长度为4的字符串
     *
     * @return String
     */
    public static String getYear() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(YYYY));
    }

    /**
     * 获取年月
     * <p>
     * 分隔符，默认为"-"
     *
     * @return String 2020-11（2022[separator]11]）
     */
    public static String getYM() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(getPattern(YYYY_MM, "-")));
    }


    /**
     * 获取年月日
     * <p>
     * separator String 分隔符，默认为"-"
     *
     * @return String 2020-11-01（2022[[separator]]11[[separator]]01）
     */
    public static String getYMD() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(getPattern(YYYY_MM_DD, "-")));
    }

    /**
     * 获取日期时间字符串
     *
     * @return String 2022-11-01 01:01:01
     */
    public static String getDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    }

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }


    /**
     * 将日期格式化位指定格式
     *
     * @param date   Date 日期
     * @param format String 格式字符串 默认为YYYY_MM_DD_HH_MM_SS
     * @return String 格式化的日期字符串
     */
    public static String parseDateToStr(Date date, String format) {
        return DateTimeFormatter.ofPattern(format)
                .format(LocalDateTime.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
    }

    /**
     * 将日期格式化位指定格式
     *
     * @param date Date 日期
     *             默认为YYYY_MM_DD_HH_MM_SS
     * @return String 格式化的日期字符串
     */
    public static String parseDateToStr(Date date) {
        return DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS)
                .format(LocalDateTime.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
    }


    public static String getPattern(String oldPattern, String separator) {
        return Objects.equals(separator, "-") ? oldPattern : oldPattern.replace("-", separator);
    }

    /**
     * 获取指定数量个单位的时间之后的时间，比如三分钟后，十小时后的时间
     *
     * @param amount Long 数量
     * @param unit   ChronoUnit 单位
     * @param date   Date 给定时间，默认当前时间
     * @return Date
     */
    public static Date getAfter(Long amount, ChronoUnit unit, Date date) {
        return getBefore(-amount, unit, date);
    }

    /**
     * 获取指定数量个单位的时间之后的时间，默认当前时间 比如三分钟后，十小时后的时间
     *
     * @param amount Long 数量
     * @param unit   ChronoUnit 单位
     * @return Date
     */
    public static Date getAfter(Long amount, ChronoUnit unit) {
        return getBefore(-amount, unit, new Date());
    }

    /**
     * 获取指定数量个单位的时间之前的时间，比如三分钟前，十小时前的时间
     *
     * @param amount Long 数量
     * @param unit   ChronoUnit 单位
     * @param date   Date 给定时间，默认当前时间
     * @return Date
     */
    public static Date getBefore(Long amount, ChronoUnit unit, Date date) {
        return Date.from(LocalDateTime.from(date.toInstant().atZone(ZoneId.systemDefault())).plus(-amount, unit).atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 日期字符串转化为日期 格式
     *
     * @param dateString 日期型字符串
     * @return Date? 日期，如果无法解析，会返回null
     */
    static Date parseDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

}
