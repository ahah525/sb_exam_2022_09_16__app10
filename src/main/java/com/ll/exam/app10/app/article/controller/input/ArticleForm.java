package com.ll.exam.app10.app.article.controller.input;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ArticleForm {
    @NotEmpty
    private String subject;
    @NotEmpty
    private String content;
    private String hashTagContents;
}
