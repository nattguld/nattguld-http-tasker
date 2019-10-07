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
	
	
	@Override
	protected String getSaveFileName() {
		return ".session_config";
	}

	@Override
	protected void read(JsonReader reader) {
		this.proxyPolicy = (ProxyPolicy)reader.getAsObject("proxy_policy", ProxyPolicy.class, ProxyPolicy.ASSIGNED_ONLY);
	}

	@Override
	protected void write(JsonWriter writer) {
		writer.write("proxy_policy", proxyPolicy);
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
