package org.mogaroo.myuw.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.IOUtils;
import org.mogaroo.myuw.api.RegistrationResult.FailureReason;
import org.mogaroo.myuw.api.adapters.CourseSectionAdapter;
import org.mogaroo.myuw.api.model.Course;
import org.mogaroo.myuw.api.model.CourseIdentifier;
import org.mogaroo.myuw.api.model.CourseSection;
import org.mogaroo.myuw.api.model.Department;
import org.mogaroo.myuw.api.model.ScheduleLineNumber;
import org.mogaroo.myuw.api.model.SectionLabel;
import org.mogaroo.myuw.api.utils.XmlUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** See MyUWService interface for documentation. 
 */

public class MyUWServiceImpl implements MyUWService {
	private static final String HTTPS = "https://";
	private static final String WS_VERSION = "v4";
	private static final String WS_CONTEXT = "/student/"+WS_VERSION+"/public/";
	private static final String CONTENT_TYPE_SUFFIX = ".xml";

	private static final String USER_AGENT_KEY = "User-Agent";
	private static final String USER_AGENT_VAL = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.107 Safari/535.1";

	private static final String SET_COOKIE_KEY = "Set-Cookie";
	private static final String COOKIE_KEY = "Cookie";
	private static final String LOCATION_KEY = "location";

	private String _wsBaseUrl;

	private String _studentWebServiceHost;
	private String _webLoginHost;
	private String _registrationHost;
	
	private HttpClient _httpClient;

	/** User credentials. Stored on successful login. **/
	private MyUWCredentials _userCredentials;

	/** Pub cookie. Used for authentication. Updated whenever possible. **/
	private String _pubCookie;
	
	/** Constructs the MyUW service.
	 * @param studentWebServiceHost A REST api provided by the UW. Can be used for polling class info.
	 * @param webLoginHost The more familiar web interface. Used for registration and login. 
	 * @param registrationHost Server hosting registration. **/
	public MyUWServiceImpl(String studentWebServiceHost, String webLoginHost, String registrationHost) {
		_studentWebServiceHost = studentWebServiceHost;
		_webLoginHost = webLoginHost;
		_registrationHost = registrationHost;

		_wsBaseUrl = HTTPS + _studentWebServiceHost + WS_CONTEXT;

		// Override the default SSL protocol. The student web service does not have a valid certificate,
		// so we need to accept HTTPS connections that don't have valid certificates.
		Protocol myhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);

