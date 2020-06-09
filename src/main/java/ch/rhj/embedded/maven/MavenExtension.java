package ch.rhj.embedded.maven;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.inject.Module;

import ch.rhj.embedded.plexus.PlexusExtension;

public class MavenExtension extends PlexusExtension
{
	@Override
	protected Set<String> getExclusions(ExtensionContext context)
	{
		Set<String> exclusions = super.getExclusions(context);

		exclusions.add("org.apache.maven.ReactorReader");
//		exclusions.add("org.apache.maven.classrealm.DefaultClassRealmManager");

		return exclusions;
	}

	@Override
	protected List<Class<? extends Module>> getModuleClasses(ExtensionContext context)
	{
		List<Class<? extends Module>> classes = super.getModuleClasses(context);

//		classes.add(ResolverModule.class);

		return classes;
	}
}
