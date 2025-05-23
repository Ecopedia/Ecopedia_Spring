package com.ecopedia.server.apiPayload.code;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ReasonDto {

    private HttpStatus httpStatus;

    private final Boolean isSuccess;
    private final String code;
    private final String message;

    public Boolean isSuccess(){
        return isSuccess;
    }

}
