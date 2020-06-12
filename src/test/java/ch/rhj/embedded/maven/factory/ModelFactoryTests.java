package ch.rhj.embedded.maven.factory;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.TARGET_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.TARGET_POM;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class ModelFactoryTests
{
	@Inject
	private ModelFactory factory;

	@Test
	public void testEmbedded() throws Exception
	{
		Model model = factory.create(EMBEDDED_POM);

		assertEquals(EMBEDDED_ID, model.getId());
	}

	@Test
	public void testPlugin() throws Exception
	{
		Model model = factory.create(PLUGIN_POM);

		assertEquals(PLUGIN_ID, model.getId());
	}

	@Test
	public void testTarget() throws Exception
	{
		Model model = factory.create(TARGET_POM);

		assertEquals(TARGET_ID, model.getId());
	}
}
