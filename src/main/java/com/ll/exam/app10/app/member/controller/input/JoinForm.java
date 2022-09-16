package com.ll.exam.app10.app.member.controller.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JoinForm {
    private String username;
    private String password;
    private String email;
    private MultipartFile profileImg;
}
