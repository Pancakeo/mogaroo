package org.mogaroo.myuw.cli.commands;

import org.mogaroo.myuw.api.AuthenticationResult;
import org.mogaroo.myuw.api.MyUWCredentials;
import org.mogaroo.myuw.api.MyUWServiceException;
import org.mogaroo.myuw.cli.MyUWContext;

import com.ballew.tools.cli.api.Command;
import com.ballew.tools.cli.api.CommandResult;
import com.ballew.tools.cli.api.annotations.CLICommand;
import com.ballew.tools.cli.api.console.Console;
import com.beust.jcommander.Parameter;

@CLICommand(name="auth", description="Authorize credentials against the MyUW service.")
public class AuthCommand extends Command<MyUWContext> {
	
	@Parameter(names={"-u", "--username"}, description="The username.", required=true)
	private String _username;
	
	@Parameter(names={"-p", "--password"}, description="The password.", required=true)
	private String _password;
	
	@Override
	protected CommandResult innerExecute(MyUWContext context) {
		Console.info("Authenticating user ["+_username+"]...");
		AuthenticationResult result = null;
		try {
			context.getMyUWService().authenticate(new MyUWCredentials(_username, _password));
		}
		catch (MyUWServiceException e) {
			Console.error("Error communicating with the MyUW service: " + e.getMessage());
			return CommandResult.ERROR;
		}
		
		if (result == AuthenticationResult.SUCCESS) {
			Console.info("Credentials are authorized.");
			return CommandResult.OK;
		}
		else {
			Console.info("Credentials are not authorized.");
			return CommandResult.ERROR;
		}
	}
}
