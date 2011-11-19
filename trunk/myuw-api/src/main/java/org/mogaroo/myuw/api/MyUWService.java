package org.mogaroo.myuw.api;

import java.util.List;

import org.mogaroo.myuw.api.model.Course;
import org.mogaroo.myuw.api.model.CourseIdentifier;
import org.mogaroo.myuw.api.model.CourseSection;
import org.mogaroo.myuw.api.model.Department;

public interface MyUWService {
	
	/**
	 * Validate credentials against the MyUW service.
	 * This only validates whether the credentials are authenticated, it
	 * does not actually login and store a cookie.
	 * @param credentials The credentials to validate.
	 * @return The authentication result.
	 */
	public AuthenticationResult authenticate(MyUWCredentials credentials);
	
	/**
	 * Login to the MyUWService. This will store the MyUW cookie.
	 * @param credentials The credentials to login with.
	 * @return The AuthenticationResult.
	 */
	public AuthenticationResult login(MyUWCredentials credentials);
	
	/**
	 * Lookup a course by a course identifier.
	 * @param courseId The course identifier.
	 * @return The course, or null if not found.
	 */
	public Course getCourse(CourseIdentifier courseId);
	
	/**
	 * Get a list of courses within a department.
	 * @param deptartment The department to search.
	 * @return The list of courses. May not be null, but may be empty.
	 */
	public List<Course> getCourses(Department deptartment);
	
	/**
	 * Get a list of valid departments recognized by the MyUW service.
	 * @return A list of valid departments recognized by the MyUW service. May not be null.
	 */
	public List<Department> getDepartments();
	
	/**
	 * Get a list of sections for a given course.
	 * @param courseId The course id to lookup.
	 * @return The course sections. May not be null.
	 */
	public List<CourseSection> getSections(CourseIdentifier courseId);
	
	/**
	 * Register the logged in user for the given course.
	 * @param courseSection The course section to register for.
	 * @return The registration result.
	 */
	public RegistrationResult register(CourseSection courseSection);
}
