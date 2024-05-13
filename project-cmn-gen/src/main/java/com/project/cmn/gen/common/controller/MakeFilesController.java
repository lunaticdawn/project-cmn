package com.project.cmn.gen.common.controller;

import com.project.cmn.gen.common.dto.ProjectInfoDto;
import com.project.cmn.gen.common.service.MakeFilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MakeFilesController {
    private final MakeFilesService makeFilesService;

    /**
     * 주어진 테이블을 바탕으로 파일을 생성한다.
     *
     * @param param {@link ProjectInfoDto} 파일 생성에 대한 정보
     */
    @ResponseBody
    @PostMapping("/make/files")
    public void makeFiles(@Validated @RequestBody ProjectInfoDto param) {
        makeFilesService.makeFiles(param);
    }
}
