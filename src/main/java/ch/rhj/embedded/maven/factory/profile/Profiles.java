package ch.rhj.embedded.maven.factory.profile;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.maven.settings.SettingsUtils.convertFromSettingsProfile;
import static org.apache.maven.settings.SettingsUtils.convertToSettingsProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Profiles
{
	public static org.apache.maven.model.Profile toModelProfile(org.apache.maven.settings.Profile settingsProfile)
	{
		return convertFromSettingsProfile(settingsProfile);
	}

	public static List<org.apache.maven.model.Profile> toModelProfiles(Collection<org.apache.maven.settings.Profile> settingsProfiles)
	{
		return settingsProfiles.stream().map(Profiles::toModelProfile).collect(toList());
	}

	public static org.apache.maven.settings.Profile toSettingsProfile(org.apache.maven.model.Profile modelProfile)
	{
		return convertToSettingsProfile(modelProfile);
	}

	public static List<org.apache.maven.settings.Profile> toSettingsProfiles(Collection<org.apache.maven.model.Profile> modelProfiles)
	{
		return modelProfiles.stream().map(Profiles::toSettingsProfile).collect(toList());
	}

	public static List<String> modelProfilesIds(Collection<org.apache.maven.model.Profile> modelProfiles)
	{
		return new ArrayList<>(modelProfiles.stream().map(profile -> profile.getId()).collect(toSet()));
	}

	public static List<String> settingsProfilesIds(Collection<org.apache.maven.settings.Profile> settingsProfiles)
	{
		return new ArrayList<>(settingsProfiles.stream().map(profile -> profile.getId()).collect(toSet()));
	}
}
