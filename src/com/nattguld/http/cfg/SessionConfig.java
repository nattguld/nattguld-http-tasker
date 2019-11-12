package com.nattguld.http.cfg;

import com.nattguld.data.cfg.Config;
import com.nattguld.data.cfg.ConfigManager;
import com.nattguld.data.json.JsonReader;
import com.nattguld.data.json.JsonWriter;

/**
 * 
 * @author randqm
 *
 */

public class SessionConfig extends Config {

	/**
	 * The proxy policy.
	 */
	private ProxyPolicy proxyPolicy = ProxyPolicy.ASSIGNED_ONLY;
	
	/**
	 * Whether to allow flagged proxies or not.
	 */
	private boolean allowFlaggedProxies;
	
	
	@Override
	protected String getSaveFileName() {
		return ".session_config";
	}

	@Override
	protected void read(JsonReader reader) {
		this.proxyPolicy = (ProxyPolicy)reader.getAsObject("proxy_policy", ProxyPolicy.class, ProxyPolicy.ASSIGNED_ONLY);
		this.allowFlaggedProxies = reader.getAsBoolean("allow_flagged_proxies", false);
	}

	@Override
	protected void write(JsonWriter writer) {
		writer.write("proxy_policy", proxyPolicy);
		writer.write("allow_flagged_proxies", allowFlaggedProxies);
	}
	
	/**
	 * Modifies whether to allow flagged proxies or not.
	 * 
	 * @param allowFlaggedProxies The new state.
	 * 
	 * @return The config.
	 */
	public SessionConfig setAllowFlaggedProxies(boolean allowFlaggedProxies) {
		this.allowFlaggedProxies = allowFlaggedProxies;
		return this;
	}
	
	/**
	 * Retrieves whether to allow flagged proxies or not.
	 * 
	 * @return The result.
	 */
	public boolean isAllowFlaggedProxies() {
		return allowFlaggedProxies;
	}
	
	/**
	 * Modifies the proxy policy.
	 * 
	 * @param proxyPolicy The new proxy policy.
	 * 
	 * @return The config.
	 */
	public SessionConfig setProxyPolicy(ProxyPolicy proxyPolicy) {
		this.proxyPolicy = proxyPolicy;
		return this;
	}
	
	/**
	 * Retrieves the proxy policy.
	 * 
	 * @return The proxy policy.
	 */
	public ProxyPolicy getProxyPolicy() {
		return proxyPolicy;
	}
	
	/**
	 * Retrieves the config.
	 * 
	 * @return The config.
	 */
	public static SessionConfig getConfig() {
		return (SessionConfig)ConfigManager.getConfig(new SessionConfig());
	}

}
