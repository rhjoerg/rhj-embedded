package ch.rhj.embedded.plexus;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import com.google.inject.Module;

public class PlexusExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback
{
	public final static Namespace STATIC_NAMESPACE = Namespace.create("PlexusExtension");
	public final static String CONTAINER_KEY = "container";

	@Override
	public void beforeAll(ExtensionContext context) throws Exception
	{
		DefaultPlexusContainer container = createContainer(context);

		getStore(context).put(CONTAINER_KEY, container);
		Plexi.requestStaticInjection(container, context.getRequiredTestClass());
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception
	{
		getStore(context).remove(CONTAINER_KEY, DefaultPlexusContainer.class).dispose();
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception
	{
		Plexi.requestInjection(getContainer(context), context.getRequiredTestInstance());
	}

	private DefaultPlexusContainer getContainer(ExtensionContext context)
	{
		return getStore(context).get(CONTAINER_KEY, DefaultPlexusContainer.class);
	}

	private Store getStore(ExtensionContext context)
	{
		return context.getStore(STATIC_NAMESPACE);
	}

	protected DefaultPlexusContainer createContainer(ExtensionContext context) throws Exception
	{
		DefaultContainerConfiguration configuration = Plexi.newConfiguration();
		Module[] modules = getModules(context);
		DefaultPlexusContainer container = Plexi.newContainer(configuration, modules);

		Plexi.index(container, getExclusions(context));

		return container;
	}

	protected Module[] getModules(ExtensionContext context)
	{
		return getModuleClasses(context).stream().map(this::createModule).filter(m -> m != null).toArray(Module[]::new);
	}

	protected Module createModule(Class<? extends Module> type)
	{
		try
		{
			return type.getDeclaredConstructor().newInstance();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	protected List<Class<? extends Module>> getModuleClasses(ExtensionContext context)
	{
		List<Class<? extends Module>> classes = new ArrayList<>();
		Class<?> testClass = context.getRequiredTestClass();

		findAnnotation(testClass, PlexusModules.class).ifPresent(a -> classes.addAll(List.of(a.value())));
		findAnnotation(testClass, WithPlexus.class).ifPresent(a -> classes.addAll(List.of(a.modules())));

		return classes;
	}

	protected Set<String> getExclusions(ExtensionContext context)
	{
		Set<String> exclusions = new TreeSet<>();
		Class<?> testClass = context.getRequiredTestClass();

		findAnnotation(testClass, PlexusExclusions.class).ifPresent(a -> exclusions.addAll(List.of(a.value())));
		findAnnotation(testClass, WithPlexus.class).ifPresent(a -> exclusions.addAll(List.of(a.exclusions())));

		return exclusions;
	}
}
