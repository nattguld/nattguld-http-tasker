package com.nattguld.tasks;

import java.util.Objects;

import com.nattguld.http.HttpClient;
import com.nattguld.http.proxies.HttpProxy;

/**
 * 
 * @author randqm
 *
 */

public abstract class NetBridgeStepTask extends NetStepTask {

	/**
	 * The external client instance.
	 */
	private final HttpClient extClient;
	
	
	/**
	 * Creates a new network flow.
	 * 
	 * @param name The name of the flow.
	 */
	public NetBridgeStepTask(HttpClient c, String name) {
		this(c, name, null);
	}
	
	/**
	 * Creates a new network flow.
	 * 
	 * @param name The name of the flow.
	 * 
	 * @param proxy The proxy bound to this task.
	 */
	public NetBridgeStepTask(HttpClient c, String name, HttpProxy proxy) {
		super(name, proxy);
		
		this.extClient = c;
	}
	
	@Override
	protected boolean buildClient() {
		setClient(extClient);
		return Objects.nonNull(extClient);
	}
	
	/**
	 * Whether to dispose on finish or not.
	 * 
	 * @return The result.
	 */
	protected abstract boolean disposeOnFinish();
	
	@Override
	public void disposeClient() {
		if (disposeOnFinish()) {
			super.disposeClient();
		}
	}
	
	@Override
	public boolean keepAlive() {
		return true;
	}
	
}
