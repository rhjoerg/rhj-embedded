package ch.rhj.embedded.maven.dependency;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static org.apache.maven.RepositoryUtils.toArtifact;
import static org.apache.maven.RepositoryUtils.toDependency;

import java.util.TreeMap;

import javax.inject.Inject;

import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.impl.DependencyCollector;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;
import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.WithMaven;
import ch.rhj.embedded.maven.context.MavenContext;
import ch.rhj.embedded.maven.context.MavenContextFactory;

@WithMaven
public class DependencyTests
{
	@Inject
	private MavenContextFactory contextFactory;

	@Inject
	private DependencyCollector dependencyCollector;

	@Test
	public void test1() throws Exception
	{
		MavenContext context = contextFactory.createContext(EMBEDDED_POM, "clean", "deploy", "site");
		MavenProject project = context.project();
		CollectRequest request = new CollectRequest();

		request.setRootArtifact(toArtifact(project.getArtifact()));
		request.setRequestContext("project");
		request.setRepositories(project.getRemoteProjectRepositories());

		DefaultRepositorySystemSession session = new DefaultRepositorySystemSession(context.repositorySession());
		ArtifactTypeRegistry registry = session.getArtifactTypeRegistry();

		session.setConfigProperty(ConflictResolver.CONFIG_PROP_VERBOSE, true);
		project.getDependencies().forEach(dependency -> request.addDependency(toDependency(dependency, registry)));

		DependencyManagement management = project.getDependencyManagement();

		if (management != null)
		{
			management.getDependencies().forEach(dependency -> request.addManagedDependency(toDependency(dependency, registry)));
		}

		CollectResult result = dependencyCollector.collectDependencies(session, request);
		Visitor visitor = new Visitor();

		result.getRoot().accept(visitor);
		visitor.nodes.forEach((id, node) ->
		{
			DependencyNode winner = DependencyNode.class.cast(node.getData().get(ConflictResolver.NODE_DATA_WINNER));

			if (winner != null)
			{
				String v1 = node.getArtifact().getVersion();
				String v2 = winner.getArtifact().getVersion();

				if (!v1.equals(v2))
				{
					System.out.println(id + " replaced with " + winner.getArtifact());
				}
			}
		});
	}

	public static class Visitor implements DependencyVisitor
	{
		public final TreeMap<String, DependencyNode> nodes = new TreeMap<>();

		@Override
		public boolean visitEnter(DependencyNode node)
		{
			// String id = RepositoryUtils.toArtifact(node.getArtifact()).getId();
			String id = node.getArtifact().toString();

			nodes.put(id, node);

			return true;
		}

		@Override
		public boolean visitLeave(DependencyNode node)
		{
			return true;
		}
	}
}
