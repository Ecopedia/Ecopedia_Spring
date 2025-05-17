package com.ecopedia.server.apiPayload.exception.handler;


import com.ecopedia.server.apiPayload.code.BaseErrorCode;
import com.ecopedia.server.apiPayload.exception.GeneralException;

public class ErrorHandler extends GeneralException {

    public ErrorHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
