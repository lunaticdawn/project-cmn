package com.project.cmn.http;

/**
 * 웹 프로젝트에서 공통으로 사용할 상수를 지정한다.
 */
public class WebCmnConstants {
    /**
     * 생성자
     */
    private WebCmnConstants() {
        throw new IllegalStateException("Constants class");
    }

    /**
     * 로그 키
     */
    public static final String LOG_KEY = "LOG_KEY";

    public enum ResponseKeys {
        TIMESTAMP("timestamp")
        , RES_STATUS("res_status")
        , RES_CODE("res_code")
        , RES_MSG("res_msg")
        , STACK_TRACE("stack_trace")
        , CONSTRAINT_VIOLATION_LIST("constraint_violation_list");
        private final String code;

        public String code() {
            return code;
        }

        ResponseKeys(String code) {
            this.code = code;
        }
    }
}