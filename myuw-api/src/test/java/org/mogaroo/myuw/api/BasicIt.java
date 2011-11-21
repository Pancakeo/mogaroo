package org.mogaroo.myuw.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mogaroo.myuw.api.Quarter.Season;
import org.mogaroo.myuw.api.RegistrationResult.FailureReason;
import org.mogaroo.myuw.api.model.CourseIdentifier;
import org.mogaroo.myuw.api.model.CourseLevel;
import org.mogaroo.myuw.api.model.CourseSection;
import org.mogaroo.myuw.api.model.Department;
import org.mogaroo.myuw.api.model.SectionLabel;

/** Integration tests. **/

public class BasicIt {
	private static final String WS_HOST = "ws.admin.washington.edu";
	private static final String WEBLOGIN_HOST = "weblogin.washington.edu";
	private static final String REGISTER_HOST = "sdb.admin.washington.edu";
	
	@Test
	// Note: This may need to be maintained each quarter.
	public void testCourseFetch() throws MyUWServiceException {
		MyUWService uwService = new MyUWServiceImpl(WS_HOST, WEBLOGIN_HOST, REGISTER_HOST);
		CourseIdentifier courseId = new CourseIdentifier(
				new Department(null, "MATH"), new CourseLevel(390));
		SectionLabel label = new SectionLabel("A");
		Quarter quarter = new Quarter(2012, Season.valueOf("WINTER"));

		CourseSection section = uwService.getCourseSection(courseId, label, quarter);
		assertNotNull("Response: section is null.", section);
		assertTrue("Must be at least one possible space", section.getEnrollmentLimit() > 0);
	}
	
	@Test
	// Auth should fail.
	public void testAuthFailure() throws MyUWServiceException {
		MyUWService uwService = new MyUWServiceImpl(WS_HOST, WEBLOGIN_HOST, REGISTER_HOST);
		AuthenticationResult result = uwService.authenticate(new MyUWCredentials("egg", "mog"));
		
		assertNotNull("auth result is null.", result);
		assertEquals("Result is not fail.", AuthenticationResult.FAILURE, result);
	}
	
	@Test
	public void testRegFailure() throws MyUWServiceException {
		MyUWService uwService = new MyUWServiceImpl(WS_HOST, WEBLOGIN_HOST, REGISTER_HOST);
		RegistrationResult result = uwService.registerBySln(null, null);
		
		assertNotNull("Result is null", result);
		assertFalse("Result should fail.", result.isSuccessful());
		assertEquals("Result didn't match expected value.", FailureReason.USER_NOT_LOGGED_IN, result.getFailureReason());
	}
}
