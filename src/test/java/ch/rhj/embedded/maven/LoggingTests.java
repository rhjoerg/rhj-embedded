package ch.rhj.embedded.maven;

import javax.inject.Inject;

import org.codehaus.plexus.logging.Logger;
import org.junit.jupiter.api.Test;

@WithMaven
public class LoggingTests
{
	@Inject
	private Logger logger;

	@Test
	public void test() throws Exception
	{
		logger.info("logger is present");
	}
}
