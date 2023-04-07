package com.project.cmn.test.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * tb_menu
 */
@Getter
@Setter
@ToString
public class TbMenuDto {
    /**
     * 메뉴아이디
     */
    @JsonProperty("menu_id")
    private Long menuId;

    /**
     * 상위메뉴아이디
     */
    @JsonProperty("parent_menu_id")
    private Long parentMenuId;

    /**
     * 메뉴명
     */
    @JsonProperty("menu_nm")
    private String menuNm;

    /**
     * 메뉴URL
     */
    @JsonProperty("menu_url")
    private String menuUrl;

    /**
     * 정렬순서
     */
    @JsonProperty("sort_order")
    private Integer sortOrder;

    /**
     * 메모
     */
    @JsonProperty("memo")
    private String memo;

    /**
     * 유효여부
     */
    @JsonProperty("valid_yn")
    private String validYn;

    /**
     * 등록일시
     */
    @JsonProperty("reg_dt")
    private LocalDateTime regDt;

    /**
     * 등록자
     */
    @JsonProperty("reg_id")
    private String regId;

    /**
     * 수정일시
     */
    @JsonProperty("upd_dt")
    private LocalDateTime updDt;

    /**
     * 수정자
     */
    @JsonProperty("upd_id")
    private String updId;

}