package org.mogaroo.myuw.cli.commands;

import java.util.Set;

import net.dharwin.common.tools.cli.api.Command;
import net.dharwin.common.tools.cli.api.CommandResult;
import net.dharwin.common.tools.cli.api.annotations.CLICommand;
import net.dharwin.common.tools.cli.api.console.Console;

import org.mogaroo.myuw.api.MyUWServiceException;
import org.mogaroo.myuw.api.Quarter;
import org.mogaroo.myuw.api.Quarter.Season;
import org.mogaroo.myuw.api.model.ScheduleLineNumber;
import org.mogaroo.myuw.cli.MyUWContext;

import com.beust.jcommander.Parameter;

@CLICommand(name="getcourses", description="Find out what you're taking!.")
public class GetRegisteredCoursesCommand extends Command<MyUWContext> {
	@Parameter(names={"-q", "--quarter"}, description="The quarter name (fall/winter/spring/summer.", required=true)
	private String _quarterName;
	
	@Parameter(names={"-y", "--year"}, description="The year of the quarter.", required=true)
	private int _year;
	
	@Override
	protected CommandResult innerExecute(MyUWContext context) {
		try {
			Season season;
			
			try {
				season = Season.valueOf(_quarterName.toUpperCase());
			}
			catch (Exception e) {
				Console.error("Quarter must be fall/winter/spring/summer.");
				return CommandResult.BAD_ARGS;
			}
			
			Set<ScheduleLineNumber> courses = context.getMyUWService().getRegisteredCourses(new Quarter(_year, season));
			Console.info("Here are the SLN numbers for the courses you're registered in! ");
			
			for (ScheduleLineNumber sln : courses) {
				Console.info("\t" + sln.getValue());
			}
		}
		catch (MyUWServiceException e) {
			Console.error("Problem fetching courses. Exception: " + e.getMessage());
		}
		
		return CommandResult.OK;
	}
	
}
