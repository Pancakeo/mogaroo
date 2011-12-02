package org.mogaroo.myuw.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
		// TODO
		throw new RuntimeException("Method not supported.");
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
		// TODO
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public List<Department> getDepartments() throws MyUWServiceException {
		// TODO
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public List<CourseSection> getSections(CourseIdentifier courseId, Quarter quarter) throws MyUWServiceException {
		// TODO
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public RegistrationResult register(CourseSection courseSection, Quarter quarter) throws MyUWServiceException {

		if (_userCredentials == null) {
			return RegistrationResult.failure(FailureReason.USER_NOT_LOGGED_IN);
		}

		return registerBySln(courseSection.getSln(), quarter);
	}
	
	@Override
	public RegistrationResult registerCourses(
			Set<CourseSection> courseSections, Quarter quarter)
			throws MyUWServiceException {
		
		// TODO
		throw new RuntimeException("Method not supported.");
	}

	@Override
	public RegistrationResult registerBySln(ScheduleLineNumber sln, Quarter quarter) throws MyUWServiceException {
		Set<ScheduleLineNumber> slns = new HashSet<ScheduleLineNumber>();
		slns.add(sln);
		return registerBySlns(slns, quarter);
	}
	
	@Override
	public RegistrationResult registerBySlns(Set<ScheduleLineNumber> slns,
			Quarter quarter) throws MyUWServiceException {
		
		if (_userCredentials == null) {
			return RegistrationResult.failure(FailureReason.USER_NOT_LOGGED_IN);
		}

		try {
			doLoginRequest(_userCredentials, true);
			return registerForClasses(slns, quarter);
		}
		catch (Exception e) {
			throw new MyUWServiceException("Register threw exception: " + e.getMessage(), e);
		}
	}

	@Override
	public RegistrationResult dropBySln(ScheduleLineNumber sln, Quarter quarter)
	throws MyUWServiceException {

		Set<ScheduleLineNumber> slns = new HashSet<ScheduleLineNumber>();
		slns.add(sln);
		
		return dropBySlns(slns, quarter);
	}
	
	@Override
	public RegistrationResult dropBySlns(Set<ScheduleLineNumber> slns,
			Quarter quarter) throws MyUWServiceException {

		if (_userCredentials == null) {
			return RegistrationResult.failure(FailureReason.USER_NOT_LOGGED_IN);
		}

		try {
			doLoginRequest(_userCredentials, true);
			return dropClasses(slns, quarter);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new MyUWServiceException("Drop threw exception: " + e.getMessage(), e);
		}

	}

	@Override
	public Set<ScheduleLineNumber> getRegisteredCourses(Quarter quarter) throws MyUWServiceException {
		Set<ScheduleLineNumber> courses = new HashSet<ScheduleLineNumber>();

		if (_userCredentials == null) {
			throw new IllegalStateException("User is not logged in.");
		}

		try {
			doLoginRequest(_userCredentials, true);
			courses.addAll(fetchRegisteredCourses(quarter));
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
	private Set<ScheduleLineNumber> fetchRegisteredCourses(Quarter quarter) throws HttpException, IOException, SAXException, ParserConfigurationException {
		// /students/uwnetid/register.asp
		Set<ScheduleLineNumber> courses = new HashSet<ScheduleLineNumber>();

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

			method.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);

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
						method = new PostMethod(location.getValue()); 
						// Set quarter context
						String qtrInfo = "" + quarter.getSeason().getSeasonNumber() + quarter.getYear();
						((PostMethod)method).addParameter(new NameValuePair("QYYYY", qtrInfo));
						((PostMethod)method).addParameter(new NameValuePair("INPUTFORM", "QTRCHG"));
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
						
						// The 'Display Textbooks' link on the registration page has all the SLN the user is registered for.
						Node n = XmlUtilities.getNode(doc, "//a[contains(text(), 'Display Textbooks')]");
						String hrefLink = XmlUtilities.getAttributeFromNode(n, "href");
						
						String[] parts = hrefLink.split("&");

						for (String part : parts) {
							Pattern p = Pattern.compile(".*sln[0-9]=([0-9]+).*", Pattern.CASE_INSENSITIVE);
							Matcher m = p.matcher(part);
																										
							if (m.matches() && m.groupCount() == 1) {
								courses.add(new ScheduleLineNumber(Integer.parseInt(m.group(1))));
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

	private RegistrationResult registerForClasses(Set<ScheduleLineNumber> slns, Quarter quarter) throws IOException, MyUWServiceException {
		// Note: Moved registration example POST key/value pairs to resources.
		// /students/uwnetid/register.asp
		StringBuilder registrationPage = new StringBuilder(HTTPS)
		.append(_registrationHost)
		.append("/students/")
		.append("/uwnetid/")
		.append("/register.asp");

		HttpMethod method = new GetMethod(registrationPage.toString());

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
						method = new PostMethod(location.getValue()); 
						method.setRequestHeader(COOKIE_KEY, _pubCookie);
						method.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
						((PostMethod)method).addParameters(getAddClassPostParams(slns, quarter));

						resultCode = _httpClient.executeMethod(method);
						content = IOUtils.toString(method.getResponseBodyAsStream());
						doc = XmlUtilities.getDocumentFromDirtyString(content);
						updatePublicCookie(method);
						method.releaseConnection();

						if (resultCode != HttpStatus.SC_OK) {
							throw new HttpException("Add class registration page request did not return HTTP 200. "
									+ "Result code: " + resultCode);
						}
						else {
							method = new GetMethod(registrationPage.toString());
							method.setRequestHeader(COOKIE_KEY, _pubCookie);
							method.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
							resultCode = _httpClient.executeMethod(method);
							content = IOUtils.toString(method.getResponseBodyAsStream());
							doc = XmlUtilities.getDocumentFromDirtyString(content);
							method.releaseConnection();

							if (resultCode != HttpStatus.SC_OK) {
								throw new HttpException("Fetch class registration page request did not return HTTP 200. "
										+ "Result code: " + resultCode);
							}
							else {
								// Fetch the student's classes. Verify the SLN numbers are among them.
								Set<ScheduleLineNumber> courses = getRegisteredCourses(quarter);
								if (courses.containsAll(slns)) {
									return RegistrationResult.successful();
								}
							}
						}
					}
				}
			}
		}

		finally {
			method.releaseConnection();
		}

		// TODO: Attempt to parse out failure reason.
		return RegistrationResult.failure(FailureReason.UNKNOWN);
	}

	private NameValuePair[] getAddClassPostParams(Set<ScheduleLineNumber> slns, Quarter quarter) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		// General:
		nvps.add(new NameValuePair("INPUTFORM", "UPDATE"));
		nvps.add(new NameValuePair("YR", "" + quarter.getYear()));
		nvps.add(new NameValuePair("QTR", "" + quarter.getSeason().getSeasonNumber()));

		// Class we're adding:
		int i = 1;
		for (ScheduleLineNumber s : slns) {
			nvps.add(new NameValuePair("action" + i, "A"));
			nvps.add(new NameValuePair("entcode" + i, ""));
			nvps.add(new NameValuePair("sln" + i, "" + s.getValue()));
			nvps.add(new NameValuePair("dup" + i, ""));
			nvps.add(new NameValuePair("credits" + i, ""));
			i++;
		}

		// Extra params. This basically tells the service we're just sending classes. (0 worked for 1 class...)
		nvps.add(new NameValuePair("maxdrops", "0"));

		return nvps.toArray(new NameValuePair[] {});
	}

	private RegistrationResult dropClasses(Set<ScheduleLineNumber> slns, Quarter quarter) throws IOException, MyUWServiceException {
		// Note: Moved registration example POST key/value pairs to resources.
		// /students/uwnetid/register.asp
		StringBuilder registrationPage = new StringBuilder(HTTPS)
		.append(_registrationHost)
		.append("/students/")
		.append("/uwnetid/")
		.append("/register.asp");

		HttpMethod method = new GetMethod(registrationPage.toString());

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
						// We need to view the page first, to determine the 'dup' value.
						method = new GetMethod(location.getValue());
						method.setRequestHeader(COOKIE_KEY, _pubCookie);
						method.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
						resultCode = _httpClient.executeMethod(method);
						content = IOUtils.toString(method.getResponseBodyAsStream());
						doc = XmlUtilities.getDocumentFromDirtyString(content);
						updatePublicCookie(method);
						method.releaseConnection();

						if (resultCode != HttpStatus.SC_OK) {
							throw new HttpException("Get 'dup' step: Result code is not 200."
									+ "Result code: " + resultCode);
						}
						else {
							Map<ScheduleLineNumber, String> dupValues = new HashMap<ScheduleLineNumber, String>();
							
							// Attempt to grab 'dup' value for each class.
							for (ScheduleLineNumber s : slns) {
								node = XmlUtilities.getNode(doc, "//input[@type='HIDDEN' and @value='" + s.getValue() + "']");
								String index = XmlUtilities.getAttributeFromNode(node, "name").replace("sln", "");
								
								// Get the dup node
								node = XmlUtilities.getNode(doc, "//input[@type='HIDDEN' and @name='dup" + index + "']");
								String dupValue = XmlUtilities.getAttributeFromNode(node, "value");
								dupValues.put(s, dupValue);
								
								method = new PostMethod(registrationPage.toString()); 
								method.setRequestHeader(COOKIE_KEY, _pubCookie);
								method.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
							}
							
							((PostMethod)method).addParameters(getDropClassParams(slns, quarter, dupValues));

							resultCode = _httpClient.executeMethod(method);
							content = IOUtils.toString(method.getResponseBodyAsStream());
							doc = XmlUtilities.getDocumentFromDirtyString(content);
							updatePublicCookie(method);
							method.releaseConnection();

							if (resultCode != HttpStatus.SC_OK) {
								throw new HttpException("Add class registration page request did not return HTTP 200. "
										+ "Result code: " + resultCode);
							}
							else {
								method = new GetMethod(registrationPage.toString());
								method.setRequestHeader(COOKIE_KEY, _pubCookie);
								method.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
								resultCode = _httpClient.executeMethod(method);
								content = IOUtils.toString(method.getResponseBodyAsStream());
								doc = XmlUtilities.getDocumentFromDirtyString(content);
								method.releaseConnection();

								if (resultCode != HttpStatus.SC_OK) {
									throw new HttpException("Fetch class registration page request did not return HTTP 200. "
											+ "Result code: " + resultCode);
								}
								else {
									Set<ScheduleLineNumber> courses = getRegisteredCourses(quarter);
									System.out.println(courses);
									
									boolean allCoursesDropped = true;
									for (ScheduleLineNumber s : slns) {
										if (courses.contains(s)) {
											allCoursesDropped = false;
											break;
										}
									}
									
									if (allCoursesDropped) {
										return RegistrationResult.successful();
									}
									else {
										return RegistrationResult.failure(FailureReason.UNKNOWN);
									}
								}
							}
						}
					}
				}
			}
		}

		finally {
			method.releaseConnection();
		}

	}

	private NameValuePair[] getDropClassParams(Set<ScheduleLineNumber> slns, Quarter quarter, 
					Map<ScheduleLineNumber, String> dupValues) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		// General:
		nvps.add(new NameValuePair("INPUTFORM", "UPDATE"));
		nvps.add(new NameValuePair("YR", "" + quarter.getYear()));
		nvps.add(new NameValuePair("QTR", "" + quarter.getSeason().getSeasonNumber()));

		// Classes we're dropping:
		int i = 1;
		for (ScheduleLineNumber s : slns) {
			nvps.add(new NameValuePair("action" + i, "D"));
			nvps.add(new NameValuePair("entcode" + i, ""));
			nvps.add(new NameValuePair("sln" + i, "" + s.getValue()));
			nvps.add(new NameValuePair("dup" + i, dupValues.get(s)));
			nvps.add(new NameValuePair("credits" + i, ""));
			i++;
		}

		// Extra params. This basically tells the service we're sending .size() classes.
		nvps.add(new NameValuePair("maxdrops", slns.size() + ""));

		return nvps.toArray(new NameValuePair[] {});
	}
	
}
