package ch.rhj.embedded.maven.plugin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.inject.Inject;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class RepositoryConnectorFactoryTests
{
	@Inject
	private DefaultPlexusContainer container;

	@Test
	public void test() throws Exception
	{
		System.out.println("--- RepositoryConnectorFactoryTests ---");

		Map<String, RepositoryConnectorFactory> factories = container.lookupMap(RepositoryConnectorFactory.class);

		assertTrue(factories.size() > 0);
	}
}
