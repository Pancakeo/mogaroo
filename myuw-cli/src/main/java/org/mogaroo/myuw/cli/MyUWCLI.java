package org.mogaroo.myuw.cli;

import net.dharwin.common.tools.cli.api.CLIContext;
import net.dharwin.common.tools.cli.api.CommandLineApplication;
import net.dharwin.common.tools.cli.api.annotations.CLIEntry;
import net.dharwin.common.tools.cli.api.exceptions.CLIInitException;

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
