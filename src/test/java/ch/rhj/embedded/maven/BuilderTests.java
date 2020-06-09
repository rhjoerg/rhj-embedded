package ch.rhj.embedded.maven;

import static ch.rhj.embedded.maven.MavenTestsConstants.EMBEDDED_POM;
import static ch.rhj.embedded.maven.MavenTestsConstants.PLUGIN_POM;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.maven.tools.plugin.extractor.MojoDescriptorExtractor;
import org.apache.maven.tools.plugin.scanner.DefaultMojoScanner;
import org.apache.maven.tools.plugin.scanner.MojoScanner;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.util.ReflectionUtils;

@WithMaven
public class BuilderTests
{
	@Inject
	private Builder builder;

	@Inject
	private DefaultPlexusContainer container;

	@Inject
	private Map<String, MojoDescriptorExtractor> extractors;

	@Test
	public void testValidateEmbedded() throws Exception
	{
		System.out.println("--- testValidateEmbedded ---");
		builder.build(EMBEDDED_POM, "validate");
	}

	@Test
	public void testVerifyPlugin() throws Exception
	{
		System.out.println("--- testVerifyPlugin ---");
		builder.build(PLUGIN_POM, "clean", "verify");
	}

	@Test
	public void testMojoDescriptorExtractor() throws Exception
	{
		System.out.println("--- testMojoDescriptorExtractor ---");

		TreeSet<String> expectedIds = new TreeSet<>(Set.of("java-javadoc", "java-annotations"));
		TreeSet<String> actualIds = new TreeSet<>(extractors.keySet());

		assertEquals(expectedIds, actualIds);

		extractors.get("java-annotations");

		DefaultMojoScanner scanner = (DefaultMojoScanner) container.lookup(MojoScanner.class);
		Try<Object> extractorsTry = ReflectionUtils.tryToReadFieldValue(DefaultMojoScanner.class, "mojoDescriptorExtractors", scanner);

		@SuppressWarnings("unchecked")
		Map<String, MojoDescriptorExtractor> scannerExtractors = (Map<String, MojoDescriptorExtractor>) extractorsTry.get();

		assertEquals(2, scannerExtractors.size());
		actualIds = new TreeSet<String>(scannerExtractors.keySet());
		assertEquals(expectedIds, actualIds);

		scannerExtractors.get("java-annotations");
	}
}
