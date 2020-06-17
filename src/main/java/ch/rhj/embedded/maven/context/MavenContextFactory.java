package ch.rhj.embedded.maven.context;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;

import ch.rhj.embedded.maven.config.ProfilesConfigurator;
import ch.rhj.embedded.maven.config.PropertiesConfigurator;
import ch.rhj.embedded.maven.config.SettingsConfigurator;

@Named
public class MavenContextFactory
{
	private final PropertiesConfigurator propertiesConfigurator;
	private final SettingsConfigurator settingsConfigurator;
	private final ProfilesConfigurator profilesConfigurator;

	@Inject
	public MavenContextFactory //
	( //
			PropertiesConfigurator propertiesConfigurator, //
			SettingsConfigurator settingsConfigurator, //
			ProfilesConfigurator profilesConfigurator //
	)
	{
		this.propertiesConfigurator = propertiesConfigurator;
		this.settingsConfigurator = settingsConfigurator;
		this.profilesConfigurator = profilesConfigurator;
	}

	public MavenContext createContext(Path pomPath, String... goals) throws Exception
	{
		MavenContext context = new MavenContext(pomPath, goals);

		propertiesConfigurator.configure(context);
		settingsConfigurator.configure(context);
		profilesConfigurator.configure(context);

		return context;
	}
}
