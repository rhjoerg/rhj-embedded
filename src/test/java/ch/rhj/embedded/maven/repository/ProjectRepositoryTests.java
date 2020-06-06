package ch.rhj.embedded.maven.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

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
}
