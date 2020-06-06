package ch.rhj.embedded.plexus.exclusion;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.codehaus.plexus.component.annotations.Component;
import org.eclipse.sisu.inject.DeferredClass;
import org.eclipse.sisu.plexus.PlexusTypeListener;

public class ExcludingTypeListener implements PlexusTypeListener
{
	private final PlexusTypeListener delegate;

	private final Set<String> exclusions = new TreeSet<>();

	public ExcludingTypeListener(PlexusTypeListener delegate, Collection<String> exclusions)
	{
		this.delegate = delegate;
		this.exclusions.addAll(exclusions);
	}

	@Override
	public void hear(Class<?> qualifiedType, Object source)
	{
		if (qualified(qualifiedType.getName()))
		{
			delegate.hear(qualifiedType, source);
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
