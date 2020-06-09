package ch.rhj.embedded.maven.realm;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import org.apache.maven.classrealm.ClassRealmManager;
import org.apache.maven.extension.internal.CoreExportsProvider;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.eclipse.aether.artifact.Artifact;

//@Named
//@Singleton
public class ParentFirstClassRealmManager implements ClassRealmManager
{
	public static final String API_REALMID = "maven.api";

	public static final ClassLoader PARENT_CLASSLOADER = ClassWorld.class.getClassLoader();

	private final ClassWorld world;

	private final ClassRealm coreRealm;

	private final ClassRealm mavenApiRealm;

	@Inject
	public ParentFirstClassRealmManager(MutablePlexusContainer container, CoreExportsProvider exports)
	{
		this.world = container.getClassWorld();
		this.coreRealm = container.getContainerRealm();

//		Plexi.setParentFirstStrategy(coreRealm);

		Map<String, ClassLoader> foreignImports = exports.get().getExportedPackages();

		this.mavenApiRealm = createRealm(API_REALMID, null, null, null, foreignImports, null);
	}

	@Override
	public ClassRealm getCoreRealm()
	{
		return coreRealm;
	}

	@Override
	public ClassRealm getMavenApiRealm()
	{
		return mavenApiRealm;
	}

	@Override
	public ClassRealm createProjectRealm(Model model, List<Artifact> artifacts)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public ClassRealm createExtensionRealm(Plugin extension, List<Artifact> artifacts)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public ClassRealm createPluginRealm(Plugin plugin, ClassLoader parent, List<String> parentImports, Map<String, ClassLoader> foreignImports,
			List<Artifact> artifacts)
	{
		return createRealm(getKey(plugin), null, parent, parentImports, foreignImports, artifacts);
	}

	private ClassRealm createRealm(String id, ClassRealm parentRealm, ClassLoader parentLoader, List<String> parentImports,
			Map<String, ClassLoader> foreignImports, List<Artifact> artifacts)
	{
		ClassRealm realm = newRealm(id);

		setParents(realm, parentRealm, parentLoader);
		setImports(realm, parentImports, foreignImports);
		setUrls(realm, artifacts);

		return realm;
	}

	private void setParents(ClassRealm realm, ClassRealm parentRealm, ClassLoader parentLoader)
	{
		parentRealm = parentRealm == null ? coreRealm : parentRealm;
		parentLoader = parentLoader == null ? PARENT_CLASSLOADER : parentLoader;

		realm.setParentRealm(parentRealm);
		realm.setParentClassLoader(parentLoader);
	}

	private void setImports(ClassRealm realm, List<String> parentImports, Map<String, ClassLoader> foreignImports)
	{
		parentImports = parentImports == null ? List.of() : parentImports;
		foreignImports = foreignImports == null ? Map.of() : foreignImports;

		parentImports.forEach(i -> realm.importFromParent(i));
		foreignImports.forEach((s, c) -> realm.importFrom(c, s));
	}

	private void setUrls(ClassRealm realm, List<Artifact> artifacts)
	{
		artifacts = artifacts == null ? List.of() : artifacts;
		artifacts.stream().filter(a -> a.getFile() != null).map(this::getUrl).forEach(u -> realm.addURL(u));
	}

	private ClassRealm newRealm(String id)
	{
		synchronized (world)
		{
			String realmId = id;
			Random random = new Random();

			while (true)
			{
				try
				{
					ClassRealm realm = world.newRealm(realmId, null);

//					Plexi.setParentFirstStrategy(realm);

					return realm;
				}
				catch (DuplicateRealmException e)
				{
					realmId = id + '-' + random.nextInt();
				}
			}
		}
	}

	private String getKey(Plugin plugin)
	{
		return "plugin>" + plugin.getKey() + ":" + plugin.getVersion();
	}

	private URL getUrl(Artifact artifact)
	{
		try
		{
			return artifact.getFile().toURI().toURL();
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}
}
