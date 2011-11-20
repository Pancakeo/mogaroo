package org.mogaroo.myuw.cli.commands;

import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mogaroo.myuw.api.MyUWService;
import org.mogaroo.myuw.api.MyUWServiceException;
import org.mogaroo.myuw.api.Quarter;
import org.mogaroo.myuw.api.Quarter.Season;
import org.mogaroo.myuw.api.model.CourseIdentifier;
import org.mogaroo.myuw.api.model.CourseLevel;
import org.mogaroo.myuw.api.model.CourseSection;
import org.mogaroo.myuw.api.model.Department;
import org.mogaroo.myuw.api.model.SectionLabel;
import org.mogaroo.myuw.cli.MyUWContext;

import com.ballew.tools.cli.api.Command;
import com.ballew.tools.cli.api.CommandResult;
import com.ballew.tools.cli.api.annotations.CLICommand;
import com.ballew.tools.cli.api.console.Console;
import com.beust.jcommander.Parameter;

@CLICommand(name="monitorsection", description="Monitor a course section's enrollment.")
public class MonitorSectionCommand extends Command<MyUWContext> {
	
	private static final SimpleDateFormat TS_FORMAT = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
	
	@Parameter(names={"-d", "--dept"}, description="The department.", required=true)
	private String _dept;
	
	@Parameter(names={"-l", "--level"}, description="The course level.", required=true)
	private int _courseLevel;
	
	@Parameter(names={"-s", "--section"}, description="The section label.", required=true)
	private String _sectionLabel;
	
	@Parameter(names={"-q", "--quarter"}, description="The quarter name (fall/winter/spring/summer.", required=true)
	private String _quarterName;
	
	@Parameter(names={"-y", "--year"}, description="The year of the quarter.", required=true)
	private int _year;
	
	@Override
	protected CommandResult innerExecute(MyUWContext context) {
		Season season;
		try {
			season = Season.valueOf(_quarterName.toUpperCase());
		}
		catch (Exception e) {
			Console.error("Quarter must be fall/winter/spring/summer.");
			return CommandResult.BAD_ARGS;
		}
		
		CourseIdentifier courseId = new CourseIdentifier(
				new Department(null, _dept), new CourseLevel(_courseLevel));
		SectionLabel label = new SectionLabel(_sectionLabel);
		Quarter quarter = new Quarter(_year, season);
		
		int sleepTimeSeconds = context.getInteger("monitor_sleeptime", 120);
		
		monitor(sleepTimeSeconds, courseId, label, quarter, context.getMyUWService());
		
		return CommandResult.OK;
	}
	
	private void monitor(int sleepTimeSeconds, CourseIdentifier courseId, SectionLabel label,
			Quarter quarter, MyUWService service) {
		
		boolean soundTheAlarms = false;
		while (true) {
			try {
				Console.info(TS_FORMAT.format(new Date()) + ": Checking status for ["+
						courseId.getDepartment().getAbbreviation() +
						" " + courseId.getCourseLevel().getLevel()+"]...");
				CourseSection section = service.getCourseSection(courseId, label, quarter);
				Console.info("Current enrollment: " + section.getCurrentEnrollment());
				Console.info("Enrollment limit: " + section.getEnrollmentLimit());
				if (section.getCurrentEnrollment() < section.getEnrollmentLimit()) {
					soundTheAlarms = true;
					break;
				}
				Console.info("No empty spaces. Sleeping for ["+sleepTimeSeconds+"] seconds...");
				Console.info("-------------------------------------------------------");
			}
			catch (MyUWServiceException e) {
				Console.error("Error in MyUW service: " + e.getMessage());
				e.printStackTrace();
			}
			try {
				Thread.sleep(sleepTimeSeconds*1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (soundTheAlarms) {
			System.out.println("SPACE IS OPEN!");
			while (true) {
				Toolkit.getDefaultToolkit().beep();
				try {
					Thread.sleep(3000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
