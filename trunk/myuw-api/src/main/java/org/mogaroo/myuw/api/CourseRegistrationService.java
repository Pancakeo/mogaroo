package org.mogaroo.myuw.api;

import org.mogaroo.myuw.api.model.CourseIdentifier;
import org.mogaroo.myuw.api.model.ScheduleLineNumber;

public interface CourseRegistrationService {
	
	public AuthenticationResult authenticate(MyUWCredentials credentials);
	public AuthenticationResult login(MyUWCredentials credentuals);
	
	public RegistrationResult register(CourseIdentifier courseId);
	public RegistrationResult register(ScheduleLineNumber sln);
}
