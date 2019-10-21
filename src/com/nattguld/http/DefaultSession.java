package com.nattguld.http;

/**
 * 
 * @author randqm
 *
 */

public class DefaultSession implements ISession {

	/**
	 * The session data.
	 */
	private final SessionData sessionData;
	
	
	/**
	 * Creates a new default session.
	 */
	public DefaultSession() {
		this(new SessionData());
	}
	
	/**
	 * Creates a new default session.
	 * 
	 * @param sessionData The session data.
	 */
	public DefaultSession(SessionData sessionData) {
		this.sessionData = sessionData;
	}
	
	@Override
	public SessionData getSessionData() {
		return sessionData;
	}

}