		_httpClient = new HttpClient();
	}

	@Override
	public AuthenticationResult authenticate(MyUWCredentials credentials) throws MyUWServiceException {
		try {
			return doLoginRequest(credentials, false);
		}
		catch (Exception e) {
			throw new MyUWServiceException("Error while authenticating user." + e.getMessage(), e);
		}
	}

	@Override
	public boolean isLoggedIn() {
		return _userCredentials != null;
	}

	@Override
	public AuthenticationResult login(MyUWCredentials credentials) throws MyUWServiceException {

		try {
			AuthenticationResult result = doLoginRequest(credentials, true);

			// If login was successful, store credentials.
			if (result == AuthenticationResult.SUCCESS) {
				_userCredentials = credentials;
			}

			return result;
		}
		catch (Exception e) {
			throw new MyUWServiceException("Error while logging in." + e.getMessage(), e);
		}

	}

	@Override
	public Course getCourse(CourseIdentifier courseId, Quarter quarter) throws MyUWServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CourseSection getCourseSection(CourseIdentifier courseId,
			SectionLabel label, Quarter quarter) throws MyUWServiceException {

		StringBuilder b = new StringBuilder(_wsBaseUrl);
		b.append("course/").append(quarter.getYear()).append(",")
		.append(quarter.getSeason().name());
		b.append(",").append(courseId.getDepartment().getAbbreviation());
		b.append(",").append(courseId.getCourseLevel().getLevel());
		b.append("/").append(label.getLabel());
		b.append(CONTENT_TYPE_SUFFIX);

		MyUWServiceResponse response = null;

		try {
			response = getWebServiceResponse(new GetMethod(b.toString()));
		}
		catch (Exception e) {
			throw new MyUWServiceException("Error getting response from MyUW service." + e.getMessage(), e);
		}

		try {
			return new CourseSectionAdapter().adapt(response.getResponseEntity());
		}
		catch (AdapterException e) {
			throw new MyUWServiceException("Unable to adapt response." + e.getMessage(), e);
		}
	}

	@Override
	public List<Course> getCourses(Department deptartment, Quarter quarter) throws MyUWServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Department> getDepartments() throws MyUWServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CourseSection> getSections(CourseIdentifier courseId, Quarter quarter) throws MyUWServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegistrationResult register(CourseSection courseSection, Quarter quarter) throws MyUWServiceException {

		if (_userCredentials == null) {
			return RegistrationResult.failure(FailureReason.USER_NOT_LOGGED_IN);
		}

		return registerBySln(courseSection.getSln(), quarter);
	}

	@Override
	public RegistrationResult registerBySln(ScheduleLineNumber sln, Quarter quarter) throws MyUWServiceException {

		if (_userCredentials == null) {
			return RegistrationResult.failure(FailureReason.USER_NOT_LOGGED_IN);
		}

		throw new MyUWServiceException("This method isn't quite done yet.", new UnsupportedOperationException());
	}

	@Override
	public List<ScheduleLineNumber> getRegisteredCourses() throws MyUWServiceException {
		List<ScheduleLineNumber> courses = new ArrayList<ScheduleLineNumber>();
		
		if (_userCredentials == null) {
			throw new IllegalStateException("User is not logged in.");
		}
		
		try {
			courses.addAll(fetchRegisteredCourses());
		}
		catch (Exception e) {
			throw new MyUWServiceException("Problem fetching registered courses: " + e.getMessage(), e);
		}
		
		return courses;
	}
	
	private MyUWServiceResponse getWebServiceResponse(HttpMethod method) throws HttpException, IOException, SAXException, ParserConfigurationException {
		int status = _httpClient.executeMethod(method);
		InputStream responseStream = method.getResponseBodyAsStream();

		try {
			return new MyUWServiceResponse(
					status,
					DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(responseStream));
		}
		finally {
			method.releaseConnection();
		}
	}
	
	// Returns a NVP array of items that match hidden form elements on the myUW pages.
	private static NameValuePair[] getHiddenFormElements(Document doc) {
    	List<NameValuePair> elements = new ArrayList<NameValuePair>();
    	
    	// Grab the hidden fields. These are also required for authentication.
		NodeList nodes = XmlUtilities.getNodes(doc, "//input[@type='hidden' and @name and @value]");

		for (int i=0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);	
			String name = n.getAttributes().getNamedItem("name").getTextContent();
			String value = n.getAttributes().getNamedItem("value").getTextContent();

			elements.add(new NameValuePair(name, value));
		}
		
		// The parsing truly is a good time.
		nodes = XmlUtilities.getNodes(doc, "//input[@type='HIDDEN' and @name and @value]");
		
		for (int i=0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);	
			String name = n.getAttributes().getNamedItem("name").getTextContent();
			String value = n.getAttributes().getNamedItem("value").getTextContent();

			elements.add(new NameValuePair(name, value));
		}
    	
    	return elements.toArray(new NameValuePair[] {});
	}
	
	// Logs into mogdub.
	private AuthenticationResult doLoginRequest(MyUWCredentials credentials, boolean storeCookie)
			throws HttpException, IOException, SAXException, ParserConfigurationException {

		GetMethod getLoginPageParamsMethod = new GetMethod(HTTPS + _webLoginHost);
		PostMethod postLoginParamsMethod = new PostMethod(HTTPS + _webLoginHost);

		try {
			getLoginPageParamsMethod.addRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
			int getResultCode = _httpClient.executeMethod(getLoginPageParamsMethod);

			if (getResultCode == HttpStatus.SC_OK) {
				String getResponseContent = IOUtils.toString(getLoginPageParamsMethod.getResponseBodyAsStream());
				Document doc = XmlUtilities.getDocumentFromDirtyString(getResponseContent);

				// Add username and password.
				postLoginParamsMethod.addRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
				
				postLoginParamsMethod.addParameter("user", credentials.getUsername());
				postLoginParamsMethod.addParameter("pass", credentials.getPassword());
				postLoginParamsMethod.addParameters(getHiddenFormElements(doc));
				
				int postResultCode = _httpClient.executeMethod(postLoginParamsMethod);
				
				// Validate login. String matching!
				if (postResultCode == HttpStatus.SC_OK) {
					String postResponseContent = IOUtils.toString(postLoginParamsMethod.getResponseBodyAsStream()); 

					Pattern failurePattern = Pattern.compile(".*<font.*>Login failed\\.\\s+Please re-enter\\.</font>.*",
							Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

					Pattern successPattern = Pattern.compile(".*<font.*>Log in successful - welcome back <b>(.*)</b></font>.*",
							Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

					Matcher failMatch = failurePattern.matcher(postResponseContent);
					Matcher successMatch = successPattern.matcher(postResponseContent);

					if (failMatch.matches()) {
						return AuthenticationResult.FAILURE;
					}
					else if (successMatch.matches() && successMatch.groupCount() == 1) {
						String uName = successMatch.group(1);
						if (credentials.getUsername().equals(uName)) {
							Header cookie = postLoginParamsMethod.getResponseHeader(SET_COOKIE_KEY);

							// Grab cookie.
							if (storeCookie && cookie != null) {
								_pubCookie = cookie.getValue();
							}

							return AuthenticationResult.SUCCESS;
						}
						else {
							return AuthenticationResult.FAILURE;
						}
					}
					else {
						// Unknown failure cause.
						return AuthenticationResult.FAILURE;
					}

				}
				else {
					throw new IllegalStateException("Error. Post did not return HTTP 200. Post returned: " + postResultCode);
				}
			}
			else {
				throw new IllegalStateException("Error. Get did not return HTTP 200. Get returned: " + getResultCode);
			}

		}
		finally {
			getLoginPageParamsMethod.releaseConnection();
			postLoginParamsMethod.releaseConnection();
		}

	}
	
	// Updates the public cookie.
	private void updatePublicCookie(HttpMethod method) {
		Header cookie = method.getResponseHeader(SET_COOKIE_KEY);
		
		if (cookie != null) {
			_pubCookie = cookie.getValue();
		}
	}

	// Fetches registered classes for a user.
	private List<ScheduleLineNumber> fetchRegisteredCourses() throws HttpException, IOException {
		// /students/uwnetid/register.asp
		List<ScheduleLineNumber> courses = new ArrayList<ScheduleLineNumber>();
		
		StringBuilder sb = new StringBuilder(HTTPS)
							.append(_registrationHost)
							.append("/students/")
							.append("/uwnetid/")
							.append("/register.asp");
		
		HttpMethod method = new GetMethod(sb.toString());

		try {
			if (_pubCookie != null) {
				method.setRequestHeader(COOKIE_KEY, _pubCookie);
			}

			int resultCode = _httpClient.executeMethod(method);
			String content = IOUtils.toString(method.getResponseBodyAsStream());
			method.releaseConnection();
			
			if (resultCode != HttpStatus.SC_OK) {
				throw new HttpException("Initial step. Expected HTTP 200, but received: " + resultCode);
			}
			else {				
				method = new PostMethod(HTTPS + _webLoginHost);
				method.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
				method.setRequestHeader(COOKIE_KEY, _pubCookie);				
				((PostMethod)method).addParameters(getHiddenFormElements(XmlUtilities.getDocumentFromDirtyString(content)));
				
				resultCode = _httpClient.executeMethod(method);
				content = IOUtils.toString(method.getResponseBodyAsStream());
				updatePublicCookie(method);
				
				method.releaseConnection();
				
				if (resultCode != HttpStatus.SC_OK) {
					throw new HttpException("Redirect step. Expected HTTP 200, but received: " + resultCode);
				}
				else {
					Document doc = XmlUtilities.getDocumentFromDirtyString(content);
					Node node = XmlUtilities.getNode(doc, "//form[@action]");
					String actionUrl = XmlUtilities.getAttributeFromNode(node, "action");
					
					method = new PostMethod(actionUrl);
					method.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
					method.setRequestHeader(COOKIE_KEY, _pubCookie);
					((PostMethod)method).addParameters(getHiddenFormElements(doc));
					
					resultCode = _httpClient.executeMethod(method);
					//IOUtils.toString(method.getResponseBodyAsStream());
					updatePublicCookie(method);
					Header location = method.getResponseHeader(LOCATION_KEY);
					method.releaseConnection();
					
					if (resultCode != HttpStatus.SC_MOVED_TEMPORARILY || location == null) {
						throw new HttpException("Final auth step: Result code is not 302 or location header is null. "
									+ "Result code: " + resultCode + ", location: " + location);
					}
					else {
						method = new GetMethod(location.getValue()); 
						method.setRequestHeader(COOKIE_KEY, _pubCookie);
						method.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
		
						resultCode = _httpClient.executeMethod(method);
						content = IOUtils.toString(method.getResponseBodyAsStream());
						doc = XmlUtilities.getDocumentFromDirtyString(content);
						method.releaseConnection();
						
						if (resultCode != HttpStatus.SC_OK) {
							throw new HttpException("Final registration page request did not return HTTP 200. "
										+ "Result code: " + resultCode);
						}
						
						NameValuePair[] nvps = getHiddenFormElements(doc);
						
						for (NameValuePair n : nvps) {
							if (n.getName().matches("sln[0-9]+")) {
								courses.add(new ScheduleLineNumber(Integer.parseInt(n.getValue())));
							}
						}
						
					}
				}
			}
		}

		finally {
			method.releaseConnection();
		}
		
		return courses;
	}

	@SuppressWarnings("unused")
	private RegistrationResult registerForClass(ScheduleLineNumber sln, Quarter quarter) {
		// Note: Moved registration example POST key/value pairs to resources.
		return RegistrationResult.failure(FailureReason.UNKNOWN);
	}
}
