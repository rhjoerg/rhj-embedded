package ch.rhj.embedded.maven.config;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;
import ch.rhj.embedded.maven.context.MavenContext;

@WithMaven
public class PropertiesConfiguratorTests
{
	@Inject
	private PropertiesConfigurator propertiesConfigurator;

	@Test
	public void test() throws Exception
	{
		MavenContext context = new MavenContext(EMBEDDED_POM);

		propertiesConfigurator.configure(context);

		assertEquals(System.getenv("GITHUB_ACTOR"), context.systemProperties().get("env.GITHUB_ACTOR"));
		assertEquals(System.getenv("GITHUB_TOKEN"), context.systemProperties().get("env.GITHUB_TOKEN"));
	}
}
