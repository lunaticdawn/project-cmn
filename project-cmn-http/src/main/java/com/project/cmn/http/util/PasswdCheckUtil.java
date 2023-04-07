package com.project.cmn.http.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 비밀번호의 적합여부를 체크하기 위한 유틸
 */

public class PasswdCheckUtil {
    /**
     * 생성자
     */
    private PasswdCheckUtil() {}

    /**
     * 동일한 문자가 n회 이상 반복되는지 체크한다.
     *
     * @param str 체크할 문자열
     * @param n 체크할 반복 횟수
     * @return true: n회 이상 반복, false: n회 미만 반복
     */
    public static boolean isSameCharConsecutivelyRepeated(String str, int n) {
        if (StringUtils.isBlank(str)) {
            return false;
        }

        int count = 1;

        for (int i = 1; i < str.length(); i ++) {
            if (str.charAt(i) == str.charAt(i - 1)) {
                count ++;
            } else {
                count = 1;
            }

            if (count == n) {
                return true;
            }
        }

        return false;
    }

    /**
     * 순차 또는 역순으로 n회 이상 연속되는지 체크한다.
     *
     * @param str 체크할 문자열
     * @param n 체크할 연속 횟수
     * @return true: n회 이상 연속, false: n회 미만 연속
     */
    public static boolean isConsecutive(String str, int n) {
        if (StringUtils.isBlank(str)) {
            return false;
        }

        int forwardCount = 1;
        int backwardCount = 1;

        for (int i = 1; i < str.length(); i ++) {
            // Forward check
            if (str.charAt(i) == str.charAt(i - 1) + 1) {
                forwardCount ++;
            } else {
                forwardCount = 1;
            }

            // Backward check
            if (str.charAt(i) == str.charAt(i - 1) - 1) {
                backwardCount ++;
            } else {
                backwardCount = 1;
            }

            if (forwardCount >= n || backwardCount >= n) {
                return true;
            }
        }

        return false;
    }

    /**
     * 문자, 숫자, 특수문자 중 n 가지가 조합되었는지 검증
     *
     * @param str 체크할 문자열
     * @param n 만족하는 조합 수
     * @return true: 조합 수를 만족, false: 조합 수를 만족하지 않음
     */
    public static boolean checkCombination(String str, int n) {
        if (StringUtils.isBlank(str)) {
            return false;
        }

        Set<Character> charSet = new HashSet<>();

        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                charSet.add('A');
            } else if (Character.isDigit(c)) {
                charSet.add('0');
            } else if (!Character.isLetterOrDigit(c)) {
                charSet.add('*');
            }

            if (charSet.size() == n) {
                return true;
            }
        }

        return false;
    }

    /**
     * 비밀번호 형식 체크
     *
     * @param str 비밀번호
     * @param repeatedCnt 동일한 문자 반복 횟수
     * @param consecutiveCnt 문자 연속 횟수
     * @param combinationCnt 문자 조합 수
     * @return true: 비밀번호로 적합함, false: 비밀번호로 적합하지 않음
     */
    public static boolean checkPasswd(String str, int repeatedCnt, int consecutiveCnt, int combinationCnt) {
        // 동일한 문자가 repeatedCnt 이상 반복됨
        if (isSameCharConsecutivelyRepeated(str, repeatedCnt)) {
            return false;
        }

        // 순차 또는 역순으로 consecutiveCnt 이상 문자가 연속됨
        if (isConsecutive(str, consecutiveCnt)) {
            return false;
        }

        // 문자, 숫자, 특수문자를 combinationCnt 개 이상 조합하지 않았음
        return checkCombination(str, combinationCnt);
    }
}
