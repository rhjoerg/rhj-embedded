package ch.rhj.embedded.maven.project;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class ProjectFactoryTests
{
	@Inject
	private ProjectFactory factory;

	@Test
	public void testEmbedded() throws Exception
	{
		MavenProject project = factory.create(EMBEDDED_POM);

		assertEquals(EMBEDDED_POM, project.getFile().toPath());
		assertEquals(EMBEDDED_ID, project.getArtifact().getId());

		Build build = project.getBuild();

		assertEquals("target", build.getDirectory());

		assertEquals("src/main/java", build.getSourceDirectory());
		assertEquals("target/classes", build.getOutputDirectory());

		assertEquals("src/test/java", build.getTestSourceDirectory());
		assertEquals("target/test-classes", build.getTestOutputDirectory());

		assertEquals("src/main/scripts", build.getScriptSourceDirectory());
	}
}
