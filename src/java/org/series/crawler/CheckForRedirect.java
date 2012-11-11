/**
 * This class is a simple example to show how you can check a URL
 * and determine if its a redirect, then do what you wish... Handy
 * for bit.ly site and other URL shortening sites such as tinyurl.
 * 
 * You are free to use this code without restriction for eternity.
 * @author Dane Leckey
 */
package org.series.crawler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * <strong>Recommend using this, to catch url exceptions
 * otherwise check prior to instantiating instances of
 * this class.</strong>
 * @param urlStr
 * @throws MalformedURLException
 */
/**
 * Function to test for url redirect
 * @return will return false if tests fail.
 */
// cast URLConn to a HttpConn //
// dont follow redirects - this can also be set using
// .setFollowRedirects(false)
// now get the response code: if 301 is not what we get,
// then the resources is not redirected, no need to continue //
// set to null //
// all is going well so instantiate the url //
// you can also use index below to achieve same thing //
// httpUrlCon.getHeaderField(7)); //
/**
 * Function to return the URL object of redirect
 * @return - will return null if url is not a redirect.
 */
/**
 * Function returns a String representation of the redirect URL
 * @return - will return the String of the Address or null if 
 * not redirect.
 */
/**
 * Example Implementation of the CheckForRedirect class,
 * 
 * You are free to use this code without restriction for eternity
 * 
 * @author Dane Leckey
 */
// You will need the following import //
public class CheckForRedirect {

	public CheckForRedirect(URL url) {
		this.url = url;
	}

	/**
	 * <strong>Recommend using this, to catch url exceptions otherwise check
	 * prior to instantiating instances of this class.</strong>
	 * 
	 * @param urlStr
	 * @throws MalformedURLException
	 */
	public CheckForRedirect(String urlStr) throws MalformedURLException {
		this.url = new URL(urlStr);
	}

	/**
	 * Function to test for url redirect
	 * 
	 * @return will return false if tests fail.
	 */
	public boolean beginTest() {
		try {
			redirect = null;
			urlCon = url.openConnection();

			// cast URLConn to a HttpConn //
			httpUrlCon = HttpURLConnection.class.cast(urlCon);

			// dont follow redirects - this can also be set using
			// .setFollowRedirects(false)
			httpUrlCon.setInstanceFollowRedirects(false);

			// now get the response code: if 301 is not what we get,
			// then the resources is not redirected, no need to continue //
			if (httpUrlCon.getResponseCode() != 301) {
				System.err.println("Url Not Redirected");
				redirect = null; // set to null //
				return false;
			}

			// all is going well so instantiate the url //
			redirect = new URL(httpUrlCon.getHeaderField("Location"));

			// you can also use index below to achieve same thing //
			// httpUrlCon.getHeaderField(7)); //

		} catch (IOException ioe) {
			System.err.println(ioe.getStackTrace());
			return false;
		}
		return true;
	}

	/**
	 * Function to return the URL object of redirect
	 * 
	 * @return - will return null if url is not a redirect.
	 */
	public URL getRedirect() {
		return redirect;
	}

	/**
	 * Function returns a String representation of the redirect URL
	 * 
	 * @return - will return the String of the Address or null if not redirect.
	 */
	public String getRedirectString() {
		return redirect.toString();
	}

	protected URL url;
	protected URLConnection urlCon;
	protected HttpURLConnection httpUrlCon;
	protected URL redirect;
}