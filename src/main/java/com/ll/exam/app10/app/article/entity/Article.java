package com.ll.exam.app10.app.article.entity;

import com.ll.exam.app10.app.base.entity.BaseEntity;
import com.ll.exam.app10.app.hashTag.entity.HashTag;
import com.ll.exam.app10.app.member.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true) // 상속받은 클래스 정보까지 출력
public class Article extends BaseEntity {
    private String subject;
    private String content;

    @ManyToOne
    private Member author;

    // 해당 게시글의 해시태그 반환
    public String extra_inputValue_hashTagContents() {
        Map<String, Object> extra = getExtra();

        if(!extra.containsKey("hashTags")) {
            return "";
        }

        List<HashTag> hashTags = (List<HashTag>) extra.get("hashTags");

        if(hashTags.isEmpty()) {
            return "";
        }

        // 해시태그 리스트를 한줄로 만들어서 리턴
        return "#" + hashTags
                .stream()
                .map(hashTag -> hashTag.getKeyword().getContent())
                .sorted()
                .collect(Collectors.joining(" #"))
                .trim();
    }
}
