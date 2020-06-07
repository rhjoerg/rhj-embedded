package ch.rhj.embedded.maven.build;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Named;

import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;

@Named
public class ProfilesFactory
{
	public List<Profile> activeProfiles(Settings settings)
	{
		List<Profile> activeByDefaultProfiles = activeByDefaultProfiles(settings);
		List<Profile> activatedProfiles = activatedProfiles(settings, activeByDefaultProfiles);

		List<Profile> result = new ArrayList<>();

		result.addAll(activeByDefaultProfiles);
		result.addAll(activatedProfiles);

		return result;
	}

	private List<Profile> activeByDefaultProfiles(Settings settings)
	{
		List<Profile> result = new ArrayList<>();

		settings.getProfiles().stream() //
				.filter(p -> p.getActivation() != null) //
				.filter(p -> p.getActivation().isActiveByDefault()) //
				.forEach(p -> result.add(p));

		return result;
	}

	private List<Profile> activatedProfiles(Settings settings, List<Profile> existing)
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
