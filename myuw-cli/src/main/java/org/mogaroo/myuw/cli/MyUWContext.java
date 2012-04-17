package org.mogaroo.myuw.cli;

import java.io.File;

import net.dharwin.common.tools.cli.api.CLIContext;

import org.mogaroo.myuw.api.MyUWService;
import org.mogaroo.myuw.api.MyUWServiceImpl;

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
	
	/** Registration has its own deal! **/
	private static final String DEFAULT_REGISTER_HOST = "sdb.admin.washington.edu";
	private static final String REGISTER_HOST_KEY = "myuw_register_page";
	
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
				this.getString(WEBLOGIN_HOST_KEY, DEFAULT_WEBLOGIN_HOST),
				this.getString(REGISTER_HOST_KEY, DEFAULT_REGISTER_HOST));
	}
	
	public MyUWService getMyUWService() {
		return _myUwService;
	}
	
	// This fixes a null pointer exceptipn with java-cli-api.
	@Override
	protected String getEmbeddedPropertiesFilename() {
	    //return null;
	    return "";
	}
	
	@Override
	protected File getExternalPropertiesFile() {
		return new File("myuw_cli.properties");
	}
	
}
