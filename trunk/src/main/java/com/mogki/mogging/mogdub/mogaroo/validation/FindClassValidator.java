package com.mogki.mogging.mogdub.mogaroo.validation;

import com.mogki.mogging.mogdub.mogaroo.request.FindClassImpl.FindClassParams;

public class FindClassValidator {

	private FindClassValidator() {
		
	}
	
	public static boolean validateInput(String uName, String pWord, String slnNum, String enrSummUrl) {
		return validateUsername(uName)
			&& validatePassword(pWord)
			&& validateSlnNum(slnNum)
			&& validateEnrollmentSummaryUrl(enrSummUrl);
	}
	
	public static boolean validateUsername(String uName) {
		return uName.length() > 0;
	}
	
	public static boolean validatePassword(String pWord) {
		return pWord.length() > 0;
	}
	
	public static boolean validateSlnNum(String sln) {
		return sln.length() > 0 && sln.matches("\\d+");
	}
	
	public static boolean validateEnrollmentSummaryUrl(String enrSummUrl) {
		return enrSummUrl.length() > 0 && enrSummUrl.matches("^https://.*");
	}
	
	public static boolean validateBundledInput(FindClassParams bundle) {
		return validateUsername(bundle.username)
				&& validatePassword(bundle.password)
				&& validateSlnNum(bundle.slnNum)
				&& validateEnrollmentSummaryUrl(bundle.enrollSumUrl);
	}
}
