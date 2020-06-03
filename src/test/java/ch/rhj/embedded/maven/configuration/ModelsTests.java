package ch.rhj.embedded.maven.configuration;

import static ch.rhj.embedded.maven.MavenTestsConstants.ARTIFACT_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.GROUP_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.POM_PATH;
import static ch.rhj.embedded.maven.MavenTestsConstants.VERSION;
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
	public void test() throws Exception
	{
		Model model = models.read(POM_PATH, true);

		assertEquals(GROUP_ID, model.getGroupId());
		assertEquals(ARTIFACT_ID, model.getArtifactId());
		assertEquals(VERSION, model.getVersion());
	}
}
