package ch.rhj.embedded.maven;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_POM;

import javax.inject.Inject;

import org.codehaus.plexus.logging.Logger;
import org.junit.jupiter.api.Test;

@WithMaven
public class BuilderTests
{
	@Inject
	private Builder builder;

	@Inject
	private Logger logger;

	@Test
	public void testValidateEmbedded() throws Exception
	{
		System.out.println("--- testValidateEmbedded ---");
		builder.build(EMBEDDED_POM, "validate");
	}

	@Test
	public void testVerifyPlugin() throws Exception
	{
		System.out.println("--- testVerifyPlugin ---");
		builder.build(PLUGIN_POM, "clean", "test-compile");

		logger.warn("test-compile only, verify doesn't work yet");
	}
}
