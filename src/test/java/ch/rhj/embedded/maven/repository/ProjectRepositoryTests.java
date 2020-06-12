package ch.rhj.embedded.maven.repository;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_ID;
import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class ProjectRepositoryTests
{
	@Inject
	private ProjectRepository projectRepository;

	@Test
	public void testCaching() throws Exception
	{
		Path pomPath = Paths.get("pom.xml");
		MavenProject project1 = projectRepository.get(pomPath);
		MavenProject project2 = projectRepository.get(pomPath);

		assertTrue(project1 == project2);
	}

	@Test
	public void testDirectories() throws Exception
	{
		MavenProject project = projectRepository.get(EMBEDDED_POM);
		Build build = project.getBuild();

		assertTrue(project.getBasedir().exists());
		assertTrue(project.getFile().exists());

		assertEquals("target", build.getDirectory());
		assertEquals("target/classes", build.getOutputDirectory());
		assertEquals("src/main/java", build.getSourceDirectory());
		assertEquals("target/test-classes", build.getTestOutputDirectory());
		assertEquals("src/test/java", build.getTestSourceDirectory());
	}

	@Test
	public void testProperties() throws Exception
	{
		MavenProject project = projectRepository.get(EMBEDDED_POM);
		Artifact artifact = project.getArtifact();

		assertEquals(EMBEDDED_ID, project.getId());
		assertNull(artifact.getFile());
	}
}
