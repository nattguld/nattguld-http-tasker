package com.nattguld.http;

import java.util.UUID;

import com.nattguld.data.json.JsonReader;
import com.nattguld.data.json.JsonResource;
import com.nattguld.data.json.JsonWriter;

/**
 * 
 * @author randqm
 *
 */

public abstract class StorableSession extends JsonResource implements ISession {

	/**
	 * The UUID.
	 */
	private final String uuid;
	
	/**
	 * The session data.
	 */
	private final SessionData sessionData;
	
	
	/**
	 * Creates a new default session.
	 * 
	 * @param sessionData The session data.
	 */
	public StorableSession(SessionData sessionData) {
		this.uuid = String.valueOf(UUID.randomUUID());
		this.sessionData = sessionData;
	}
	
	/**
	 * Creates a new storable session.
	 * 
	 * @param reader The json reader.
	 */
	public StorableSession(JsonReader reader) {
		super(reader);
		
		this.uuid = getReader().getAsString("uuid");
		this.sessionData = (SessionData)getReader().getAsObject("session_data", SessionData.class, new SessionData());
		
		if (!getReader().has("session_data")) {
			sessionData.readMigrate(getReader());
		}
	}
	
	@Override
	protected void write(JsonWriter writer) {
		writer.write("uuid", uuid);
		writer.write("session_data", sessionData);
	}
	
	@Override
	public String getUUID() {
		return uuid;
	}
	
	@Override
	public SessionData getSessionData() {
		return sessionData;
	}

	@Override
	protected String getSaveFileName() {
		return getUUID();
	}

}
