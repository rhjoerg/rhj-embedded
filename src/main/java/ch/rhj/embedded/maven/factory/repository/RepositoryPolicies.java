package ch.rhj.embedded.maven.factory.repository;

import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE;
import static org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;

import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;

public interface RepositoryPolicies
{
	public static ArtifactRepositoryPolicy createDefaultPolicy()
	{
		return new ArtifactRepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE);
	}
}
