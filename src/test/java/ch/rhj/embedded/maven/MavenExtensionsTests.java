package ch.rhj.embedded.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Map;

import javax.inject.Inject;

import org.apache.maven.tools.plugin.scanner.MojoScanner;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.eclipse.aether.impl.MetadataResolver;
import org.eclipse.aether.repository.WorkspaceReader;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.repository.ProjectWorkspaceReader;

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
	public void testMetadataResolver() throws Exception
	{
		System.out.println("--- testMetadataResolver ---");

		Map<String, MetadataResolver> resolvers = container.lookupMap(MetadataResolver.class);

		if (resolvers.size() > 1)
		{
			resolvers.forEach((hint, resolver) ->
			{
				Class<? extends MetadataResolver> type = resolver.getClass();
				ProtectionDomain domain = type.getProtectionDomain();
				URL url = domain.getCodeSource().getLocation();

				System.out.println(hint + ": " + url);
			});

			fail();
		}
	}

	@Test
	public void testMojoScanner() throws Exception
	{
		System.out.println("--- testMojoScanner ---");

		Map<String, MojoScanner> scanners = container.lookupMap(MojoScanner.class);

		assertEquals(1, scanners.size());
	}
}
