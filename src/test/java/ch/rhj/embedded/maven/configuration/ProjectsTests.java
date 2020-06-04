package ch.rhj.embedded.maven.configuration;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_POM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class ProjectsTests
{
	@Inject
	private Projects projects;

	@Test
	public void test() throws Exception
	{
		MavenProject project1 = projects.get(EMBEDDED_POM);
		MavenProject project2 = projects.get(EMBEDDED_POM);
		MavenProject project3 = projects.get(PLUGIN_POM);

		assertTrue(project1 == project2);

		assertEquals(EMBEDDED_ID, project1.getId());
		assertEquals(PLUGIN_ID, project3.getId());
	}
}
