package ch.rhj.embedded.maven.factory;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.maven.settings.SettingsUtils.convertFromSettingsProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;

import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class ProfilesFactory
{
	@Inject
	public ProfilesFactory()
	{
	}

	public List<org.apache.maven.settings.Profile> createSettingsProfiles(MavenContext context)
	{
		Settings settings = context.settings();
		List<org.apache.maven.settings.Profile> activeByDefaultProfiles = activeByDefaultProfiles(settings);
		List<org.apache.maven.settings.Profile> activatedProfiles = activatedProfiles(settings, activeByDefaultProfiles);

		List<org.apache.maven.settings.Profile> result = new ArrayList<>();

		result.addAll(activeByDefaultProfiles);
		result.addAll(activatedProfiles);

		return result;
	}

	public List<org.apache.maven.model.Profile> createModelProfiles(MavenContext context) throws Exception
	{
		return createSettingsProfiles(context).stream().map(p -> convertFromSettingsProfile(p)).collect(toList());
	}

	private List<org.apache.maven.settings.Profile> activeByDefaultProfiles(Settings settings)
	{
		List<org.apache.maven.settings.Profile> result = new ArrayList<>();

		settings.getProfiles().stream() //
				.filter(p -> p.getActivation() != null) //
				.filter(p -> p.getActivation().isActiveByDefault()) //
				.forEach(p -> result.add(p));

		return result;
	}

	private List<org.apache.maven.settings.Profile> activatedProfiles(Settings settings, List<org.apache.maven.settings.Profile> existing)
	{
		List<Profile> result = new ArrayList<>();

		Set<String> activatedIds = new TreeSet<>(settings.getActiveProfiles());
		Set<String> existingIds = existing.stream().map(p -> p.getId()).collect(toSet());

		settings.getProfiles().stream() //
				.filter(p -> activatedIds.contains(p.getId())) //
				.filter(p -> !existingIds.contains(p.getId())) //
				.forEach(p -> result.add(p));

		return result;
	}
}
