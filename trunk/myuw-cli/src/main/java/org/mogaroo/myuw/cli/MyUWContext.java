package org.mogaroo.myuw.cli;

import java.io.File;

import org.mogaroo.myuw.api.MyUWService;
import org.mogaroo.myuw.api.MyUWServiceImpl;

import com.ballew.tools.cli.api.CLIContext;

/**
 * 
 * @author Sean
 *
 */
public class MyUWContext extends CLIContext {
	
	/** MyUW dev server. **/
	//private static final String DEFAULT_WS_HOST = "ucswseval1.cac.washington.edu";
	/** MyUW prod server. **/
	private static final String DEFAULT_WS_HOST = "ws.admin.washington.edu";
	private static final String STUDENT_WS_HOST_KEY = "myuw_ws_host";
	
	private static final String DEFAULT_WEBLOGIN_HOST = "weblogin.washington.edu";
	private static final String WEBLOGIN_HOST_KEY = "myuw_login_host";
	
	/**
	 * The MyUW service.
	 */
	private MyUWService _myUwService;
	
	/**
	 * Initialize the context. This creates the UserDatastore.
	 * @param app The host application.
	 */
	public MyUWContext(MyUWCLI app) {
		super(app);
		_myUwService = new MyUWServiceImpl(
				this.getString(STUDENT_WS_HOST_KEY, DEFAULT_WS_HOST),
				this.getString(WEBLOGIN_HOST_KEY, DEFAULT_WEBLOGIN_HOST));
	}
	
	public MyUWService getMyUWService() {
		return _myUwService;
	}
	
	@Override
	protected File getExternalPropertiesFile() {
		return new File("myuw_cli.properties");
	}
	
}
