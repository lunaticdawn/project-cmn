package com.project.cmn.mybatis.sample.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * mysql.tb_user_info
 */
@Getter
@Setter
@ToString
public class TbUserInfoDto {
    /**
     * 사용자 아이디
     */
    @JsonProperty("user_id")
    private String userId;

    /**
     * 사용자 비밀번호
     */
    @JsonProperty("user_pwd")
    private String userPwd;

    /**
     * 사용자 이름
     */
    @JsonProperty("user_nm")
    private String userNm;

    /**
     * 등록일시
     */
    @JsonProperty("reg_time")
    private LocalDateTime regTime;

    /**
     * 등록자 아이디
     */
    @JsonProperty("reg_id")
    private String regId;

    /**
     * 수정일시
     */
    @JsonProperty("mod_time")
    private LocalDateTime modTime;

    /**
     * 수정자 아이디
     */
    @JsonProperty("mod_id")
    private String modId;

}