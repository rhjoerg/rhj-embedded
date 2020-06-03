package ch.rhj.embedded.maven.configuration;

import java.io.File;

import org.apache.maven.wagon.AbstractWagon;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;

public class ProjectWagon extends AbstractWagon
{
	@Override
	public void get(String resourceName, File destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
	{
		// does nothing
	}

	@Override
	public boolean getIfNewer(String resourceName, File destination, long timestamp)
			throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
	{
		return true;
	}

	@Override
	public void put(File source, String destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected void openConnectionInternal() throws ConnectionException, AuthenticationException
	{
		// does nothing
	}

	@Override
	protected void closeConnection() throws ConnectionException
	{
		// does nothing
	}
}
