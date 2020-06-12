package ch.rhj.embedded.maven.factory;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;

@Named
public class LayoutProvider
{
	private final Map<String, ArtifactRepositoryLayout> layouts;

	@Inject
	public LayoutProvider(Map<String, ArtifactRepositoryLayout> layouts)
	{
		this.layouts = layouts;
	}

	public ArtifactRepositoryLayout getLayout(String name)
	{
		return layouts.get(name);
	}

	public ArtifactRepositoryLayout getDefaultLayout()
	{
		return getLayout("default");
	}

	public ArtifactRepositoryLayout getFlatLayout()
	{
		return getLayout("flat");
	}
}
