package ch.rhj.embedded.maven;

import static ch.rhj.embedded.maven.MavenTestsConstants.BASEDIR;
import static ch.rhj.embedded.maven.MavenTestsConstants.POM_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.configuration.Models;

@WithMaven
public class MavenExtensionsTest
{
	@Inject
	private Models models;

	@Test
	public void testMavenProject() throws Exception
	{
		Model model = models.read(POM_PATH, true);
		MavenProject project = new MavenProject(model);

		project.setFile(model.getPomFile());

		assertEquals(BASEDIR.toFile(), project.getBasedir());
	}
}
