package ch.rhj.embedded.plexus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.jupiter.api.Test;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

import ch.rhj.embedded.test.Foo;

public class PlexiTests
{
	@Inject
	private static Foo staticFoo;

	@Inject
	private Foo instanceFoo;

	@Test
	public void testNewClassWorld() throws Exception
	{
		ClassWorld classWorld = Plexi.newClassWorld();
		ClassRealm classRealm = classWorld.getClassRealm(Plexi.DEFAULT_REALM_NAME);

		Class<?> expected = PlexiTests.class;
		Class<?> actual = classRealm.loadClass(PlexiTests.class.getName());

		assertEquals(expected, actual);
	}

	@Test
	public void testConfigurationBuilder() throws Exception
	{
		DefaultContainerConfiguration configuration = Plexi.configurationBuilder().build();

		assertTrue(configuration.getAutoWiring());
		assertEquals(PlexusConstants.SCANNING_OFF, configuration.getClassPathScanning());
	}

	@Test
	public void testNewConfiguration() throws Exception
	{
		DefaultContainerConfiguration configuration = Plexi.newConfiguration();

		assertTrue(configuration.getAutoWiring());
		assertEquals(PlexusConstants.SCANNING_OFF, configuration.getClassPathScanning());
	}

	@Test
	public void testNewContainer() throws Exception
	{
		DefaultPlexusContainer container = Plexi.newContainer(Plexi.newConfiguration());
		Injector injector;
		Binding<Foo> binding;

		injector = container.addPlexusInjector(List.of());
		binding = injector.getExistingBinding(Key.get(Foo.class));

		assertNull(binding);
		assertThrows(ComponentLookupException.class, () -> container.lookup(Foo.class));

		injector = Plexi.index(container, List.of());
		binding = injector.getExistingBinding(Key.get(Foo.class));

		assertNotNull(binding);
		assertNotNull(container.lookup(Foo.class));
	}

	@Test
	public void testRequestStaticInjection() throws Exception
	{
		DefaultPlexusContainer container = Plexi.newContainer(Plexi.newConfiguration());

		assertTrue(staticFoo == null);
		Plexi.requestStaticInjection(container, PlexiTests.class);
		assertTrue(staticFoo != null);
	}

	@Test
	public void testRequestInjection() throws Exception
	{
		DefaultPlexusContainer container = Plexi.newContainer(Plexi.newConfiguration());

		assertTrue(instanceFoo == null);
		Plexi.requestInjection(container, this);
		assertTrue(instanceFoo != null);
	}
}
