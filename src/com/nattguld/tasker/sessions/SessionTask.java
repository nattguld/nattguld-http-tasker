package com.nattguld.tasker.sessions;

import java.util.Objects;

import com.nattguld.http.ISession;
import com.nattguld.http.browser.Browser;
import com.nattguld.http.cfg.ProxyPolicy;
import com.nattguld.http.cfg.SessionConfig;
import com.nattguld.http.proxies.ProxyManager;
import com.nattguld.http.proxies.cfg.ProxyChoice;
import com.nattguld.http.proxies.cfg.ProxyConfig;
import com.nattguld.tasker.NetStepTask;
import com.nattguld.tasker.steps.Step;

/**
 * 
 * @author randqm
 *
 */

public abstract class SessionTask<S extends ISession> extends NetStepTask {
	
	/**
	 * The session instance.
	 */
	private final S session;
	
	/**
	 * The proxy choice.
	 */
	private ProxyChoice proxyChoice;

	
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
		super(name, session.getSessionData().getProxy());
		
		this.session = session;
		
		setProxyChoice(proxyChoice);
	}
	
	@Override
	protected boolean buildClient() {
		if (!session.getSessionData().hasProxy()) {
			if (SessionConfig.getConfig().getProxyPolicy() == ProxyPolicy.ASSIGNED_ONLY) {
				System.err.println("No proxy assigned to session while proxy policy requires one.");
				return false;
			}
			if (proxyChoice == ProxyChoice.DIRECT && SessionConfig.getConfig().getProxyPolicy() != ProxyPolicy.ANY
					&& !ProxyConfig.getConfig().isCellularMode()) {
				System.err.println("The current proxy policy does not allow a direct session connection.");
				return false;
			}
			if (SessionConfig.getConfig().getProxyPolicy() == ProxyPolicy.ANY) {
				proxyChoice = ProxyChoice.DIRECT;
			}
		}
		boolean built = super.buildClient();
		
		if (!built) {
			return false;
		}
		getClient().getCookieJar().importCookies(getSession().getSessionData().getCookies());
		return true;
	}
	
	/**
	 * Modifies the proxy choice.
	 * 
	 * @param proxyChoice The new proxy choice.
	 * 
	 * @return The session.
	 */
	public SessionTask<S> setProxyChoice(ProxyChoice proxyChoice) {
		this.proxyChoice = getProxyChoice(proxyChoice);
		return this;
	}
	
	/**
	 * Disposes the session.
	 */
	protected void disposeSession() {
		if (Objects.nonNull(getClient())) {
			session.getSessionData().getCookies().clear();
			session.getSessionData().getCookies().addAll(getClient().getCookieJar().getCookies());
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
		
		if (step.isCritical()) {
			resetSession();
		}
	}
	
	/**
	 * Resets the account session.
	 */
	protected void resetSession() {
		getSession().getSessionData().getCookies().clear();
	}
	
	@Override
	protected Browser getBrowser() {
		return getSession().getSessionData().getBrowser();
	}
	
	@Override
	protected ProxyChoice[] getProxyChoices() {
		return new ProxyChoice[] {
				proxyChoice
		};
	}
	
	/**
	 * Retrieves the proxy choice.
	 * 
	 * @return The proxy choice.
	 */
	public ProxyChoice getProxyChoice() {
		return proxyChoice;
	}
	
	/**
	 * Retrieves the session.
	 * 
	 * @return The session.
	 */
	public S getSession() {
		return session;
	}
	
	@Override
	protected boolean isUniqueProxyUser() {
		return true;
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
