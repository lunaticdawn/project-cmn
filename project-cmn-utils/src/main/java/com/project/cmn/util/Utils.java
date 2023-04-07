package com.project.cmn.util;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Utils {
    private static String defaultDateFormat = "yyyy-MM-dd";

    private Utils() {
    }

    public static void setDefaultDateFormat(@NonNull String defaultDateFormat) {
        Utils.defaultDateFormat = defaultDateFormat;
    }

    /**
     * 두 날짜의 차이를 구한다.(단위: 일)
     *
     * @param startDate 시작일
     * @param endDate   종료일
     * @return 두 날짜의 차이(단위: 일)
     */
    public static long getDaysBetween(@NonNull LocalDate startDate, @NonNull LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 두 날짜의 차이를 구한다.(단위: 일)
     *
     * @param startDateStr 시작일 문자열
     * @param endDateStr   종료일 문자열
     * @param dateFormat   날짜 문자열의 형식
     * @return 두 날짜의 차이(단위: 일)
     */
    public static long getDaysBetween(@NonNull String startDateStr, @NonNull String endDateStr, String dateFormat) {
        if (StringUtils.isBlank(dateFormat)) {
            dateFormat = defaultDateFormat;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
        LocalDate startDate = LocalDate.parse(startDateStr, dtf);
        LocalDate endDate = LocalDate.parse(endDateStr, dtf);

        return getDaysBetween(startDate, endDate);
    }

    /**
     * 두 날짜의 차이를 구한다.(단위: 일)
     *
     * @param startDateStr 시작일 문자열
     * @param endDateStr   종료일 문자열
     * @return 두 날짜의 차이(단위: 일)
     */
    public static long getDaysBetween(@NonNull String startDateStr, @NonNull String endDateStr) {
        return getDaysBetween(startDateStr, endDateStr, defaultDateFormat);
    }
}
