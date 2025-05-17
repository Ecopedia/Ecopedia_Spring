package com.ecopedia.server.apiPayload.exception;

public abstract class BaseException extends RuntimeException{
	public abstract BaseExceptionType getExceptionType();
}
