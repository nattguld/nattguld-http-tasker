package com.nattguld.http;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.nattguld.data.json.JsonReader;
import com.nattguld.data.json.JsonResource;
import com.nattguld.data.json.JsonWriter;
import com.nattguld.http.browser.Browser;
import com.nattguld.http.content.cookies.Cookie;
import com.nattguld.http.proxies.HttpProxy;

/**
 * 
 * @author randqm
 *
 */

public class Session extends JsonResource {
	
	/**
	 * The session id.
	 */
	private final int sessionId;
	
	/**
	 * The browser to use.
	 */
	private Browser browser;
	
	/**
	 * The cookies to attach to the session.
	 */
	private final List<Cookie> cookies;
	
	/**
	 * The proxy.
	 */
	private HttpProxy proxy;
	
	
	/**
	 * Creates a new session.
	 * 
	 * @param mobile Whether the session is on mobile or not.
	 */
	public Session(boolean mobile) {
		this(new Browser(mobile));
	}
	
	/**
	 * Creates a new session.
	 * 
	 * @param mobile Whether the session is on mobile or not.
	 */
	public Session(Browser browser) {
		this.sessionId = hashCode();
		this.browser = browser;
		this.cookies = new ArrayList<>();
	}
	
	/**
	 * Loads the session.
	 * 
	 * @param reader The json reader.
	 */
	public Session(JsonReader reader) {
		super(reader);
		
		this.sessionId = getReader().getAsInt("session_id");
		this.browser = (Browser)getReader().getAsObject("browser", Browser.class);
		this.cookies = getReader().getAsList("q_cookies", new TypeToken<List<Cookie>>() {}.getType(), new ArrayList<Cookie>());
	}
	
	@Override
	protected void write(JsonWriter writer) {
		writer.write("session_id", sessionId);
		writer.write("browser", browser);
		writer.write("q_cookies", cookies);
	}
	
	@Override
	protected String getSaveDirName() {
		return "sessions";
	}

	@Override
	protected String getSaveFileName() {
		return Integer.toString(sessionId);
	}
	
	/**
	 * Retrieves the session id.
	 * 
	 * @return The session id.
	 */
	public int getSessionId() {
		return sessionId;
	}
	
	/**
	 * Modifies the browser.
	 * 
	 * @param browser The new browser.
	 */
	protected void setBrowser(Browser browser) {
		this.browser = browser;
	}
	
	/**
	 * Retrieves the browser
	 * 
	 * @return The browser.
	 */
	public Browser getBrowser() {
		return browser;
	}
	
	/**
	 * Clears the cookies.
	 */
	public void clearCookies() {
		cookies.clear();
	}
	
	/**
	 * Retrieves the cookies.
	 * 
	 * @return The cookies.
	 */
	public List<Cookie> getCookies() {
		return cookies;
	}
	
	/**
	 * Modifies the proxy.
	 * 
	 * @param proxy The new proxy.
	 */
	public void setProxy(HttpProxy proxy) {
		this.proxy = proxy;
	}
	
	/**
	 * Retrieves the proxy.
	 * 
	 * @return The proxy.
	 */
	public HttpProxy getProxy() {
		return proxy;
	}

}
