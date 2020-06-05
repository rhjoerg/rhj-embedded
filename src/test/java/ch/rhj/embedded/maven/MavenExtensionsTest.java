package ch.rhj.embedded.maven;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;
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
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.junit.jupiter.api.Test;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import ch.rhj.embedded.maven.configuration.Models;
import ch.rhj.embedded.maven.configuration.ProjectWorkspaceReader;

@WithMaven
public class MavenExtensionsTest
{
	@Inject
	private DefaultPlexusContainer container;

	@Inject
	private Models models;

	@Inject
	private ProjectWorkspaceReader workspaceReader;

	@Inject
	private MutableBeanLocator beanLocator;

//	@Test
	public void testBeanLocator()
	{
		System.out.println("--- testBeanLocator ---");

		beanLocator.publishers().forEach(p -> System.out.println(p.getClass() + ": " + p));
	}

//	@Test
	public void testWorkspaceReader() throws Exception
	{
		System.out.println("--- testWorkspaceReader ---");

		Class<WorkspaceReader> type = WorkspaceReader.class;
		String role = type.getName();
		Map<String, ComponentDescriptor<WorkspaceReader>> map = container.getComponentDescriptorMap(type, role);
		ComponentDescriptor<WorkspaceReader> descriptor = container.getComponentDescriptor(type, role, "reactor");

		System.out.println(map.size());
		map.forEach((r, c) -> System.out.println(r + ": " + c.getRoleHint() + ": " + c.getImplementation()));
		System.out.println(descriptor);
	}

	private void fixWorkspaceReader() throws Exception
	{
		System.out.println("--- fixWorkspaceReader ---");

		Class<WorkspaceReader> type = WorkspaceReader.class;
		String role = type.getName();

		Injector injector;
		List<Binding<WorkspaceReader>> bindings;

		injector = container.addPlexusInjector(List.of());
		bindings = injector.findBindingsByType(TypeLiteral.get(WorkspaceReader.class));
		System.out.println(bindings.size());
		bindings.forEach(binding -> System.out.println(binding));

		injector = container.addPlexusInjector(List.of(), binder ->
		{
			binder.bind(WorkspaceReader.class).annotatedWith(Names.named("reactor")).toInstance(workspaceReader);
		});

		bindings = injector.findBindingsByType(TypeLiteral.get(WorkspaceReader.class));
		System.out.println(bindings.size());
		bindings.forEach(binding -> System.out.println(binding));

		Map<String, ComponentDescriptor<WorkspaceReader>> map = container.getComponentDescriptorMap(type, role);

		System.out.println(map.size());
		map.forEach((r, c) -> System.out.println(r + ": " + c.getRoleHint() + ": " + c.getImplementation()));
	}

	@Test
	public void testMavenValidate() throws Exception
	{
		fixWorkspaceReader();

		System.out.println("--- testMavenValidate ---");

		Model model = models.get(EMBEDDED_POM);

		MavenExecutionRequest request = new DefaultMavenExecutionRequest();
		MavenExecutionResult result = new DefaultMavenExecutionResult();

		RepositorySystem repositorySystem = container.lookup(RepositorySystem.class);
		ArtifactRepository localRepository = repositorySystem.createDefaultLocalRepository();
		ArtifactRepository remoteRepository = repositorySystem.createDefaultRemoteRepository();
//		WorkspaceReader workspaceReader = container.lookup(ProjectWorkspaceReader.class);

		request.setLocalRepository(localRepository);
		request.setRemoteRepositories(Arrays.asList(remoteRepository));
		request.setGoals(Arrays.asList("validate"));
		request.setBaseDirectory(model.getProjectDirectory());
		request.setPom(model.getPomFile());
//		request.setWorkspaceReader(workspaceReader);

		Maven maven = container.lookup(Maven.class);

		result = maven.execute(request);

		if (result.hasExceptions())
		{
			result.getExceptions().forEach(e -> e.printStackTrace());
			fail();
		}
	}
}
