package ch.rhj.embedded.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.inject.Inject;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.eclipse.aether.repository.WorkspaceReader;
import org.junit.jupiter.api.Test;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;

import ch.rhj.embedded.maven.build.ProjectWorkspaceReader;

@WithMaven
public class MavenExtensionsTests
{
	@Inject
	private DefaultPlexusContainer container;

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
	public void testSecDispatchers() throws Exception
	{
		Map<String, SecDispatcher> dispatchers = container.lookupMap(SecDispatcher.class);

		assertTrue(dispatchers.containsKey("maven"));
	}
}
