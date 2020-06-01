package ch.rhj.embedded.plexus;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Test;

@WithPlexus
public class PlexusExtensionTests
{
	@Inject
	private static Foo staticFoo;

	@Inject
	private Foo instanceFoo;

	@Test
	public void test()
	{
		assertTrue(staticFoo != null);
		assertTrue(instanceFoo != null);
	}

	@Named
	public static class Foo
	{
	}
}
