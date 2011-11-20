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

@CLICommand(name="login", description="Logs a user into the MyUW service.")
public class LoginCommand extends Command<MyUWContext> {
	
	@Parameter(names={"-u", "--username"}, description="The username.", required=true)
	private String _username;
	
	@Parameter(names={"-p", "--password"}, description="The password.", required=true)
	private String _password;
	
	@Override
	protected CommandResult innerExecute(MyUWContext context) {
		Console.info("Logging in user ["+_username+"]...");
		AuthenticationResult result = null;
		try {
			context.getMyUWService().authenticate(new MyUWCredentials(_username, _password));
		}
		catch (MyUWServiceException e) {
			Console.error("Error communicating with the MyUW service: " + e.getMessage());
			return CommandResult.ERROR;
		}
		
		if (result == AuthenticationResult.SUCCESS) {
			Console.info("Successfully logged in.");
			return CommandResult.OK;
		}
		else {
			Console.info("Error logging in.");
			return CommandResult.ERROR;
		}
	}
}
