package ch.rhj.embedded.plexus.exclusion;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.sisu.plexus.PlexusTypeBinder;
import org.eclipse.sisu.plexus.PlexusTypeListener;
import org.eclipse.sisu.plexus.PlexusTypeVisitor;
import org.eclipse.sisu.space.SpaceModule.Strategy;
import org.eclipse.sisu.space.SpaceVisitor;

import com.google.inject.Binder;

public class ExcludingStrategy implements Strategy
{
	private final Set<String> exclusions = new TreeSet<>();

	public ExcludingStrategy(Collection<String> exclusions)
	{
		this.exclusions.addAll(exclusions);
	}

	@Override
	public SpaceVisitor visitor(Binder binder)
	{
		PlexusTypeListener delegate = new PlexusTypeBinder(binder);
		ExcludingTypeListener listener = new ExcludingTypeListener(delegate, exclusions);

		return new PlexusTypeVisitor(listener);
	}
}
