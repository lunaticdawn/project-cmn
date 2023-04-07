package com.project.cmn.util;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 현재 Host의 주소를 가져온다.
 * enum 클래스를 이용한 singleton
 */
@Getter
public enum HostInfoUtils {
    INSTANCE;

    /**
     * 호스트 이름
     */
    private String hostName;

    /**
     * 호스트 주소
     */
    private String hostAddr;

    /**
     * 생성자.
     * 생성자를 private 으로 숨기고 enum 을 통해 가져오도록 함. (enum 을 이용한 singleton)
     */
    HostInfoUtils() {
        Logger log = LoggerFactory.getLogger(HostInfoUtils.class);

        try {
            hostName = InetAddress.getLocalHost().getHostName();
            hostAddr = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn(e.getLocalizedMessage());
        }
    }
}
