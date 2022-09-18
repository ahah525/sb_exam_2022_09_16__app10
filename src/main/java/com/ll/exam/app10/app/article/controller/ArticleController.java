package com.ll.exam.app10.app.article.controller;

import com.ll.exam.app10.app.article.controller.input.ArticleForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.util.Map;

@Controller
@RequestMapping("/article")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {
    // 게시글 작성폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite() {
        return "article/write";
    }

    // 게시글 작성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    @ResponseBody
    public String write(ArticleForm articleForm, MultipartRequest multipartRequest) {
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        log.debug("fileMap : " + fileMap);

        return "작업중";
    }
}
