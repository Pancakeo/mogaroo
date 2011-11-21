package org.mogaroo.myuw.cli.commands;

import java.util.List;

import org.mogaroo.myuw.api.MyUWServiceException;
import org.mogaroo.myuw.api.model.ScheduleLineNumber;
import org.mogaroo.myuw.cli.MyUWContext;

import com.ballew.tools.cli.api.Command;
import com.ballew.tools.cli.api.CommandResult;
import com.ballew.tools.cli.api.annotations.CLICommand;
import com.ballew.tools.cli.api.console.Console;

@CLICommand(name="getcourses", description="Find out what you're taking!.")
public class GetRegisteredCoursesCommand extends Command<MyUWContext> {
			
	@Override
	protected CommandResult innerExecute(MyUWContext context) {
		try {
			List<ScheduleLineNumber> courses = context.getMyUWService().getRegisteredCourses();
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
