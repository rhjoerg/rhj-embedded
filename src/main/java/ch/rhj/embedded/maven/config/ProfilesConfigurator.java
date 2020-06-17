package ch.rhj.embedded.maven.config;

import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.settings.Settings;
import org.codehaus.plexus.logging.Logger;

import ch.rhj.embedded.maven.context.ContextProfiles;
import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class ProfilesConfigurator
{
	private final Logger logger;

	@Inject
	public ProfilesConfigurator(Logger logger)
	{
		this.logger = logger;
	}

	public void configure(MavenContext context)
	{
		ContextProfiles profiles = new ContextProfiles();
		Settings settings = context.settings();
		TreeSet<String> activeProfileIds = new TreeSet<>(settings.getActiveProfiles());

		settings.getProfiles().forEach(profile ->
		{
			boolean active = activeProfileIds.contains(profile.getId());

			if (!active && profile.getActivation() != null)
			{
				active = profile.getActivation().isActiveByDefault();
			}

			profiles.add(profile, active);
		});

		context.profiles(profiles);

		logger.warn("model profiles not yet respected");
	}
}
