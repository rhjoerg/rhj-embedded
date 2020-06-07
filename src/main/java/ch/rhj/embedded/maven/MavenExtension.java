package ch.rhj.embedded.maven;

import java.util.Set;

import org.junit.jupiter.api.extension.ExtensionContext;

import ch.rhj.embedded.plexus.PlexusExtension;

public class MavenExtension extends PlexusExtension
{
	@Override
	protected Set<String> getExclusions(ExtensionContext context)
	{
		Set<String> exclusions = super.getExclusions(context);

		exclusions.add("org.apache.maven.ReactorReader");
		exclusions.add("org.apache.maven.classrealm.DefaultClassRealmManager");

		return exclusions;
	}
}
