package org.mogaroo.myuw.api;

import java.util.List;

import org.mogaroo.myuw.api.model.Course;
import org.mogaroo.myuw.api.model.CourseIdentifier;
import org.mogaroo.myuw.api.model.CourseSection;
import org.mogaroo.myuw.api.model.Department;
import org.mogaroo.myuw.api.model.ScheduleLineNumber;
import org.mogaroo.myuw.api.model.SectionLabel;

/**
 * Provides interaction with the MyUW web service.
 * @author Sean
 *
 */
public interface MyUWService {
	
	/**
	 * Validate credentials against the MyUW service.
	 * This only validates whether the credentials are authenticated, it
	 * does not actually login and store a cookie.
	 * @param credentials The credentials to validate.
	 * @return The authentication result.
	 */
	public AuthenticationResult authenticate(MyUWCredentials credentials) throws MyUWServiceException;
	
	/**
	 * Login to the MyUWService. This will store the MyUW cookie.
	 * @param credentials The credentials to login with.
	 * @return The AuthenticationResult.
	 */
	public AuthenticationResult login(MyUWCredentials credentials) throws MyUWServiceException;
	
	/** Verify that a user is logged in. This method should be called before registering. 
	 * @return True if the user is logged in. **/
	public boolean isLoggedIn();
	
	/**
	 * Lookup a course by a course identifier.
	 * @param courseId The course identifier.
	 * @return The course, or null if not found.
	 */
	public Course getCourse(CourseIdentifier courseId, Quarter quarter) throws MyUWServiceException;
	
	/**
	 * Lookup a course section by a course identifie and section label.
	 * @param courseId The course identifier.
	 * @param label The section label.
	 * @return The course section, or null if not found.
	 */
	public CourseSection getCourseSection(CourseIdentifier courseId, SectionLabel label, Quarter quarter) throws MyUWServiceException;
	
	/**
	 * Get a list of courses within a department.
	 * @param deptartment The department to search.
	 * @return The list of courses. May not be null, but may be empty.
	 */
	public List<Course> getCourses(Department deptartment, Quarter quarter) throws MyUWServiceException;
	
	/**
	 * Get a list of valid departments recognized by the MyUW service.
	 * @return A list of valid departments recognized by the MyUW service. May not be null.
	 */
	public List<Department> getDepartments() throws MyUWServiceException;
	
	/**
	 * Get a list of sections for a given course.
	 * @param courseId The course id to lookup.
	 * @return The course sections. May not be null.
	 */
	public List<CourseSection> getSections(CourseIdentifier courseId, Quarter quarter) throws MyUWServiceException;
	
	/**
	 * Register the logged in user for the given course.
	 * @param courseSection The course section to register for.
	 * @return The registration result.
	 */
	public RegistrationResult register(CourseSection courseSection, Quarter quarter) throws MyUWServiceException;
	
	/**
	 * Register the logged in user for the given course.
	 * @param sln The SLN of the course.
	 * @return The registration result.
	 */
	public RegistrationResult registerBySln(ScheduleLineNumber sln, Quarter quarter) throws MyUWServiceException;
	
	
	/**
	 * Retrieves the current list of courses the user is registered for.
	 * @return List of courses the user is registered for.
	 */
	public List<ScheduleLineNumber> getRegisteredCourses() throws MyUWServiceException;
}
