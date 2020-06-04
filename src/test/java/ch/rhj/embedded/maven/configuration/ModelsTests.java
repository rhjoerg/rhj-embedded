package ch.rhj.embedded.maven.configuration;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class ModelsTests
{
	@Inject
	private Models models;

	@Test
	public void testEmbeddedModel() throws Exception
	{
		Model model = models.get(EMBEDDED_POM);

		assertEquals(EMBEDDED_ID, model.getId());
	}
}
