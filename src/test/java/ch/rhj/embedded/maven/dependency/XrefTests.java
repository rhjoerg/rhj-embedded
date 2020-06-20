package ch.rhj.embedded.maven.dependency;

import static ch.rhj.embedded.maven.MavenTestsConstants.TEST_OUTPUT_DIRECTORY;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ch.rhj.embedded.maven.build.ProjectArchiver;
import ch.rhj.embedded.maven.build.ProjectInstaller;
import ch.rhj.embedded.maven.build.ProjectRepository;
import ch.rhj.embedded.maven.build.ProjectWorkspaceReader;
import ch.rhj.embedded.maven.config.AuthenticationsConfigurator;
import ch.rhj.embedded.maven.config.ExecutionRequestConfigurator;
import ch.rhj.embedded.maven.config.MavenSessionConfigurator;
import ch.rhj.embedded.maven.config.ProfilesConfigurator;
import ch.rhj.embedded.maven.config.ProjectConfigurator;
import ch.rhj.embedded.maven.config.ProjectRequestConfigurator;
import ch.rhj.embedded.maven.config.PropertiesConfigurator;
import ch.rhj.embedded.maven.config.RepositoriesConfigurator;
import ch.rhj.embedded.maven.config.SettingsConfigurator;
import ch.rhj.embedded.maven.context.MavenContextFactory;
import ch.rhj.embedded.maven.util.ArtifactFactory;
import ch.rhj.embedded.maven.util.SessionRunner;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;

public class XrefTests
{
	private final static Path OUTPUT_DIRECTORY = TEST_OUTPUT_DIRECTORY.resolve("XrefTests");
	private final static Path IMAGE_PATH = OUTPUT_DIRECTORY.resolve("xref.png");

	private final AtomicInteger edgeIndex = new AtomicInteger();

	private final Predicate<Class<?>> typeFilter = //
			type -> type.getPackageName().startsWith("ch.rhj.embedded.");

	private final Predicate<Constructor<?>> ctorFilter = //
			ctor -> ctor.getAnnotation(Inject.class) != null;

	@Test
	public void test() throws Exception
	{
		Dimension size = new Dimension(1200, 800);
		Dimension margins = new Dimension(280, 80);
		Point2D center = new Point2D.Double(000, 400);

		AbstractGraph<String, Integer> graph = createGraph();

		XrefLayout layout = new XrefLayout(graph, size, margins);

		while (!layout.done())
		{
			layout.step();
		}

		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16).deriveFont(AffineTransform.getTranslateInstance(0, -2));

		VisualizationImageServer<String, Integer> server = new VisualizationImageServer<String, Integer>(layout, size);
		VertexLabelAsShapeRenderer<String, Integer> renderer = new VertexLabelAsShapeRenderer<String, Integer>(server.getRenderContext());

		server.getRenderContext().setVertexLabelTransformer(s -> " " + s + " ");
		server.getRenderContext().setVertexShapeTransformer(renderer);
		server.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.WHITE));
		server.getRenderContext().setVertexFontTransformer(s -> font);
		server.getRenderer().setVertexLabelRenderer(renderer);
		server.getRenderer().setVertexRenderer(new GradientVertexRenderer<String, Integer>(Color.WHITE, Color.WHITE, false));
		server.setBackground(Color.WHITE);

		BufferedImage image = BufferedImage.class.cast(server.getImage(center, size));

		Files.deleteIfExists(IMAGE_PATH);
		Files.createDirectories(OUTPUT_DIRECTORY);
		ImageIO.write(image, "png", IMAGE_PATH.toFile());
	}

	private AbstractGraph<String, Integer> createGraph()
	{
		DirectedSparseGraph<String, Integer> graph = new DirectedSparseGraph<>();
		TreeSet<String> vertices = new TreeSet<>();

		// build

		add(graph, vertices, ProjectArchiver.class);
		add(graph, vertices, ProjectInstaller.class);
		add(graph, vertices, ProjectRepository.class);
		add(graph, vertices, ProjectWorkspaceReader.class);

		// config

		add(graph, vertices, AuthenticationsConfigurator.class);
		add(graph, vertices, ExecutionRequestConfigurator.class);
		add(graph, vertices, ProfilesConfigurator.class);
		add(graph, vertices, ProjectConfigurator.class);
		add(graph, vertices, ProjectRequestConfigurator.class);
		add(graph, vertices, PropertiesConfigurator.class);
		add(graph, vertices, RepositoriesConfigurator.class);
		add(graph, vertices, MavenSessionConfigurator.class);
		add(graph, vertices, SettingsConfigurator.class);

		// context

		add(graph, vertices, MavenContextFactory.class);

		// util

		add(graph, vertices, ArtifactFactory.class);
		add(graph, vertices, SessionRunner.class);

		for (;;)
		{
			String found = null;

			for (String vertex : graph.getVertices())
			{
				Collection<String> predecessors = graph.getPredecessors(vertex);
				Collection<String> successors = graph.getSuccessors(vertex);

				boolean hasPredecessors = predecessors != null && !predecessors.isEmpty();
				boolean hasSuccessors = successors != null && !successors.isEmpty();

				if (hasPredecessors || hasSuccessors)
				{
					continue;
				}

				found = vertex;
				break;
			}

			if (found != null)
			{
				graph.removeVertex(found);
				continue;
			}

			break;
		}

		return graph;
	}

	private void add(DirectedSparseGraph<String, Integer> graph, TreeSet<String> vertices, Class<?> type)
	{
		String name = type.getSimpleName();

		if (vertices.contains(name) || !typeFilter.test(type))
		{
			return;
		}

		graph.addVertex(name);
		vertices.add(name);

		Optional<Constructor<?>> ctor = List.of(type.getConstructors()).stream().filter(ctorFilter).findFirst();

		if (ctor.isEmpty())
		{
			return;
		}

		List.of(ctor.get().getParameterTypes()).stream().filter(typeFilter).forEach(t ->
		{
			add(graph, vertices, t);
			graph.addEdge(edgeIndex.incrementAndGet(), name, t.getSimpleName());
		});
	}

	private static class XrefLayout extends FRLayout<String, Integer>
	{
		private boolean relocated = false;

		private final Dimension fullSize;
		private final Dimension margins;

		public XrefLayout(Graph<String, Integer> graph, Dimension size, Dimension margins)
		{
			super(graph, reducedSize(size, margins));

			this.fullSize = size;
			this.margins = margins;
		}

		@Override
		public Dimension getSize()
		{
			if (relocated)
			{
				return fullSize;
			}

			return super.getSize();
		}

		@Override
		public boolean done()
		{
			return super.done() && relocated;
		}

		@Override
		public void initialize()
		{
			if (!relocated)
			{
				super.initialize();
			}
		}

		@Override
		public void reset()
		{
			if (!relocated)
			{
				super.reset();
			}
		}

		@Override
		public void step()
		{
			if (super.done())
			{
				relocate();
			}
			else
			{
				super.step();
			}
		}

		private void relocate()
		{
			Graph<String, Integer> graph = getGraph();

			for (String vertex : graph.getVertices())
			{
				Point2D location = apply(vertex);
				double x = location.getX() + margins.width;
				double y = location.getY() + margins.height;

				setLocation(vertex, x, y);
			}

			this.relocated = true;
		}

		private static Dimension reducedSize(Dimension size, Dimension margins)
		{
			int width = size.width - 2 * margins.width;
			int height = size.height - 2 * margins.height;

			return new Dimension(width, height);
		}
	}
}
