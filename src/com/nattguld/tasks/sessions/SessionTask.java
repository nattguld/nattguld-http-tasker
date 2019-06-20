package com.nattguld.tasks.sessions;

import java.util.Objects;

import com.nattguld.Session;
import com.nattguld.http.browser.Browser;
import com.nattguld.http.proxies.ProxyManager;
import com.nattguld.http.proxies.cfg.ProxyChoice;
import com.nattguld.http.proxies.cfg.ProxyConfig;
import com.nattguld.tasker.steps.Step;
import com.nattguld.tasks.NetStepTask;

/**
 * 
 * @author randqm
 *
 */

public abstract class SessionTask<S extends Session> extends NetStepTask {
	
	/**
	 * The session instance.
	 */
	private final S session;
	
	/**
	 * The proxy choice.
	 */
	private final ProxyChoice proxyChoice;

	
	/**
	 * Creates a new task.
	 * 
	 * @param session The session.
	 * 
	 * @param proxyChoice The proxy choice.
	 * 
	 * @param name The name of the task.
	 * 
	 * @param maxReAttempts The maximum allowed re-attempts on failed execute.
	 */
	public SessionTask(S session, ProxyChoice proxyChoice, String name) {
		super(name, session.getProxy());
		
		this.session = session;
		this.proxyChoice = getProxyChoice(proxyChoice);
	}
	
	@Override
	protected boolean buildClient() {
		boolean built = super.buildClient();
		
		if (!built) {
			return false;
		}
		getClient().getCookieJar().importCookies(getSession().getCookies());
		return true;
	}
	
	/**
	 * Disposes the session.
	 */
	protected void disposeSession() {
		if (Objects.nonNull(getClient())) {
			session.getCookies().clear();
			session.getCookies().addAll(getClient().getCookieJar().getCookies());
		}
	}
	
	@Override
	protected void disposeClient() {
		disposeSession();
		
		super.disposeClient();
	}
	
	@Override
	protected void onStepFail(Step step) {
		super.onStepFail(step);
		
		resetSession();
	}
	
	/**
	 * Resets the account session.
	 */
	protected void resetSession() {
		getSession().getCookies().clear();
	}
	
	@Override
	protected Browser getBrowser() {
		return getSession().getBrowser();
	}
	
	@Override
	protected ProxyChoice[] getProxyChoices() {
		return new ProxyChoice[] {
				proxyChoice
		};
	}
	
	/**
	 * Retrieves the session.
	 * 
	 * @return The session.
	 */
	protected S getSession() {
		return session;
	}
	
	/**
	 * Retrieves the proxy choice to use.
	 * 
	 * @param original The original choice.
	 * 
	 * @return The proxy choice.
	 */
	private static ProxyChoice getProxyChoice(ProxyChoice original) {
		if (ProxyConfig.getConfig().isCellularMode()) {
			return ProxyChoice.DIRECT;
		}
		return Objects.isNull(original) ? ProxyManager.findBestChoice() : original;
	}

}
