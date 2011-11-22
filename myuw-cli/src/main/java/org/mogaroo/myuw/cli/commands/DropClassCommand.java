package org.mogaroo.myuw.cli.commands;

import java.util.List;

import org.mogaroo.myuw.api.MyUWService;
import org.mogaroo.myuw.api.MyUWServiceException;
import org.mogaroo.myuw.api.Quarter;
import org.mogaroo.myuw.api.Quarter.Season;
import org.mogaroo.myuw.api.RegistrationResult;
import org.mogaroo.myuw.api.model.ScheduleLineNumber;
import org.mogaroo.myuw.cli.MyUWContext;

import com.ballew.tools.cli.api.Command;
import com.ballew.tools.cli.api.CommandResult;
import com.ballew.tools.cli.api.annotations.CLICommand;
import com.ballew.tools.cli.api.console.Console;
import com.beust.jcommander.Parameter;

@CLICommand(name="dropclass", description="Drops a class. Login must be called first.")
public class DropClassCommand extends Command<MyUWContext> {
	
	@Parameter(names={"-sln", "--schedule-line-number"}, description="The username.", required=true)
	private int _sln;
	
	@Parameter(names={"-q", "--quarter"}, description="The quarter name (fall/winter/spring/summer.", required=true)
	private String _quarterName;
	
	@Parameter(names={"-y", "--year"}, description="The year of the quarter.", required=true)
	private int _year;
		
	@Override
	protected CommandResult innerExecute(MyUWContext context) {
		MyUWService uwService = context.getMyUWService();
		
		if (uwService.isLoggedIn()) {
			Console.info("User is logged in.");
			try {
				
				Season season;
				try {
					season = Season.valueOf(_quarterName.toUpperCase());
				}
				catch (Exception e) {
					Console.error("Quarter must be fall/winter/spring/summer.");
					return CommandResult.BAD_ARGS;
				}
				
				Console.info("Checking to see if user is enrolled in '" + _sln + "'...");
				List<ScheduleLineNumber> courseList = uwService.getRegisteredCourses();
								
				boolean foundMatch = false;
				for (ScheduleLineNumber sln : courseList) {
					if (sln.getValue() == _sln) {
						foundMatch = true;
						break;
					}
				}
				
				if (!foundMatch) {
					Console.error("Not registered for class '" + _sln + "'.");
					return CommandResult.BAD_ARGS;
				}
				
				RegistrationResult result = uwService.dropBySln(new ScheduleLineNumber(_sln), new Quarter(_year, season));
				
				if (result.isSuccessful()) {
					Console.info("Successfully dropped SLN # " + _sln + ".");
					return CommandResult.OK;
				}
				else {
					Console.error("Failed to drop class. Reason: " + result.getFailureReason());
					return CommandResult.ERROR;
				}
				
			} catch (MyUWServiceException e) {
				Console.error("Error communicating with the MyUW service: " + e.getMessage());
				return CommandResult.ERROR;
			}
		}
		else {
			Console.error("User is not logged in.");
			return CommandResult.ERROR;
		}
	}
}
