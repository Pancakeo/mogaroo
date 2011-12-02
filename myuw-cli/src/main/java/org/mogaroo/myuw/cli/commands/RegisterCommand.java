package org.mogaroo.myuw.cli.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

@CLICommand(name="register", description="Registers for a class. Login must be called first.")
public class RegisterCommand extends Command<MyUWContext> {
	
	@Parameter(names={"-sln", "--schedule-line-number"}, description="SLN number(s).", required=true)
	private List<String> _slnInputList;
	
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
				
				Set<ScheduleLineNumber> slns = new HashSet<ScheduleLineNumber>();
				for (String s : _slnInputList) {
					slns.add(new ScheduleLineNumber(Integer.parseInt(s)));
				}
				
				Quarter quarter = new Quarter(_year, season);
				
				Console.info("Checking to see if user is already enrolled in SLN number(s): " + slns + "...");
				Set<ScheduleLineNumber> courses = uwService.getRegisteredCourses(quarter);
												
				boolean alreadyReged = false;
				for (ScheduleLineNumber sln : courses) {
					if (slns.contains(sln)) {
						Console.error("You're already registered for class " + sln.getValue());
						alreadyReged = true;
					}
				}
				
				if (alreadyReged) {
					return CommandResult.ERROR;
				}
								
				RegistrationResult result = uwService.registerBySlns(slns, quarter);
				
				if (result.isSuccessful()) {
					Console.info("Successfully registered for SLN numbers: " + slns + ".");
					return CommandResult.OK;
				}
				else {
					Console.error("Failed to register. Reason: " + result.getFailureReason());
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
