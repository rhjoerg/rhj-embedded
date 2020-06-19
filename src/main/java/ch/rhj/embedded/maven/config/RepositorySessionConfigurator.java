package ch.rhj.embedded.maven.config;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.bridge.MavenRepositorySystem;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.ConfigurationProperties;
import org.eclipse.aether.DefaultRepositoryCache;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.DefaultSessionData;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.artifact.DefaultArtifactType;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.MirrorSelector;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.resolution.ResolutionErrorPolicy;
import org.eclipse.aether.util.artifact.DefaultArtifactTypeRegistry;
import org.eclipse.aether.util.graph.manager.ClassicDependencyManager;
import org.eclipse.aether.util.graph.selector.AndDependencySelector;
import org.eclipse.aether.util.graph.selector.ExclusionDependencySelector;
import org.eclipse.aether.util.graph.selector.OptionalDependencySelector;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.eclipse.aether.util.graph.transformer.ChainedDependencyGraphTransformer;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;
import org.eclipse.aether.util.graph.transformer.JavaDependencyContextRefiner;
import org.eclipse.aether.util.graph.transformer.JavaScopeDeriver;
import org.eclipse.aether.util.graph.transformer.JavaScopeSelector;
import org.eclipse.aether.util.graph.transformer.NearestVersionSelector;
import org.eclipse.aether.util.graph.transformer.SimpleOptionalitySelector;
import org.eclipse.aether.util.graph.traverser.FatArtifactTraverser;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.eclipse.aether.util.repository.DefaultAuthenticationSelector;
import org.eclipse.aether.util.repository.DefaultMirrorSelector;
import org.eclipse.aether.util.repository.DefaultProxySelector;
import org.eclipse.aether.util.repository.SimpleArtifactDescriptorPolicy;
import org.eclipse.aether.util.repository.SimpleResolutionErrorPolicy;

import ch.rhj.embedded.maven.context.ContextRepositories;
import ch.rhj.embedded.maven.context.MavenContext;

@Named
public class RepositorySessionConfigurator implements MavenConfigurator
{
	private final ArtifactHandlerManager artifactHandlerManager;
	private final RepositorySystem repositorySystem;
	private final MavenRepositorySystem mavenRepositorySystem;

	@Inject
	public RepositorySessionConfigurator(ArtifactHandlerManager artifactHandlerManager, RepositorySystem repositorySystem,
			MavenRepositorySystem mavenRepositorySystem)
	{
		this.artifactHandlerManager = artifactHandlerManager;
		this.repositorySystem = repositorySystem;
		this.mavenRepositorySystem = mavenRepositorySystem;
	}

	@Override
	public List<Integer> positions()
	{
		return ConfiguratorPositions.REPOSITORY_SESSION_CONFIGURATOR_POSITIONS;
	}

	@Override
	public void configure(MavenContext context) throws Exception
	{
		DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
		Map<Object, Object> config = createConfig(context);

		session.setData(new DefaultSessionData());

		session.setDependencyTraverser(new FatArtifactTraverser());
		session.setDependencyManager(new ClassicDependencyManager());
		session.setDependencySelector(createDependencySelector());
		session.setDependencyGraphTransformer(createDependencyGraphTransformer());

		session.setArtifactTypeRegistry(createArtifactTypeRegistry());
		session.setArtifactDescriptorPolicy(new SimpleArtifactDescriptorPolicy(true, true));

		session.setSystemProperties(context.systemProperties());
		session.setUserProperties(context.userProperties());

		session.setCache(new DefaultRepositoryCache());
		session.setResolutionErrorPolicy(new SimpleResolutionErrorPolicy(ResolutionErrorPolicy.CACHE_DISABLED));

		session.setArtifactTypeRegistry(RepositoryUtils.newArtifactTypeRegistry(artifactHandlerManager));
		session.setLocalRepositoryManager(createLocalRepositoryManager(context, session));

		session.setMirrorSelector(createMirrorSelector(context));
		session.setProxySelector(createProxySelector(context));
		session.setAuthenticationSelector(createAuthenticationSelector(context));

		session.setConfigProperties(config);

		ContextRepositories repositories = context.repositories();
		Settings settings = context.settings();

		mavenRepositorySystem.injectMirror(repositories.remoteRepositories(), settings.getMirrors());
		mavenRepositorySystem.injectProxy(session, repositories.remoteRepositories());
		mavenRepositorySystem.injectAuthentication(session, repositories.remoteRepositories());

		mavenRepositorySystem.injectMirror(repositories.pluginRepositories(), settings.getMirrors());
		mavenRepositorySystem.injectProxy(session, repositories.pluginRepositories());
		mavenRepositorySystem.injectAuthentication(session, repositories.pluginRepositories());

		context.repositorySession(session);
	}

	private Map<Object, Object> createConfig(MavenContext context) throws Exception
	{
		LinkedHashMap<Object, Object> config = new LinkedHashMap<>();

		config.put(ConfigurationProperties.USER_AGENT, getUserAgent());
		config.put(ConfigurationProperties.INTERACTIVE, false);

		config.putAll(context.systemProperties());
		config.putAll(context.userProperties());

		return config;
	}

