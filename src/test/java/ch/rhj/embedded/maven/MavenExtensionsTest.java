package ch.rhj.embedded.maven;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.maven.Maven;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Model;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.PlexusContainer;
import org.eclipse.aether.repository.WorkspaceReader;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.configuration.Models;
import ch.rhj.embedded.maven.configuration.ProjectWorkspaceReader;

@WithMaven
public class MavenExtensionsTest
{
	@Inject
	private Models models;

	@Inject
	private PlexusContainer container;

	@Test
	public void testValidate() throws Exception
	{
		Model model = models.get(EMBEDDED_POM);

		MavenExecutionRequest request = new DefaultMavenExecutionRequest();
		MavenExecutionResult result = new DefaultMavenExecutionResult();

		RepositorySystem repositorySystem = container.lookup(RepositorySystem.class);
		ArtifactRepository localRepository = repositorySystem.createDefaultLocalRepository();
		ArtifactRepository remoteRepository = repositorySystem.createDefaultRemoteRepository();
		WorkspaceReader workspaceReader = container.lookup(ProjectWorkspaceReader.class);

		request.setLocalRepository(localRepository);
		request.setRemoteRepositories(Arrays.asList(remoteRepository));
		request.setGoals(Arrays.asList("validate"));
		request.setBaseDirectory(model.getProjectDirectory());
		request.setPom(model.getPomFile());
		request.setWorkspaceReader(workspaceReader);

		Maven maven = container.lookup(Maven.class);

		result = maven.execute(request);

		if (result.hasExceptions())
		{
			result.getExceptions().forEach(e -> e.printStackTrace());
			fail();
		}
	}
}
