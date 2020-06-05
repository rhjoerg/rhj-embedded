package ch.rhj.embedded.plexus;

import static ch.rhj.embedded.plexus.Plexi.ScanningMode.scanningMode;
import static org.codehaus.plexus.PlexusConstants.SCANNING_OFF;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.plexus.Plexi.ScanningMode;
import ch.rhj.embedded.test.Foo;

public class PlexiTests
{
	@Inject
	private static Foo staticFoo;

	@Inject
	private Foo instanceFoo;

	@Test
	public void testNewClassWorld() throws ClassNotFoundException
	{
		ClassWorld classWorld = Plexi.newClassWorld();
		ClassRealm classRealm = classWorld.getClassRealm(Plexi.DEFAULT_REALM_NAME);

		Class<?> expected = PlexiTests.class;
		Class<?> actual = classRealm.loadClass(PlexiTests.class.getName());

		assertEquals(expected, actual);
	}

	@Test
	public void testScanningMode()
	{
		assertEquals(ScanningMode.OFF, scanningMode(SCANNING_OFF));
		assertThrows(IllegalArgumentException.class, () -> scanningMode(""));
	}

	@Test
	public void testConfigurationBuilder()
	{
		DefaultContainerConfiguration configuration = Plexi.configurationBuilder().build();

		assertTrue(configuration.getAutoWiring());
		assertEquals(ScanningMode.OFF.value, configuration.getClassPathScanning());
	}

	@Test
	public void testNewConfiguration()
	{
		DefaultContainerConfiguration configuration = Plexi.newConfiguration();

		assertTrue(configuration.getAutoWiring());
		assertEquals(ScanningMode.OFF.value, configuration.getClassPathScanning());
	}

	@Test
	public void testNewContainer() throws Exception
	{
		Plexi.newContainer(Plexi.newConfiguration());
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
