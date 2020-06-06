package ch.rhj.embedded.maven;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

import org.apache.maven.Maven;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Model;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.eclipse.aether.repository.WorkspaceReader;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.repository.ModelRepository;
import ch.rhj.embedded.maven.repository.ProjectWorkspaceReader;

@WithMaven
public class MavenExtensionsTest
{
	@Inject
	private DefaultPlexusContainer container;

	@Inject
	private ModelRepository modelRepository;

	@Test
	public void testWorkspaceReader() throws Exception
	{
		Class<WorkspaceReader> type = WorkspaceReader.class;
		String role = type.getName();
		Map<String, ComponentDescriptor<WorkspaceReader>> map = container.getComponentDescriptorMap(type, role);
		ComponentDescriptor<WorkspaceReader> descriptor = container.getComponentDescriptor(type, role, "reactor");

		assertEquals(1, map.size());
		assertEquals(ProjectWorkspaceReader.class, descriptor.getImplementationClass());
	}

	@Test
	public void testMavenValidate() throws Exception
	{
		System.out.println("--- testMavenValidate ---");

		Model model = modelRepository.get(EMBEDDED_POM);

		MavenExecutionRequest request = new DefaultMavenExecutionRequest();
		MavenExecutionResult result = new DefaultMavenExecutionResult();

		RepositorySystem repositorySystem = container.lookup(RepositorySystem.class);
		ArtifactRepository localRepository = repositorySystem.createDefaultLocalRepository();
		ArtifactRepository remoteRepository = repositorySystem.createDefaultRemoteRepository();

		request.setLocalRepository(localRepository);
		request.setRemoteRepositories(Arrays.asList(remoteRepository));
		request.setGoals(Arrays.asList("validate"));
		request.setBaseDirectory(model.getProjectDirectory());
		request.setPom(model.getPomFile());

		Maven maven = container.lookup(Maven.class);

		result = maven.execute(request);

		if (result.hasExceptions())
		{
			result.getExceptions().forEach(e -> e.printStackTrace());
			fail();
		}
	}
}
