package ch.rhj.embedded.plexus;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.inject.Inject;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.test.Foo1;
import ch.rhj.embedded.test.Foo2;
import ch.rhj.embedded.test.Foo3;

@WithPlexus(exclusions = "ch.rhj.embedded.test.Foo1")
@PlexusExclusions({ "ch.rhj.embedded.test.Foo2", "ch.rhj.embedded.test.Foo3" })
public class PlexusExclusionTests
{
	@Inject
	private DefaultPlexusContainer container;

	@Test
	public void test() throws Exception
	{
		assertThrows(ComponentLookupException.class, () -> container.lookup(Foo1.class));
		assertThrows(ComponentLookupException.class, () -> container.lookup(Foo2.class));
		assertThrows(ComponentLookupException.class, () -> container.lookup(Foo3.class));
	}
}
