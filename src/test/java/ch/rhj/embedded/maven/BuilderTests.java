package ch.rhj.embedded.maven;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_POM;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

@WithMaven
public class BuilderTests
{
	@Inject
	private Builder builder;

	@Test
	public void testValidateEmbedded() throws Exception
	{
		builder.build(EMBEDDED_POM, "validate");
	}

	@Test
	public void testVerifyPlugin() throws Exception
	{
		builder.build(PLUGIN_POM, "verify");
	}
}
