package com.nattguld.tasker;

import java.util.Objects;

import com.nattguld.http.ConnectionPolicy;
import com.nattguld.http.HttpClient;
import com.nattguld.http.browser.Browser;
import com.nattguld.http.proxies.HttpProxy;
import com.nattguld.http.proxies.ProxyManager;
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
		
		disposeClient();
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
		HttpProxy proxy = Objects.nonNull(getProxy()) ? getProxy()
				: ProxyManager.getProxyByPreference(getProxyChoices(), getIdentifier(), isIgnoreUsers(), isIgnoreProxyCooldowns());
		
		if (Objects.nonNull(getProxy()) && proxy == ProxyManager.INVALID_PROXY) {
			System.err.println(getClass().getSimpleName() + ": Failed to get proxy to use");
			return false;
		}
		HttpClient c = new HttpClient(getBrowser(), proxy, getClientPolicies());
		c.initProxies(getIdentifier());
		
		setClient(c);
		
		if (Objects.isNull(getClient())) {
			System.err.println(getClass().getSimpleName() + " Failed to initialize client");
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
	 * Disposes the client in use.
	 */
	protected void disposeClient(boolean rebuild) {
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
	 * Whether to ignore proxy cooldowns or not.
	 * 
	 * @return The result.
	 */
	protected boolean isIgnoreProxyCooldowns() {
		return true;
	}
	
	/**
	 * Whether to ignore users or not.
	 * 
	 * @return The result.
	 */
	protected boolean isIgnoreUsers() {
		return true;
	}
	
	/**
	 * Retrieves the identifier.
	 * 
	 * @return The identifier.
	 */
	protected String getIdentifier() {
		return getClass().getSimpleName();
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
