package ch.rhj.embedded.plexus;

import static org.codehaus.plexus.PlexusConstants.SCANNING_CACHE;
import static org.codehaus.plexus.PlexusConstants.SCANNING_INDEX;
import static org.codehaus.plexus.PlexusConstants.SCANNING_OFF;
import static org.codehaus.plexus.PlexusConstants.SCANNING_ON;

import java.util.Collections;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import com.google.inject.Binder;
import com.google.inject.Module;

public interface Plexi
{
	public final static String DEFAULT_REALM_NAME = "plexus.core";

	public static ClassWorld newClassWorld()
	{
		return new ClassWorld(DEFAULT_REALM_NAME, Thread.currentThread().getContextClassLoader());
	}

	public static ConfigurationBuilder configurationBuilder()
	{
		return new ConfigurationBuilder();
	}

	public static DefaultContainerConfiguration newConfiguration()
	{
		return configurationBuilder().build();
	}

	public static DefaultPlexusContainer newContainer(ContainerConfiguration configuration, Module... customModules) throws PlexusContainerException
	{
		return new DefaultPlexusContainer(configuration, customModules);
	}

	public static <T> void inject(DefaultPlexusContainer container, Class<? super T> role, String hint, T component)
	{
		container.addComponent(component, role, hint);
	}

	public static void requestStaticInjection(DefaultPlexusContainer container, Class<?> target)
	{
		Module module = new Module()
		{
			@Override
			public void configure(Binder binder)
			{
				binder.requestStaticInjection(target);
			}
		};

		container.addPlexusInjector(Collections.emptyList(), module);
	}

	public static void requestInjection(DefaultPlexusContainer container, Object target)
	{
		Module module = new Module()
		{
			@Override
			public void configure(Binder binder)
			{
				binder.requestInjection(target);
			}
		};

		container.addPlexusInjector(Collections.emptyList(), module);
	}

	public static enum ScanningMode
	{
		OFF(SCANNING_OFF),

		ON(SCANNING_ON),

		INDEX(SCANNING_INDEX),

		CACHE(SCANNING_CACHE);

		public final String value;

		private ScanningMode(String value)
		{
			this.value = value;
		}

		public static ScanningMode scanningMode(String value)
		{
			for (ScanningMode scanning : ScanningMode.values())
			{
				if (scanning.value.equalsIgnoreCase(value))
				{
					return scanning;
				}
			}

			throw new IllegalArgumentException();
		}
	}

	public static class ConfigurationBuilder
	{
		private ClassWorld classWorld = newClassWorld();
		private ClassRealm classRealm = classWorld.getClassRealm(DEFAULT_REALM_NAME);

		private boolean autoWiring = true;
		private boolean jsr250Lifecycle = true;
		private ScanningMode scanningMode = ScanningMode.INDEX;

		public ClassWorld classWorld()
		{
			return classWorld;
		}

		public ClassRealm classRealm()
		{
			return classRealm;
		}

		public boolean autoWiring()
		{
			return autoWiring;
		}

		public boolean jsr250Lifecycle()
		{
			return jsr250Lifecycle;
		}

		public ScanningMode scanningMode()
		{
			return scanningMode;
		}

		public DefaultContainerConfiguration build()
		{
			DefaultContainerConfiguration configuration = new DefaultContainerConfiguration();

			configuration.setClassWorld(classWorld());
			configuration.setRealm(classRealm());

			configuration.setAutoWiring(autoWiring());
			configuration.setJSR250Lifecycle(jsr250Lifecycle());
			configuration.setClassPathScanning(scanningMode().value);

			return configuration;
		}
	}
}
