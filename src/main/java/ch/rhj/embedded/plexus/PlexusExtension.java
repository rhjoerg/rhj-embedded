package ch.rhj.embedded.plexus;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

public class PlexusExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback
{
	public final static Namespace STATIC_NAMESPACE = Namespace.create("PlexusExtension");
	public final static String CONTAINER_KEY = "container";

	@Override
	public void beforeAll(ExtensionContext context) throws Exception
	{
		getStore(context).put(CONTAINER_KEY, Plexi.newContainer(Plexi.newConfiguration()));
		Plexi.requestStaticInjection(getContainer(context), context.getRequiredTestClass());
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
}
