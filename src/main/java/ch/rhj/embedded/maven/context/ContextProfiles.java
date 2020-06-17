package ch.rhj.embedded.maven.context;

import static ch.rhj.embedded.maven.util.ProfileConverter.convertToModelProfiles;
import static ch.rhj.embedded.maven.util.ProfileConverter.convertToSettingsProfiles;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ContextProfiles
{
	private final ArrayList<org.apache.maven.settings.Profile> settingsProfiles = new ArrayList<>();
	private final ArrayList<org.apache.maven.model.Profile> modelProfiles = new ArrayList<>();

	private final TreeSet<String> activeProfileIds = new TreeSet<String>();

	public List<String> activeProfileIds()
	{
		return new ArrayList<>(activeProfileIds);
	}

	public List<org.apache.maven.settings.Profile> settingsProfiles()
	{
		return unmodifiableList(settingsProfiles);
	}

	public List<org.apache.maven.model.Profile> modelProfiles()
	{
		return unmodifiableList(modelProfiles);
	}

	public List<org.apache.maven.settings.Profile> allAsSettingsProfiles()
	{
		ArrayList<org.apache.maven.settings.Profile> result = new ArrayList<>();

		result.addAll(settingsProfiles);
		result.addAll(convertToSettingsProfiles(modelProfiles));

		return result;
	}

	public List<org.apache.maven.model.Profile> allAsModelProfiles()
	{
		ArrayList<org.apache.maven.model.Profile> result = new ArrayList<>();

		result.addAll(convertToModelProfiles(settingsProfiles));
		result.addAll(modelProfiles);

		return result;
	}

	public List<org.apache.maven.settings.Profile> activeAsSettingsProfiles()
	{
		return allAsSettingsProfiles().stream().filter(p -> activeProfileIds.contains(p.getId())).collect(toList());
	}

	public List<org.apache.maven.model.Profile> activeAsModelProfiles()
	{
		return allAsModelProfiles().stream().filter(p -> activeProfileIds.contains(p.getId())).collect(toList());
	}

	public void add(org.apache.maven.settings.Profile settingsProfile, boolean active)
	{
		settingsProfiles.add(settingsProfile);

		if (active)
		{
			activeProfileIds.add(settingsProfile.getId());
		}
	}

	public void add(org.apache.maven.model.Profile modelProfile, boolean active)
	{
		modelProfiles.add(modelProfile);

		if (active)
		{
			activeProfileIds.add(modelProfile.getId());
		}
	}
}
