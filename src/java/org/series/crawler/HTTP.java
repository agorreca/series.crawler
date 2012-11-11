package org.series.crawler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Get a file from the Internet.
 * 
 * @author jabba
 */
public class HTTP {
	/**
	 * The URL of the file we want to fetch.
	 */
	private URL url;
	private static String MESSAGE = "    ====== Getting information of external website =====";

	/**
	 * Get the content of the given URL as a string.
	 * 
	 * @param url
	 *            The URL we want to get.
	 * @return The content of the URL as a string.
	 */
	public String getURL(String url) {
		System.out.println(MESSAGE);
		this.url = this.parseURLString(url);
		URLConnection urlc = this.getHttpURLConnection(this.url);
		return getContent(urlc);
	}

	/**
	 * Gets the content of the given URL as an array of strings.
	 * 
	 * @param address
	 *            The URL of the file we want to read.
	 * @return An array of strings containing the lines of the given file (URL).
	 */
	public String[] getURLasLines(String address) {
		System.out.println(MESSAGE);
		String all = this.getURL(address);
		all = all.replaceAll("\r", "\n");
		return all.split("\n");
	}

	/**
	 * It can check if a given URL is alive or not.
	 * 
	 * @param address
	 *            URL address that we want to check if it's available or not.
	 * @return True, if it's available. False, otherwise.
	 */
	public boolean checkHttpUrl(String address) {
		try {
			System.out.println(MESSAGE);
			URL url = parseURLString(address);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			int response = huc.getResponseCode();
			if (response == 200)
				return true;
			else
				return false;
		} catch (java.io.IOException ioe) {
		}
		return false;
	}

	public String getHeaderField(String address, String field) {
		System.out.println(MESSAGE);
		URL url = parseURLString(address);
		URLConnection urlc = this.getHttpURLConnection(url);
		 return urlc.getHeaderField("Location");
	}

	/*
	 * =======================================================================
	 * and here come the private functions
	 * =======================================================================
	 */

	/**
	 * Get the content of a URL as a string.
	 * 
	 * @param urlc
	 *            URLConnection object the URL we want to read.
	 * @return String content of the given URL.
	 */
	private String getContent(URLConnection urlc) {
		try {
			InputStream is = urlc.getInputStream();
			DataInputStream ds = new DataInputStream(is);
			int length;
			length = urlc.getContentLength();
			if (length != -1) {
				byte b[] = new byte[length];
				ds.readFully(b);
				String s = new String(b);
				return s;
			} else {
				StringBuilder s = new StringBuilder();
				int i = is.read();
				while (i != -1) {
					s.append((char) i);
					i = is.read();
				}
				return new String(s);
			}
		} catch (Exception e) {
			System.err
					.println("Error: I/O error while trying to read the network file!");
			System.exit(-1);
		}
		return null; // it cannot arrive here
	}

	/**
	 * Checks if the URL is well-formed.
	 * 
	 * @param address
	 *            The URL we want to check.
	 * @return The URL itself if it is correct. If it's not correct, the program
	 *         terminates.
	 */
	private URL parseURLString(String address) {
		URL url = null;

		try {
			url = new URL(address);
		} catch (MalformedURLException e) {
			System.err.println("Error: the URL \"" + address
					+ "\" is malformed!");
			System.exit(-1);
		}
		return url;
	}

	@SuppressWarnings("static-access")
	private URLConnection getHttpURLConnection(URL url) {
		HttpURLConnection myUC = null;
		try {
			myUC = (HttpURLConnection) url.openConnection();
			myUC.setInstanceFollowRedirects(false);
			myUC.setFollowRedirects(false);
			myUC.setRequestProperty(
					"User-agent", // change
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11"); // change
			myUC.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			myUC.setRequestProperty("Accept-Charset", "Accept-Charset: ISO-8859-1,UTF-8;q=0.7,*;q=0.7");
			myUC.connect();
		} catch (Exception e) {
			System.err
					.println("Error: network error while trying to open network connection!");
			System.exit(-1);
		}
		return myUC;
	}
}