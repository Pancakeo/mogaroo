package org.mogaroo.myuw.cli.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.dharwin.common.tools.cli.api.Command;
import net.dharwin.common.tools.cli.api.CommandResult;
import net.dharwin.common.tools.cli.api.annotations.CLICommand;
import net.dharwin.common.tools.cli.api.console.Console;

import org.mogaroo.myuw.api.MyUWService;
import org.mogaroo.myuw.api.MyUWServiceException;
import org.mogaroo.myuw.api.Quarter;
import org.mogaroo.myuw.api.Quarter.Season;
import org.mogaroo.myuw.api.RegistrationResult;
import org.mogaroo.myuw.api.model.ScheduleLineNumber;
import org.mogaroo.myuw.cli.MyUWContext;

import com.beust.jcommander.Parameter;

@CLICommand(name="drop", description="Drops a class. Login must be called first.")
public class DropClassCommand extends Command<MyUWContext> {
	
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
				
				Console.info("Checking to see if user is enrolled in SLN numbers " + slns + "...");
				Set<ScheduleLineNumber> courses = uwService.getRegisteredCourses(quarter);
								
				if (!courses.containsAll(slns)) {
					Console.error("Not registered for at least one class in " + slns + ". Registered for: " + courses);
					return CommandResult.BAD_ARGS;
				}
								
				RegistrationResult result = uwService.dropBySlns(slns, new Quarter(_year, season));
				
				if (result.isSuccessful()) {
					Console.info("Successfully dropped SLN numbers " + slns + ".");
					return CommandResult.OK;
				}
				else {
					Console.error("Failed to drop class(es). Reason: " + result.getFailureReason());
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
