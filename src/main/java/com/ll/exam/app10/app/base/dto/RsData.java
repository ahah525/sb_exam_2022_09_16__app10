package com.ll.exam.app10.app.base.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RsData<T> {
    private final String resultCode;
    private final String msg;
    private final T body;

    // 결과 성공 여부 리턴
    public boolean isSuccess() {
        return resultCode.startsWith("S-");
    }
    // 결과 실패 여부 리턴
    public boolean isFail() {
        return isSuccess() == false;
    }
}
