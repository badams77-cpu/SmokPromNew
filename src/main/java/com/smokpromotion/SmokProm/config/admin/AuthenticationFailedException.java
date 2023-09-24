package com.smokpromotion.SmokProm.config.admin;

import org.springframework.security.core.AuthenticationException;


public class AuthenticationFailedException extends AuthenticationException {

	private AuthenticationFailureReasonEnum failureReason;

	public AuthenticationFailedException(AuthenticationFailureReasonEnum reason, String msg) {
		super(msg);
		this.failureReason = reason;
	}


	public AuthenticationFailedException(AuthenticationFailureReasonEnum reason, String msg, Throwable t) {
		super(msg, t);
		this.failureReason = reason;
	}

	public AuthenticationFailureReasonEnum getFailureReason() {
		return failureReason;
	}
}
