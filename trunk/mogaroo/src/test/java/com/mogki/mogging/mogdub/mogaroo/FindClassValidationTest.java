package com.mogki.mogging.mogdub.mogaroo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mogki.mogging.mogdub.mogaroo.request.FindClassImpl.FindClassParams;
import com.mogki.mogging.mogdub.mogaroo.validation.FindClassValidator;

/** Unit testing for the Find Class Validator module. **/

public class FindClassValidationTest {

	@Test
	/** Verify bad input is bad. **/
	public void testInvalidFindClassParams() {
		String uName = "";
		String pWord = "";
		String slnNum = "49ads";
		String enrSummUrl = "asdajhsahjd";
		FindClassParams bundle = new FindClassParams(uName, pWord, slnNum, enrSummUrl);
		
		assertFalse("Empty input, but validated.", FindClassValidator.validateUsername(uName));
		assertFalse("Empty input, but validated", FindClassValidator.validatePassword(pWord));
		assertFalse("Non-numeric SLN, but validated.", FindClassValidator.validateSlnNum(slnNum));
		assertFalse("Doesn't match http://, but validated", FindClassValidator.validateEnrollmentSummaryUrl(enrSummUrl));
		assertFalse("Invalid input, but validated", FindClassValidator.validateInput(uName, pWord, slnNum, enrSummUrl));
		assertFalse("Invalid input, but validated", FindClassValidator.validateBundledInput(bundle));
	}
	
	@Test
	/** Verify good input is good. **/
	public void testValidFindClassParams() {
		String uName = "mogwai";
		String pWord = "twisted_advance";
		String slnNum = "12345";
		String enrSummUrl = "https://.......";
		FindClassParams bundle = new FindClassParams(uName, pWord, slnNum, enrSummUrl);
		
		assertTrue("Valid input, should pass.", FindClassValidator.validateUsername(uName));
		assertTrue("Valid input, should pass.", FindClassValidator.validatePassword(pWord));
		assertTrue("Valid input, should pass.", FindClassValidator.validateSlnNum(slnNum));
		assertTrue("Valid input, should pass.", FindClassValidator.validateEnrollmentSummaryUrl(enrSummUrl));
		assertTrue("Valid input, should pass.", FindClassValidator.validateInput(uName, pWord, slnNum, enrSummUrl));
		assertTrue("Valid input, should pass.", FindClassValidator.validateBundledInput(bundle));
	}
}
