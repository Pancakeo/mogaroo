package org.mogaroo.myuw.api;

public class RegistrationResult {
	
	private boolean _successful;
	private FailureReason _failureReason;
	
	private RegistrationResult(boolean successful, FailureReason reason) {
		_successful = successful;
		_failureReason = reason;
	}
	
	public boolean isSuccessful() {
		return _successful;
	}

	public FailureReason getFailureReason() {
		return _failureReason;
	}

	public static RegistrationResult successful() {
		return new RegistrationResult(true, null);
	}
	
	public static RegistrationResult failure(FailureReason reason) {
		return new RegistrationResult(false, reason);
	}
	
	public enum FailureReason {
		SECTION_FULL,
		COURSE_NOT_FOUND,
		PREREQS_NOT_SATISFIED,
		CONFLICTED,
		USER_NOT_LOGGED_IN,
		UNKNOWN;
	}
}
