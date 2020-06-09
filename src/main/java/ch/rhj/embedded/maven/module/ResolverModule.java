package ch.rhj.embedded.maven.module;

import javax.inject.Singleton;

import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.apache.maven.repository.internal.SnapshotMetadataGeneratorFactory;
import org.apache.maven.repository.internal.VersionsMetadataGeneratorFactory;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.impl.guice.AetherModule;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ResolverModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		install(new AetherModule());

		bind(ArtifactDescriptorReader.class).to(DefaultArtifactDescriptorReader.class).in(Singleton.class);
		bind(VersionResolver.class).to(DefaultVersionResolver.class).in(Singleton.class);
		bind(VersionRangeResolver.class).to(DefaultVersionRangeResolver.class).in(Singleton.class);
		bind(MetadataGeneratorFactory.class).annotatedWith(Names.named("snapshot")).to(SnapshotMetadataGeneratorFactory.class).in(Singleton.class);
		bind(MetadataGeneratorFactory.class).annotatedWith(Names.named("versions")).to(VersionsMetadataGeneratorFactory.class).in(Singleton.class);
	}
}
