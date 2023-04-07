package com.project.cmn.mybatis.controller;

import com.project.cmn.mybatis.dto.ProjectInfoDto;
import com.project.cmn.mybatis.service.MakeFilesService;
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

    @ResponseBody
    @PostMapping("/make/files")
    public void makeFiles(@Validated @RequestBody ProjectInfoDto param) {
        makeFilesService.makeFiles(param);
    }
}
