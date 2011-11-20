package org.mogaroo.myuw.api;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.mogaroo.myuw.api.adapters.CourseSectionAdapter;
import org.mogaroo.myuw.api.model.Course;
import org.mogaroo.myuw.api.model.CourseIdentifier;
import org.mogaroo.myuw.api.model.CourseSection;
import org.mogaroo.myuw.api.model.Department;
import org.mogaroo.myuw.api.model.ScheduleLineNumber;
import org.mogaroo.myuw.api.model.SectionLabel;
import org.xml.sax.SAXException;

public class MyUWServiceImpl implements MyUWService {
	
	private static final String HTTPS = "https://";
	private static final String WS_VERSION = "v4";
	private static final String WS_CONTEXT = "/student/"+WS_VERSION+"/public/";
	private static final String CONTENT_TYPE_SUFFIX = ".xml";
	
	private String _wsBaseUrl;
	
	private String _studentWebServiceHost;
	private String _webLoginHost;
	private CourseRegistrationService _registrationService;

	private HttpClient _httpClient;

	public MyUWServiceImpl(String studentWebServiceHost, String webLoginHost) {
		_studentWebServiceHost = studentWebServiceHost;
		_webLoginHost = webLoginHost;
		_registrationService = new CourseRegistrationServiceImpl();
		
		_wsBaseUrl = HTTPS + _studentWebServiceHost + WS_CONTEXT;
		
		@SuppressWarnings("deprecation")
		Protocol myhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);
		_httpClient = new HttpClient();
		
	}

	public AuthenticationResult authenticate(MyUWCredentials credentials) throws MyUWServiceException {
		// TODO: Provide authentication.
		return AuthenticationResult.SUCCESS;
	}

	public AuthenticationResult login(MyUWCredentials credentials) throws MyUWServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Course getCourse(CourseIdentifier courseId, Quarter quarter) throws MyUWServiceException {
		// TODO Auto-generated method stub
		return null;
	}

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

	public List<Course> getCourses(Department deptartment, Quarter quarter) throws MyUWServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Department> getDepartments() throws MyUWServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CourseSection> getSections(CourseIdentifier courseId, Quarter quarter) throws MyUWServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public RegistrationResult register(CourseSection courseSection, Quarter quarter) throws MyUWServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public RegistrationResult register(ScheduleLineNumber sln, Quarter quarter) throws MyUWServiceException {
		// TODO Auto-generated method stub
		return null;
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
	
	private TrustManager getEasyTrustManager() {
		return new X509TrustManager() {

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
	}

}
