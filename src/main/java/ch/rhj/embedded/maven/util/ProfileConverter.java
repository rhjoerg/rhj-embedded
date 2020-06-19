package ch.rhj.embedded.maven.util;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import org.apache.maven.settings.SettingsUtils;

public interface ProfileConverter
{
	public static org.apache.maven.model.Profile convertToModelProfile(org.apache.maven.settings.Profile settingsProfile)
	{
		return SettingsUtils.convertFromSettingsProfile(settingsProfile);
	}

	public static List<org.apache.maven.model.Profile> convertToModelProfiles(Collection<org.apache.maven.settings.Profile> settingsProfiles)
	{
		return settingsProfiles.stream().map(ProfileConverter::convertToModelProfile).collect(toList());
	}

	public static org.apache.maven.settings.Profile convertToSettingsProfile(org.apache.maven.model.Profile modelProfile)
	{
		return SettingsUtils.convertToSettingsProfile(modelProfile);
	}

	public static List<org.apache.maven.settings.Profile> convertToSettingsProfiles(Collection<org.apache.maven.model.Profile> modelProfiles)
	{
		return modelProfiles.stream().map(ProfileConverter::convertToSettingsProfile).collect(toList());
	}

}
