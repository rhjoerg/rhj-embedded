package ch.rhj.embedded.maven.factory.profile;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.model.Model;
import org.apache.maven.settings.Settings;

public class ProfilesResult
{
	private final Settings settings;
	private final Model model;

	private final ArrayList<org.apache.maven.settings.Profile> settingsProfiles = new ArrayList<>();
	private final ArrayList<org.apache.maven.model.Profile> modelProfiles = new ArrayList<>();

	private final TreeSet<String> activeProfiles = new TreeSet<String>();

	public ProfilesResult(Settings settings, Model model)
	{
		this.settings = settings;
		this.model = model;
	}

	public Settings settings()
	{
		return settings;
	}

	public Model model()
	{
		return model;
	}

	public Set<String> activeProfiles()
	{
		return unmodifiableSet(activeProfiles);
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
		result.addAll(Profiles.toSettingsProfiles(modelProfiles));

		return result;
	}

	public List<org.apache.maven.model.Profile> allAsModelProfiles()
	{
		ArrayList<org.apache.maven.model.Profile> result = new ArrayList<>();

		result.addAll(Profiles.toModelProfiles(settingsProfiles));
		result.addAll(modelProfiles);

		return result;
	}

	public void add(org.apache.maven.settings.Profile settingsProfile, boolean active)
	{
		settingsProfiles.add(settingsProfile);

		if (active)
		{
			activeProfiles.add(settingsProfile.getId());
		}
	}

	public void add(org.apache.maven.model.Profile modelProfile, boolean active)
	{
		modelProfiles.add(modelProfile);

		if (active)
		{
			activeProfiles.add(modelProfile.getId());
		}
	}
}
