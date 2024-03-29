package com.project.cmn.http.accesslog;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 접근로그 정보를 담고 있는 객체
 */
@Getter
@Setter
@ToString
public class AccessLogDto {
    /**
     * 로그키
     */
    private String logKey;

    /**
     * 서버 주소
     */
    private String hostAddr;

    /**
     * 클라이언트 주소
     */
    private String clientAddr;

    /**
     * HTTP Request 메소드
     */
    private String httpMethod;

    /**
     * Request URI
     */
    private String requestUri;

    /**
     * Request Query String
     */
    private String queryString;

    /**
     * Request Parameter
     */
    private String requestParam;

    /**
     * HTTP Request Header 정보
     */
    private Map<String, String> requestHeader;

    /**
     * Request 내용
     */
    private String requestBody;

    /**
     * HTTP 상태 코드
     */
    private int resStatus;

    /**
     * 프로그램 응답코드
     */
    private String resCode;

    /**
     * 프로그램 응답메시지
     */
    private String resMsg;

    /**
     * 결과가 리스트인 경우, 그 갯수
     */
    private Integer resCnt;

    /**
     * HTTP Response Header 정보
     */
    private Map<String, String> responseHeader;

    /**
     * Response 내용
     */
    private String responseBody;

    /**
     * 소요시간
     */
    private long durationTime;

    /**
     * {@link CmnStopWatch}
     */
    private CmnStopWatch stopWatch;
}
