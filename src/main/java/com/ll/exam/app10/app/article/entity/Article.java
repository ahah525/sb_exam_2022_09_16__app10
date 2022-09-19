package com.ll.exam.app10.app.article.entity;

import com.ll.exam.app10.app.base.entity.BaseEntity;
import com.ll.exam.app10.app.member.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true) // 상속받은 클래스 정보까지 출력
public class Article extends BaseEntity {
    @Column(unique = true)
    private String subject;
    private String content;

    @ManyToOne
    private Member author;
}
