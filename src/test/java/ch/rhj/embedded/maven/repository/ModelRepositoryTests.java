package ch.rhj.embedded.maven.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.maven.model.io.ModelReader;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.eclipse.sisu.inject.DeferredClass;
import org.eclipse.sisu.plexus.PlexusAnnotatedBeanModule;
import org.eclipse.sisu.plexus.PlexusBeanModule;
import org.eclipse.sisu.plexus.PlexusTypeBinder;
import org.eclipse.sisu.plexus.PlexusTypeListener;
import org.eclipse.sisu.plexus.PlexusTypeVisitor;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.ClassSpace;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.SpaceVisitor;
import org.eclipse.sisu.space.URLClassSpace;
import org.junit.jupiter.api.Test;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

import ch.rhj.embedded.maven.WithMaven;

@WithMaven
public class ModelRepositoryTests
{
	@Inject
	private DefaultPlexusContainer container;

	@Inject
	private ModelRepository modelRepository;

	public static class FilteredTypeBinder implements PlexusTypeListener
	{
		private final PlexusTypeListener delegate;

		private final Set<String> exclusions;

		public FilteredTypeBinder(PlexusTypeListener delegate, String... exclusions)
		{
			this.delegate = delegate;
			this.exclusions = Set.of(exclusions);
		}

		@Override
		public void hear(Class<?> qualifiedType, Object source)
		{
			if (qualified(qualifiedType.getName()))
			{
				delegate.hear(qualifiedType, source);
			}
			else
			{
				System.out.println("excluded: " + qualifiedType.getName());
			}
		}

		@Override
		public void hear(Component component, DeferredClass<?> implementation, Object source)
		{
			if (qualified(implementation.getName()))
			{
				delegate.hear(component, implementation, source);
			}
		}

		private boolean qualified(String name)
		{
			return !exclusions.contains(name);
		}
	}

	@Test
	public void test() throws Exception
	{
		System.out.println("--- ModelRepositoryTests.test ---");

		SpaceModule.Strategy strategy = new SpaceModule.Strategy()
		{
			@Override
			public SpaceVisitor visitor(Binder binder)
			{
				PlexusTypeListener delegate = new PlexusTypeBinder(binder);
				FilteredTypeBinder listener = new FilteredTypeBinder(delegate, "org.apache.maven.ReactorReader");

				return new PlexusTypeVisitor(listener);
			}
		};

		ClassRealm containerRealm = container.getContainerRealm();
		ClassSpace space = new URLClassSpace(containerRealm);
		Map<?, ?> variables = new ContextMapAdapter(container.getContext());
		BeanScanning scanning = BeanScanning.GLOBAL_INDEX;
		PlexusAnnotatedBeanModule abm = new PlexusAnnotatedBeanModule(space, variables, scanning);
		PlexusBeanModule bm = abm.with(strategy);

		Injector injector = container.addPlexusInjector(List.of(bm));
		Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();

		System.out.println(bindings.size());
		bindings.forEach((k, b) -> System.out.println(k + ": " + b));

		Class<?> type = container.getContainerRealm().loadClass("org.apache.maven.ReactorReader");
		Key<?> key = Key.get(type);
		Binding<?> binding = bindings.get(key);

		assertNull(binding);

		container.lookup(ModelReader.class);
		assertNotNull(modelRepository);
	}
}
