package org.mogaroo.myuw.cli.commands;

import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.mogaroo.myuw.api.RegistrationResult.FailureReason;
import org.mogaroo.myuw.api.model.CourseEntry;
import org.mogaroo.myuw.api.model.CourseIdentifier;
import org.mogaroo.myuw.api.model.CourseLevel;
import org.mogaroo.myuw.api.model.CourseSection;
import org.mogaroo.myuw.api.model.Department;
import org.mogaroo.myuw.api.model.ScheduleLineNumber;
import org.mogaroo.myuw.api.model.SectionLabel;
import org.mogaroo.myuw.cli.MyUWContext;

import com.beust.jcommander.Parameter;

@CLICommand(name="autoregister", description="Register for a class as soon as it becomes available. Can do multiple classes.")
public class AutoRegisterCommand extends Command<MyUWContext> {
	
	private static final SimpleDateFormat TS_FORMAT = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
	
	@Parameter(names={"-e", "--entry"}, description="Entry(s). Must match: Department,Level,Section,Sln. Example: Math,126,A,12345"
		, required=true)
	private List<String> _entries;
		
	@Parameter(names={"-q", "--quarter"}, description="The quarter name (fall/winter/spring/summer.", required=true)
	private String _quarterName;
	
	@Parameter(names={"-y", "--year"}, description="The year of the quarter.", required=true)
	private int _year;
	
	@Parameter(names={"--retry_on_error"}, description="Ignore registration errors and retry.", required=false)
    private boolean _retryOnError;
	
	@Override
	protected CommandResult innerExecute(MyUWContext context) {
	    int sleepTimeSeconds = context.getInteger("monitor_sleeptime", 120);
	    
		if (!context.getMyUWService().isLoggedIn()) {
			Console.error("User is not logged in.");
			return CommandResult.ERROR;
		}
		
		Season season;
		try {
			season = Season.valueOf(_quarterName.toUpperCase());
		}
		catch (Exception e) {
			Console.error("Quarter must be fall/winter/spring/summer.");
			return CommandResult.BAD_ARGS;
		}
		
		List<CourseEntry> courses = new ArrayList<CourseEntry>();
		// Keep list of slns, to see if user is already registered.
		Set<ScheduleLineNumber> slns = new HashSet<ScheduleLineNumber>();
		
		Quarter quarter = new Quarter(_year, season);
				
		for (String e : _entries) {
			String[] parts = e.split("\\s?,\\s?");
			
			if (parts.length == 4) {
				Department dept = new Department(null, parts[0]);
				CourseLevel level = new CourseLevel(Integer.parseInt(parts[1]));
				SectionLabel sectionLabel = new SectionLabel(parts[2]);
				ScheduleLineNumber sln = new ScheduleLineNumber(Integer.parseInt(parts[3]));
				
				slns.add(sln);
				
				CourseIdentifier courseId = new CourseIdentifier(dept, level);
				courses.add(new CourseEntry(courseId, null, null, sectionLabel, sln));
			}
			else {
				Console.error("Unable to parse entry: " + e);
				Console.error("Example of how to input an entry (Department, Level, Section, Sln): -e Math,126,A,12345");
			}

		}
		
		// Verify user is not trying to register for a class he's already in.
		Console.info("Fetching current set of registered courses...");
		try {
			Set<ScheduleLineNumber> registeredCourses = context.getMyUWService().getRegisteredCourses(quarter);
			
			boolean alreadyInCourse = false;
			for (ScheduleLineNumber sln : slns) {
				if (registeredCourses.contains(sln)) {
					Console.error("Already registered for " + sln.getValue());
					alreadyInCourse = true;
				}
			}
			
			if (alreadyInCourse) {
				return CommandResult.ERROR;
			}
			
		}
		catch (MyUWServiceException e) {
			Console.error("Error communicating with the MyUW service: " + e.getMessage());
			
			if (_retryOnError) {
			    sleepAndRetry(context, sleepTimeSeconds);
			}
			else {
			    return CommandResult.ERROR;
			}
		}
		
		// Monitor and register.
		RegistrationResult result = autoRegister(sleepTimeSeconds, courses, quarter, context.getMyUWService());
		
		if (result.isSuccessful()) {
			Console.info("Successfully registered for courses: " + courses);
			return CommandResult.OK;
		}
		else {
		    if (_retryOnError) {
		        return sleepAndRetry(context, sleepTimeSeconds);
            }
		    else {
		        Console.error("Failed to register. Reason: " + result.getFailureReason());
		        return CommandResult.ERROR;
		    }
		}
	}
	
	private CommandResult sleepAndRetry(MyUWContext context, int sleepTimeSeconds) {
	    Console.warn("Registration failed. Retry on error enabled. Sleeping for ["+sleepTimeSeconds+"] seconds...");
        try {
            Thread.sleep(sleepTimeSeconds*1000);
            
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return innerExecute(context);
	}
	
	private RegistrationResult autoRegister(int sleepTimeSeconds, List<CourseEntry> courses, 
			Quarter quarter, MyUWService service) {
		
		boolean allOpen;
		
		while (true) {
			try {
				
				allOpen = true;
				for (CourseEntry c : courses) {
					Console.info(TS_FORMAT.format(new Date()) + ": Checking status for ["+
							c.getCourseId().getDepartment().getAbbreviation() + 
							" " + c.getCourseId().getCourseLevel().getLevel()+"]...");
					
					System.out.println(c);
					CourseSection section = service.getCourseSection(c.getCourseId(), c.getSectionLabel(), quarter);
					Console.info("Current enrollment: " + section.getCurrentEnrollment());
					Console.info("Enrollment limit: " + section.getEnrollmentLimit());
					
					if (section.getCurrentEnrollment() >= section.getEnrollmentLimit()) {
						allOpen = false;
						Console.info("No empty spaces for " + c);
					}
					
					Console.info("----");
				}
				
				if (allOpen) {
					break;
				}
				else {
					 Console.info("At least one course full. Sleeping for ["+sleepTimeSeconds+"] seconds...");
					 Console.info("-------------------------------------------------------");
				}
				
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
		
		if (allOpen) {
			Console.info("Space is open in all classes!");
			Toolkit.getDefaultToolkit().beep();
			
			Set<ScheduleLineNumber> slns = new HashSet<ScheduleLineNumber>();
			for (CourseEntry c : courses) {
				slns.add(c.getSln());
			}
						
			try {
				Console.info("Attempting to register for " + slns);
				return service.registerBySlns(slns, quarter);
			}
			catch (MyUWServiceException e) {
				Console.error("Error in MyUW service: " + e.getMessage());
				e.printStackTrace();
			}
			    			
		}
		
		return RegistrationResult.failure(FailureReason.UNKNOWN);
	}
}
