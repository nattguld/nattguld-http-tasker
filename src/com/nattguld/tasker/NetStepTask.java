package com.nattguld.tasker;

import java.util.Objects;

import com.nattguld.http.ConnectionPolicy;
import com.nattguld.http.HttpClient;
import com.nattguld.http.browser.Browser;
import com.nattguld.http.cfg.SessionConfig;
import com.nattguld.http.proxies.HttpProxy;
import com.nattguld.http.proxies.ProxyManager;
import com.nattguld.http.proxies.ProxyState;
import com.nattguld.http.proxies.cfg.ProxyChoice;
import com.nattguld.http.proxies.cfg.ProxyConfig;
import com.nattguld.tasker.steps.Step;
import com.nattguld.tasker.steps.StepState;
import com.nattguld.tasker.tasks.impl.StepTask;

/**
 * 
 * @author randqm
 *
 */

public abstract class NetStepTask extends StepTask {
	
	/**
	 * The proxy bound to this task.
	 */
	private final HttpProxy proxy;
	
	/**
	 * The client to use.
	 */
	private HttpClient c;
	
	/**
	 * Whether an external client is assigned or not.
	 */
	private boolean externalClient;
	

	/**
	 * Creates a new network flow.
	 * 
	 * @param name The name of the flow.
	 */
	public NetStepTask(String name) {
		this(name, null);
	}
	
	/**
	 * Creates a new network flow.
	 * 
	 * @param name The name of the flow.
	 * 
	 * @param proxy The proxy bound to this task.
	 */
	public NetStepTask(String name, HttpProxy proxy) {
		super(name);
		
		this.proxy = proxy;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		addFirst(new Step("Building client") {
			@Override
			public StepState execute() {
				setStatus("Building client");
				
				if (!buildClient()) {
					setStatus("Failed to build client");
					return StepState.CANCEL;
				}
				setStatus("Client built");
				return StepState.SUCCESS;
			}
		});
	}
	
	@Override
	protected void onFinish() {
		super.onFinish();
		
		if (!externalClient) {
			disposeClient();
		}
	}
	
	@Override
	protected void onException(Step step, Exception ex) {
		super.onException(step, ex);
		
		if (step.isCritical()) {
			disposeClient();
		}
	}
	
	/**
	 * Builds the client session.
	 * 
	 * @return Whether the client session was built successfully or not.
	 */
	protected boolean buildClient() {
		if (!keepAlive()) {
			disposeClient();
		}
		if (Objects.nonNull(getClient())) {
			return true;
		}
		HttpProxy proxy = buildProxy();
		
		if (Objects.isNull(proxy)) {
			if (Objects.nonNull(getProxy())) {
				System.err.println(getClass().getName() + ": Failed to retrieve proxy to use (Max. connections reached)");
				return false;
			}
			if (!hasProxyChoice(ProxyChoice.DIRECT)) {
				System.err.println(getClass().getName() + ": Failed to retrieve proxy to use");
				return false;
			}
		}
		if (Objects.nonNull(proxy)) {
			if (proxy == ProxyManager.INVALID_PROXY) {
				System.err.println(getClass().getName() + ": Invalid proxy received");
				return false;
			}
			if (!SessionConfig.getConfig().isAllowFlaggedProxies() 
					&& (proxy.getState() == ProxyState.GHOSTED || proxy.getState() == ProxyState.BLACKLISTED)) {
				System.err.println(getClass().getName() + ": Invalid proxy state (" + proxy.getState().getName() + ")");
				onFlaggedProxy(proxy);
				return false;
			}
		}
		HttpClient c = new HttpClient(getBrowser(), proxy, getClientPolicies());
		c.initProxies(getIdentifier());
		
		setClient(c);
		
		if (Objects.isNull(getClient())) {
			System.err.println(getClass().getName() + " Failed to initialize client");
			return false;
		}
		if (ProxyConfig.getConfig().isFiddler()) {
			HttpClient fiddlerClient = new HttpClient(c.getBrowser(), ProxyManager.FIDDLER_PROXY);
			fiddlerClient.getCookieJar().importCookies(c.getCookieJar().getCookies());
			setClient(fiddlerClient);
		}
		return true;
	}
	
	/**
	 * Handles what to do when a flagged proxy is encountered.
	 * 
	 * @param proxy The proxy.
	 */
	protected void onFlaggedProxy(HttpProxy proxy) {
		//To override when required
	}
	
	/**
	 * Retrieves the proxy to use.
	 * 
	 * @return The proxy.
	 */
	protected HttpProxy buildProxy() {
		if (Objects.nonNull(getProxy()) && getProxy() != ProxyManager.INVALID_PROXY && getProxy() != ProxyManager.LOCALHOST) {
			if (!getProxy().getLocalConfig().canAddUser(getIdentifier(), isUniqueProxyUser())) {
				System.err.println(getClass().getName() + " Cant add user to proxy at this time");
				return ProxyManager.INVALID_PROXY;
			}
			return getProxy();
		}
		return ProxyManager.getProxyByChoices(getProxyChoices(), getIdentifier(), isUniqueProxyUser());
	}
	
	/**
	 * Disposes the client in use.
	 */
	protected void disposeClient(boolean rebuild) {
		if (externalClient) {
			return;
		}
		if (Objects.nonNull(c)) {
			c.close();
			setClient(null);
		}
		if (rebuild) {
			buildClient();
		}
	}
	
	/**
	 * Disposes the client in use.
	 */
	protected void disposeClient() {
		disposeClient(false);
	}
	
	/**
	 * Modifies the client to use.
	 * 
	 * @param c The new client instance.
	 */
	protected void setClient(HttpClient c) {
		this.c = c;
	}
	
	/**
	 * Assigns an external client.
	 * 
	 * @param c The external client.
	 */
	public NetStepTask assignExternalClient(HttpClient c) {
		this.c = c;
		this.externalClient = true;
		return this;
	}
	
	/**
	 * Retrieves the client to use.
	 * 
	 * @return The client to use.
	 */
	public HttpClient getClient() {
		return c;
	}
	
	/**
	 * Whether to keep the connection alive throughout resets or not.
	 * 
	 * @return The result.
	 */
	protected boolean keepAlive() {
		return false;
	}
	
	/**
	 * Whether to ignore users or not.
	 * 
	 * @return The result.
	 */
	protected boolean isUniqueProxyUser() {
		return false;
	}
	
	/**
	 * Retrieves the identifier.
	 * 
	 * @return The identifier.
	 */
	protected String getIdentifier() {
		return getClass().getName();
	}

	/**
	 * Retrieves the user agent.
	 * 
	 * @return The user agent.
	 */
	protected Browser getBrowser() {
		return new Browser();
	}
	
	/**
	 * Retrieves the proxy choices.
	 * 
	 * @return The proxy choices.
	 */
	protected abstract ProxyChoice[] getProxyChoices();
	
	/**
	 * Retrieves whether a proxy choice is available or not.
	 * 
	 * @param proxyChoice The proxy choice.
	 * 
	 * @return The result.
	 */
	protected boolean hasProxyChoice(ProxyChoice proxyChoice) {
		if (Objects.isNull(getProxyChoices())) {
			return false;
		}
		for (ProxyChoice pc : getProxyChoices()) {
			if (pc == proxyChoice) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Retrieves the client properties.
	 * 
	 * @return The client properties.
	 */
	protected ConnectionPolicy[] getClientPolicies() {
		return null;
	}
	
	/**
	 * Retrieves the proxy bound to the task if any.
	 * 
	 * @return The task proxy.
	 */
	public HttpProxy getProxy() {
		return proxy;
	}

}
