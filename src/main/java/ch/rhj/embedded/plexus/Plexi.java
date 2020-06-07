package ch.rhj.embedded.plexus;

import static org.codehaus.plexus.PlexusConstants.SCANNING_OFF;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.strategy.ParentFirstStrategy;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.util.ReflectionUtils;
import org.eclipse.sisu.plexus.PlexusAnnotatedBeanModule;
import org.eclipse.sisu.plexus.PlexusBeanModule;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.ClassSpace;
import org.eclipse.sisu.space.URLClassSpace;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;

import ch.rhj.embedded.plexus.exclusion.ExcludingStrategy;

public interface Plexi
{
	public final static String DEFAULT_REALM_NAME = "plexus.core";

	public static void setParentFirstStrategy(ClassRealm realm)
	{
		try
		{
			ReflectionUtils.setVariableValueInObject(realm, "strategy", new ParentFirstStrategy(realm));
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static ClassWorld newClassWorld() throws Exception
	{
		ClassWorld world = new ClassWorld(DEFAULT_REALM_NAME, ClassWorld.class.getClassLoader());

		setParentFirstStrategy(world.getClassRealm(DEFAULT_REALM_NAME));

		return world;
	}

	public static ConfigurationBuilder configurationBuilder() throws Exception
	{
		return new ConfigurationBuilder();
	}

	public static DefaultContainerConfiguration newConfiguration() throws Exception
	{
		return configurationBuilder().build();
	}

	public static DefaultPlexusContainer newContainer(ContainerConfiguration configuration, Module... customModules) throws PlexusContainerException
	{
		return new DefaultPlexusContainer(configuration, customModules);
	}

	public static Injector index(DefaultPlexusContainer container, Collection<String> exclusions)
	{
		ClassRealm containerRealm = container.getContainerRealm();
		ClassSpace space = new URLClassSpace(containerRealm);
		Map<?, ?> variables = new ContextMapAdapter(container.getContext());
		BeanScanning scanning = BeanScanning.GLOBAL_INDEX;
		PlexusAnnotatedBeanModule annotatedBeanModule = new PlexusAnnotatedBeanModule(space, variables, scanning);
		ExcludingStrategy strategy = new ExcludingStrategy(exclusions);
		PlexusBeanModule beanModule = annotatedBeanModule.with(strategy);

		return container.addPlexusInjector(List.of(beanModule));
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

	public static class ConfigurationBuilder
	{
		private ClassWorld classWorld;
		private ClassRealm classRealm;

		private boolean autoWiring = true;
		private boolean jsr250Lifecycle = true;
		private String scanning = SCANNING_OFF;

		public ConfigurationBuilder() throws Exception
		{
			this.classWorld = newClassWorld();
			this.classRealm = classWorld.getClassRealm(DEFAULT_REALM_NAME);
		}

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

		public String scanning()
		{
			return scanning;
		}

		public ConfigurationBuilder scanning(String scanning)
		{
			this.scanning = scanning;

			return this;
		}

		public DefaultContainerConfiguration build()
		{
			DefaultContainerConfiguration configuration = new DefaultContainerConfiguration();

			configuration.setClassWorld(classWorld());
			configuration.setRealm(classRealm());

			configuration.setAutoWiring(autoWiring());
			configuration.setJSR250Lifecycle(jsr250Lifecycle());
			configuration.setClassPathScanning(scanning());

			return configuration;
		}
	}
}
