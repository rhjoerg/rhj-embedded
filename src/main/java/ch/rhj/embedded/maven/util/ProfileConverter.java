package ch.rhj.embedded.maven.util;

import static java.util.stream.Collectors.toList;
import static org.apache.maven.settings.SettingsUtils.convertFromSettingsProfile;

import java.util.Collection;
import java.util.List;

public interface ProfileConverter
{
	public static org.apache.maven.model.Profile convertToModelProfile(org.apache.maven.settings.Profile settingsProfile)
	{
		return convertFromSettingsProfile(settingsProfile);
	}

	public static List<org.apache.maven.model.Profile> convertToModelProfiles(Collection<org.apache.maven.settings.Profile> settingsProfiles)
	{
		return settingsProfiles.stream().map(ProfileConverter::convertToModelProfile).collect(toList());
	}

	public static org.apache.maven.settings.Profile convertToSettingsProfile(org.apache.maven.model.Profile modelProfile)
	{
		return convertToSettingsProfile(modelProfile);
	}

	public static List<org.apache.maven.settings.Profile> convertToSettingsProfiles(Collection<org.apache.maven.model.Profile> modelProfiles)
	{
		return modelProfiles.stream().map(ProfileConverter::convertToSettingsProfile).collect(toList());
	}

}