	private String getUserAgent() throws Exception
	{
		String mavenVersion = getMavenVersion();
		String javaVersion = System.getProperty("java.version");
		String osName = System.getProperty("os.name");
		String osVersion = System.getProperty("os.version");

		return String.format("Apache-Maven/%1$s (Java %2$s; %3$s %4$s)", mavenVersion, javaVersion, osName, osVersion);
	}

	private String getMavenVersion() throws Exception
	{
		Properties props = new Properties();

		try (InputStream is = getClass().getResourceAsStream("/META-INF/maven/org.apache.maven/maven-core/pom.properties"))
		{
			if (is != null)
			{
				props.load(is);
			}
		}

		return props.getProperty("version", "unknown-version");
	}

	private DependencySelector createDependencySelector()
	{
		ScopeDependencySelector scopeSelector = new ScopeDependencySelector("test", "provided");
		OptionalDependencySelector optionalSelector = new OptionalDependencySelector();
		ExclusionDependencySelector exclusionSelector = new ExclusionDependencySelector();

		return new AndDependencySelector(scopeSelector, optionalSelector, exclusionSelector);
	}

	private DependencyGraphTransformer createDependencyGraphTransformer()
	{
		NearestVersionSelector versionSelector = new NearestVersionSelector();
		JavaScopeSelector scopeSelector = new JavaScopeSelector();
		SimpleOptionalitySelector optionalitySelector = new SimpleOptionalitySelector();
		JavaScopeDeriver scopeDeriver = new JavaScopeDeriver();
		ConflictResolver conflictResolver = new ConflictResolver(versionSelector, scopeSelector, optionalitySelector, scopeDeriver);

		return new ChainedDependencyGraphTransformer(conflictResolver, new JavaDependencyContextRefiner());
	}

	private ArtifactTypeRegistry createArtifactTypeRegistry()
	{
		DefaultArtifactTypeRegistry registry = new DefaultArtifactTypeRegistry();

		registry.add(new DefaultArtifactType("pom"));
		registry.add(new DefaultArtifactType("maven-plugin", "jar", "", "java"));
		registry.add(new DefaultArtifactType("jar", "jar", "", "java"));
		registry.add(new DefaultArtifactType("ejb", "jar", "", "java"));
		registry.add(new DefaultArtifactType("ejb-client", "jar", "client", "java"));
		registry.add(new DefaultArtifactType("test-jar", "jar", "tests", "java"));
		registry.add(new DefaultArtifactType("javadoc", "jar", "javadoc", "java"));
		registry.add(new DefaultArtifactType("java-source", "jar", "sources", "java", false, false));
		registry.add(new DefaultArtifactType("war", "war", "", "java", false, true));
		registry.add(new DefaultArtifactType("ear", "ear", "", "java", false, true));
		registry.add(new DefaultArtifactType("rar", "rar", "", "java", false, true));
		registry.add(new DefaultArtifactType("par", "par", "", "java", false, true));

		return registry;
	}

	private LocalRepositoryManager createLocalRepositoryManager(MavenContext context, RepositorySystemSession session)
	{
		LocalRepository localRepository = new LocalRepository(context.repositories().localRepository().getBasedir());

		return repositorySystem.newLocalRepositoryManager(session, localRepository);
	}

	private MirrorSelector createMirrorSelector(MavenContext context)
	{
		DefaultMirrorSelector selector = new DefaultMirrorSelector();
		Settings settings = context.settings();

		for (Mirror m : settings.getMirrors())
		{
			selector.add(m.getId(), m.getUrl(), m.getLayout(), false, m.getMirrorOf(), m.getMirrorOfLayouts());
		}

		return selector;
	}

	private ProxySelector createProxySelector(MavenContext context)
	{
		DefaultProxySelector selector = new DefaultProxySelector();
		Settings settings = context.settings();

		for (Proxy p : settings.getProxies())
		{
			AuthenticationBuilder authenticationBuilder = new AuthenticationBuilder();
			Authentication authentication = authenticationBuilder.addUsername(p.getUsername()).addPassword(p.getPassword()).build();

			selector.add(new org.eclipse.aether.repository.Proxy(p.getProtocol(), p.getHost(), p.getPort(), authentication), p.getNonProxyHosts());
		}

		return selector;
	}

	private AuthenticationSelector createAuthenticationSelector(MavenContext context)
	{
		DefaultAuthenticationSelector selector = new DefaultAuthenticationSelector();
		Settings settings = context.settings();

		for (Server server : settings.getServers())
		{
			AuthenticationBuilder authenticationBuilder = new AuthenticationBuilder();

			authenticationBuilder.addUsername(server.getUsername()).addPassword(server.getPassword());
			authenticationBuilder.addPrivateKey(server.getPrivateKey(), server.getPassphrase());

			selector.add(server.getId(), authenticationBuilder.build());
		}

		return selector;
	}
}
