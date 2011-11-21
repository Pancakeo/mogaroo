package org.mogaroo.myuw.api;

import java.io.IOException;
import java.io.InputStream;
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

	private HttpClient _httpClient;
	
	/** User credentials. Set on successful login. **/
	private MyUWCredentials _userCredentials;

	/** Pub cookie. **/
	private String _pubCookie;
	
	public MyUWServiceImpl(String studentWebServiceHost, String webLoginHost) {
		_studentWebServiceHost = studentWebServiceHost;
		_webLoginHost = webLoginHost;
		
		_wsBaseUrl = HTTPS + _studentWebServiceHost + WS_CONTEXT;
		
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
			throw new MyUWServiceException("Error while authenticating user.", e);
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
			else {
				System.out.println("uh oh");
			}
			
			return result;
		}
		catch (Exception e) {
			throw new MyUWServiceException("Error while logging in.", e);
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
			throw new MyUWServiceException("Error getting response from MyUW service.", e);
		}
		
		try {
			return new CourseSectionAdapter().adapt(response.getResponseEntity());
		}
		catch (AdapterException e) {
			throw new MyUWServiceException("Unable to adapt response.", e);
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
		
		// Quarter part might be tricky..
		//INPUTFORM=UPDATE&YR=2012&QTR=1
		//&entcode1=&sln1=15679&dup1=&credits1=&gr_sys1=decnochange
		//&entcode2=&sln2=15685&dup2=A&credits2=&gr_sys2=decnochange
		//&entcode3=&sln3=12358&dup3=&credits3=&gr_sys3=decnochange
		//&action4=A&sln4=13255&entcode4=&credits4=&dup4=+
		//&action5=A&sln5=&entcode5=&credits5=&dup5=+
		//&action6=A&sln6=&entcode6=&credits6=&dup6=+
		//&action7=A&sln7=&entcode7=&credits7=&dup7=+
		//&action8=A&sln8=&entcode8=&credits8=&dup8=+
		//&action9=A&sln9=&entcode9=&credits9=&dup9=+
		//&action10=A&sln10=&entcode10=&credits10=&dup10=+
		//&action11=A&sln11=&entcode11=&credits11=&dup11=+&maxdrops=3&maxadds=8

		/*
		INPUTFORM=UPDATE&YR=2012&QTR=1
		&action1=D&entcode1=&sln1=13255&dup1=B&credits1=&gr_sys1=decnochange
		&entcode2=&sln2=15679&dup2=&credits2=&gr_sys2=decnochange
		&entcode3=&sln3=15685&dup3=A&credits3=&gr_sys3=decnochange
		&entcode4=&sln4=12358&dup4=&credits4=&gr_sys4=decnochange
		&action5=A&sln5=&entcode5=&credits5=&dup5=+
		&action6=A&sln6=&entcode6=&credits6=&dup6=+
		&action7=A&sln7=&entcode7=&credits7=&dup7=+
		&action8=A&sln8=&entcode8=&credits8=&dup8=+
		&action9=A&sln9=&entcode9=&credits9=&dup9=+
		&action10=A&sln10=&entcode10=&credits10=&dup10=+
		&action11=A&sln11=&entcode11=&credits11=&dup11=+
		&action12=A&sln12=&entcode12=&credits12=&dup12=+&maxdrops=4&maxadds=8
		*/
		
		/*
		StringBuffer sb = new StringBuffer();
		sb.append("INPUTFORM=UPDATE");
		sb.append("QTR=" + quarter.getYear());
		sb.append("entcode1=");
		sb.append("dup1=");
		sb.append("credits1=");
		sb.append("gr_sys1=decnochange");
		*/
		
		GetMethod method = new GetMethod("https://sdb.admin.washington.edu/students/uwnetid/register.asp");
		PostMethod relayMethod = new PostMethod("https://weblogin.washington.edu");
		PostMethod anotherRelay = new PostMethod("https://sdb.admin.washington.edu/relay.pubcookie3?appsrvid=sdb.admin.washington.edu");
		GetMethod finalRequest = null;
		
		try {
			//doLoginRequest(_userCredentials, true);
			
			if (_pubCookie != null) {
				System.out.println("Setting cookie to " + _pubCookie);
				method.setRequestHeader(COOKIE_KEY, _pubCookie);
			}
			
			int resultCode = _httpClient.executeMethod(method);
			System.out.println(resultCode);
			String content = IOUtils.toString(method.getResponseBodyAsStream());
			//System.out.println(content);
		
			Document doc = XmlUtilities.getDocumentFromDirtyString(content);
			NodeList nodes = XmlUtilities.getNodes(doc, "//input[@type='hidden']");			
			
			relayMethod.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
			relayMethod.setRequestHeader(COOKIE_KEY, _pubCookie);
			
			for (int i=0; i < nodes.getLength(); i++) {
				Node n = nodes.item(i);
				relayMethod.addParameter(n.getAttributes().getNamedItem("name").getTextContent(),
											n.getAttributes().getNamedItem("value").getTextContent());
			}
			
			resultCode = _httpClient.executeMethod(relayMethod);
			System.out.println(resultCode);
			String relayContent = IOUtils.toString(relayMethod.getResponseBodyAsStream());
			System.out.println(relayContent);
			
			Header cookie = relayMethod.getResponseHeader(SET_COOKIE_KEY);
			if (cookie != null) {
				_pubCookie = cookie.getValue();
				System.out.println("updated pub cookie to: " + _pubCookie);
			}
			
			// Now, we must relay...
			// grab hidden fields... also, new pub cookie.
			
			// TODO: Scope these requests... too easy to make a typo and refer to the wrong variable.
			
			doc = XmlUtilities.getDocumentFromDirtyString(relayContent);
			nodes = XmlUtilities.getNodes(doc, "//input[@type='hidden']");			
			anotherRelay.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
			anotherRelay.setRequestHeader(COOKIE_KEY, _pubCookie);

			for (int i=0; i < nodes.getLength(); i++) {
				Node n = nodes.item(i);
				anotherRelay.addParameter(n.getAttributes().getNamedItem("name").getTextContent(),
											n.getAttributes().getNamedItem("value").getTextContent());
			}

			resultCode = _httpClient.executeMethod(anotherRelay);
			System.out.println(resultCode);
			content = IOUtils.toString(anotherRelay.getResponseBodyAsStream());
			//System.out.println(content);
			
			cookie = anotherRelay.getResponseHeader(SET_COOKIE_KEY);
			if (cookie != null) {
				_pubCookie = cookie.getValue();
				System.out.println("updated pub cookie to: " + _pubCookie);
			}
			
			Header location = anotherRelay.getResponseHeader(LOCATION_KEY);
			
			if (location != null) {
				System.out.println("Found location header: " + location.getValue());
				
				// It's the final countdown...
				finalRequest = new GetMethod(location.getValue());
				finalRequest.setRequestHeader(COOKIE_KEY, _pubCookie);
				finalRequest.setRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
				
				resultCode = _httpClient.executeMethod(finalRequest);
				
				content = IOUtils.toString(finalRequest.getResponseBodyAsStream());
				System.out.println(content);
				
				doc = XmlUtilities.getDocumentFromDirtyString(content);
				Node n = XmlUtilities.getNode(doc, "//title");
				System.out.println(n.getTextContent());
			}
						
			
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new MyUWServiceException("Error while registering.", e);
		}
		finally {
			method.releaseConnection();
			relayMethod.releaseConnection();
			anotherRelay.releaseConnection();
			
			if (finalRequest != null) {
				finalRequest.releaseConnection();
			}
		}
		
		return RegistrationResult.failure(FailureReason.UNKNOWN);
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
		
	// Logs into myuw.
	private AuthenticationResult doLoginRequest(MyUWCredentials credentials, boolean storeCookie)
		throws HttpException, IOException, SAXException, ParserConfigurationException {
		
		GetMethod getStartPageMethod = new GetMethod(HTTPS + _webLoginHost);
		PostMethod postLoginMethod = new PostMethod(HTTPS + _webLoginHost);
		
		try {
			getStartPageMethod.addRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
			int getResultCode = _httpClient.executeMethod(getStartPageMethod);
			
			if (getResultCode == HttpStatus.SC_OK) {
				String getResponseContent = IOUtils.toString(getStartPageMethod.getResponseBodyAsStream());
				Document doc = XmlUtilities.getDocumentFromDirtyString(getResponseContent);
												
				// Add username and password.
				postLoginMethod.addRequestHeader(USER_AGENT_KEY, USER_AGENT_VAL);
				postLoginMethod.addParameter("user", credentials.getUsername());
				postLoginMethod.addParameter("pass", credentials.getPassword());
				
				// Grab the hidden fields. These are also required for authentication.
				NodeList nodes = XmlUtilities.getNodes(doc, "//input[@type='hidden' and @name and @value]");
				
				for (int i=0; i < nodes.getLength(); i++) {
					Node n = nodes.item(i);	
					String name = n.getAttributes().getNamedItem("name").getTextContent();
					String value = n.getAttributes().getNamedItem("value").getTextContent();
					
					postLoginMethod.addParameter(name, value);
				}
				
				int postResultCode = _httpClient.executeMethod(postLoginMethod);
				if (postResultCode == HttpStatus.SC_OK) {
					String postResponseContent = IOUtils.toString(postLoginMethod.getResponseBodyAsStream()); 
										
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
							
							//  Maybe there's a pub cookie!
							Header cookie = postLoginMethod.getResponseHeader(SET_COOKIE_KEY);
							
							if (storeCookie && cookie != null) {
								System.out.println("Cookie acquired: " + cookie.getValue());
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
			getStartPageMethod.releaseConnection();
			postLoginMethod.releaseConnection();
		}
				
	}

}
