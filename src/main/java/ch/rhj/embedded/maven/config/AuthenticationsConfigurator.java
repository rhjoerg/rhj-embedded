package ch.rhj.embedded.maven.config;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Named;

import org.apache.maven.artifact.repository.Authentication;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

import ch.rhj.embedded.maven.context.ContextAuthentications;
import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class AuthenticationsConfigurator implements MavenConfigurator
{

	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.AUTHENTICATIONS_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context)
	{
		Map<String, Authentication> authenticationMap = discoverAuthentications(context.settings());
		ContextAuthentications authentications = new ContextAuthentications(authenticationMap);

		context.authentications(authentications);
	}

	private Map<String, Authentication> discoverAuthentications(Settings settings)
	{
		Map<String, Authentication> authenticationMap = new TreeMap<>();

		for (Server server : settings.getServers())
		{
			Authentication authentication = discoverAuthentication(server);

			if (authentication != null)
			{
				authenticationMap.put(server.getId(), authentication);
			}
		}

		return authenticationMap;
	}

	private Authentication discoverAuthentication(Server server)
	{
		String username = server.getUsername();
		String password = server.getPassword();
		String privateKey = server.getPrivateKey();
		String passphrase = server.getPassphrase();

		boolean valid = false;

		valid = valid || (username != null && !username.isBlank());
		valid = valid || (password != null && !password.isBlank());
		valid = valid || (privateKey != null && !privateKey.isBlank());
		valid = valid || (passphrase != null && !passphrase.isBlank());

		if (valid)
		{
			Authentication authentication = new Authentication(username, password);

			authentication.setPrivateKey(privateKey);
			authentication.setPassphrase(passphrase);

			return authentication;
		}

		return null;
	}
}
