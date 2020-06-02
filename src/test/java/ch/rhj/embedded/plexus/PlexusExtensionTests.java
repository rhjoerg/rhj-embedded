package ch.rhj.embedded.plexus;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.test.Foo;

@WithPlexus
public class PlexusExtensionTests
{
	@Inject
	private static Foo staticFoo;

	@Inject
	private Foo instanceFoo;

	@Inject
	private DefaultPlexusContainer container;

	@Test
	public void test()
	{
		assertNotNull(staticFoo);
		assertNotNull(instanceFoo);
		assertNotNull(container);
	}
}
