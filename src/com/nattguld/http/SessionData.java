package com.nattguld.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.reflect.TypeToken;
import com.nattguld.data.json.JsonReader;
import com.nattguld.http.browser.Browser;
import com.nattguld.http.content.cookies.Cookie;
import com.nattguld.http.proxies.HttpProxy;

/**
 * 
 * @author randqm
 *
 */

public class SessionData {
	
	/**
	 * The browser to use.
	 */
	private Browser browser;
	
	/**
	 * The cookies to attach to the session.
	 */
	private List<Cookie> cookies;
	
	/**
	 * The proxy.
	 */
	private HttpProxy proxy;
	
	
	/**
	 * Creates a new session.
	 * 
	 * @param mobile Whether the session is on mobile or not.
	 */
	public SessionData(boolean mobile) {
		this(new Browser(mobile));
	}
	
	/**
	 * Creates a new session.
	 * 
	 * @param mobile Whether the session is on mobile or not.
	 */
	public SessionData(Browser browser) {
		this.browser = browser;
		this.cookies = new ArrayList<>();
	}
	
	/**
	 * Reads the session data.
	 * 
	 * @param reader The json reader.
	 */
	@Deprecated
	public void readMigrate(JsonReader reader) {
		this.browser = (Browser)reader.getAsObject("browser", Browser.class);
		this.cookies = reader.getAsList("q_cookies", new TypeToken<List<Cookie>>() {}.getType(), new ArrayList<Cookie>());
		this.proxy = (HttpProxy)reader.getAsObject("proxy", HttpProxy.class, null);
	}
	
	/**
	 * Resets the browser.
	 * 
	 * @return The session.
	 */
	public SessionData resetBrowser() {
		setBrowser(new Browser(getBrowser().isMobile()));
		return this;
	}
	
	/**
	 * Modifies the browser.
	 * 
	 * @param browser The new browser.
	 * 
	 * @return The session.
	 */
	protected SessionData setBrowser(Browser browser) {
		this.browser = browser;
		return this;
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
	 * 
	 * @return The session.
	 */
	public SessionData clearCookies() {
		cookies.clear();
		return this;
	}
	
	/**
	 * Adds or if it already exists replaces a cookie.
	 * 
	 * @param cookie The cookie.
	 * 
	 * @return The session.
	 */
	public SessionData addOrReplaceCookie(Cookie cookie) {
		Cookie exists = getCookieByName(cookie.getName());
		
		if (Objects.nonNull(exists)) {
			cookies.remove(exists);
		}
		cookies.add(cookie);
		return this;
	}
	
	/**
	 * Retrieves a cookie by it's name.
	 * 
	 * @param cookieName The cookie name.
	 * 
	 * @return The cookie.
	 */
	public Cookie getCookieByName(String cookieName) {
		for (Cookie cookie : getCookies()) {
			if (cookie.getName().equalsIgnoreCase(cookieName)) {
				return cookie;
			}
		}
		return null;
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
	 * 
	 * @return The session.
	 */
	public SessionData setProxy(HttpProxy proxy) {
		this.proxy = proxy;
		return this;
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
