package ch.rhj.embedded.maven.factory;

import static ch.rhj.embedded.maven.MavenTestsConstants.BASE_DIR;
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
		MavenProject project = factory.createProject(EMBEDDED_POM);

		assertEquals(EMBEDDED_POM, project.getFile().toPath());
		assertEquals(EMBEDDED_ID, project.getArtifact().getId());

		Build build = project.getBuild();

		assertEquals(BASE_DIR.resolve("target").toString(), build.getDirectory());

		assertEquals(BASE_DIR.resolve("src/main/java").toString(), build.getSourceDirectory());
		assertEquals(BASE_DIR.resolve("target/classes").toString(), build.getOutputDirectory());

		assertEquals(BASE_DIR.resolve("src/test/java").toString(), build.getTestSourceDirectory());
		assertEquals(BASE_DIR.resolve("target/test-classes").toString(), build.getTestOutputDirectory());

		assertEquals(BASE_DIR.resolve("src/main/scripts").toString(), build.getScriptSourceDirectory());
	}
}
