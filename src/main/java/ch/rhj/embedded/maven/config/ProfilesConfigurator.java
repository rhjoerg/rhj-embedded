package ch.rhj.embedded.maven.config;

import static ch.rhj.embedded.maven.util.ProfileConverter.convertToModelProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Named;

import org.apache.maven.model.Activation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

import ch.rhj.embedded.maven.context.ContextProfiles;
import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class ProfilesConfigurator implements MavenConfigurator
{
	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.PROFILES_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context)
	{
		ContextProfiles result = new ContextProfiles();
		TreeSet<String> activeProfileIds = discoverActiveProfileIds(context);
		List<Profile> profiles = discoverProfiles(context);

		profiles.forEach(profile ->
		{
			boolean active = activeProfileIds.contains(profile.getId());

			result.add(profile, active);
		});

		context.profiles(result);
	}

	private List<Profile> discoverProfiles(MavenContext context)
	{
		List<Profile> result = new ArrayList<>();

		discoverProfiles(context.settings(), result);
		discoverProfiles(context.project(), result);
		discoverProfiles(context.model(), result);

		return result;
	}

	private void discoverProfiles(Settings settings, List<Profile> result)
	{
		result.addAll(convertToModelProfiles(settings.getProfiles()));
	}

	private void discoverProfiles(MavenProject project, List<Profile> result)
	{
		if (project != null)
		{
			result.addAll(project.getActiveProfiles());
		}
	}

	private void discoverProfiles(Model model, List<Profile> result)
	{
		if (model != null)
		{
			result.addAll(model.getProfiles());
		}
	}

	private TreeSet<String> discoverActiveProfileIds(MavenContext context)
	{
		TreeSet<String> result = new TreeSet<>();

		discoverActiveProfileIds(context.settings(), result);
		discoverActiveProfileIds(context.project(), result);
		discoverActiveProfileIds(context.model(), result);

		return result;
	}

	private void discoverActiveProfileIds(Settings settings, Set<String> result)
	{
		result.addAll(settings.getActiveProfiles());
	}

	private void discoverActiveProfileIds(MavenProject project, Set<String> result)
	{
		if (project != null)
		{
			List<Profile> profiles = project.getActiveProfiles();

			if (profiles != null)
			{
				profiles.forEach(profile -> result.add(profile.getId()));
			}
		}
	}

	private void discoverActiveProfileIds(Model model, Set<String> result)
	{
		if (model != null)
		{
			List<Profile> profiles = model.getProfiles();

			if (profiles != null)
			{
				profiles.forEach(profile ->
				{
					Activation activation = profile.getActivation();

					if (activation != null && activation.isActiveByDefault())
					{
						result.add(profile.getId());
					}
				});
			}
		}
	}
}
