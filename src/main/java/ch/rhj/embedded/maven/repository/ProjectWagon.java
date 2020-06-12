package ch.rhj.embedded.maven.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.events.SessionListener;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.repository.Repository;

@Named("project")
@SuppressWarnings("deprecation")
public class ProjectWagon implements Wagon
{
	private final ThreadLocal<Repository> repositories = new ThreadLocal<>();

	private final List<TransferListener> transferListeners = new ArrayList<>();

	private final String url;

	@Inject
	public ProjectWagon(ProjectWorkspaceReader workspaceReader)
	{
		this.url = workspaceReader.getUrl();
	}

	@Override
	public void get(String resourceName, File destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
	{
		System.out.println("GET: " + resourceName);

		throw new ResourceDoesNotExistException(resourceName);
	}

	@Override
	public boolean getIfNewer(String resourceName, File destination, long timestamp)
			throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void put(File source, String destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void putDirectory(File sourceDirectory, String destinationDirectory)
			throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public boolean resourceExists(String resourceName) throws TransferFailedException, AuthorizationException
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public List<String> getFileList(String destinationDirectory) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public boolean supportsDirectoryCopy()
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public Repository getRepository()
	{
		return repositories.get();
	}

	@Override
	public void connect(Repository source, AuthenticationInfo authenticationInfo, ProxyInfo proxyInfo) throws ConnectionException, AuthenticationException
	{
		if (source.getUrl().startsWith(url))
		{
			repositories.set(source);
		}
	}

	@Override
	public void connect(Repository source) throws ConnectionException, AuthenticationException
	{
		AuthenticationInfo authenticationInfo = null;
		ProxyInfo proxyInfo = null;

		connect(source, authenticationInfo, proxyInfo);
	}

	@Override
	public void connect(Repository source, ProxyInfo proxyInfo) throws ConnectionException, AuthenticationException
	{
		AuthenticationInfo authenticationInfo = null;

		connect(source, authenticationInfo, proxyInfo);
	}

	@Override
	public void connect(Repository source, ProxyInfoProvider proxyInfoProvider) throws ConnectionException, AuthenticationException
	{
		AuthenticationInfo authenticationInfo = null;

		connect(source, authenticationInfo, proxyInfoProvider);
	}

	@Override
	public void connect(Repository source, AuthenticationInfo authenticationInfo) throws ConnectionException, AuthenticationException
	{
		ProxyInfo proxyInfo = null;

		connect(source, authenticationInfo, proxyInfo);
	}

	@Override
	public void connect(Repository source, AuthenticationInfo authenticationInfo, ProxyInfoProvider proxyInfoProvider)
			throws ConnectionException, AuthenticationException
	{
		ProxyInfo proxyInfo = null;

		connect(source, authenticationInfo, proxyInfo);
	}

	@Override
	@Deprecated
	public void openConnection() throws ConnectionException, AuthenticationException
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void disconnect() throws ConnectionException
	{
		repositories.set(null);
	}

	@Override
	public void setTimeout(int timeoutValue)
	{
		// ignored
	}

	@Override
	public int getTimeout()
	{
		return 0;
	}

	@Override
	public void setReadTimeout(int timeoutValue)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public int getReadTimeout()
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void addSessionListener(SessionListener listener)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void removeSessionListener(SessionListener listener)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public boolean hasSessionListener(SessionListener listener)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void addTransferListener(TransferListener listener)
	{
		synchronized (transferListeners)
		{
			transferListeners.add(listener);
		}
	}

	@Override
	public void removeTransferListener(TransferListener listener)
	{
		synchronized (transferListeners)
		{
			transferListeners.remove(listener);
		}
	}

	@Override
	public boolean hasTransferListener(TransferListener listener)
	{
		synchronized (transferListeners)
		{
			return transferListeners.contains(listener);
		}
	}

	@Override
	public boolean isInteractive()
	{
		return false;
	}

	@Override
	public void setInteractive(boolean interactive)
	{
		// ignored
	}

}
