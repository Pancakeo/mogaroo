package org.mogaroo.myuw.cli;

import com.ballew.tools.cli.api.CLIContext;
import com.ballew.tools.cli.api.CommandLineApplication;
import com.ballew.tools.cli.api.annotations.CLIEntry;
import com.ballew.tools.cli.api.exceptions.CLIInitException;

@CLIEntry
public class MyUWCLI extends CommandLineApplication<MyUWContext> {

	public MyUWCLI() throws CLIInitException {
		super();
	}
	
	@Override
	public CLIContext createContext() {
		return new MyUWContext(this);
	}

	@Override
	protected void shutdown() {
		
	}

}
