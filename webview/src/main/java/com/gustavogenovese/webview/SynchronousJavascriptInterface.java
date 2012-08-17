package com.gustavogenovese.webview;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.util.Log;
import android.webkit.WebView;

/**
 * Provides an interface for getting synchronous javascript calls
 */
public class SynchronousJavascriptInterface {

	/** The TAG for logging. */
	private static final String TAG = "SynchronousJavascriptInterface";

	/** The javascript interface name for adding to web view. */
	private final String interfaceName = "SynchJS";

	/** Countdown latch used to wait for result. */
	private CountDownLatch latch = null;

	/** Return value to wait for. */
	private String returnValue;

	/**
	 * Base Constructor.
	 */
	public SynchronousJavascriptInterface() {

	}

	/**
	 * Evaluates the expression and returns the value.
	 * 
	 * @param webView
	 * @param expression
	 * @return
	 */
	public String getJSValue(WebView webView, String expression) {
		latch = new CountDownLatch(1);
		String code = "javascript:window." + interfaceName
				+ ".setValue((function(){try{return " + expression
				+ "+\"\";}catch(js_eval_err){return '';}})());";
		webView.loadUrl(code);

		try {
			// Set a 1 second timeout in case there's an error
			latch.await(1, TimeUnit.SECONDS);
			return returnValue;
		} catch (InterruptedException e) {
			Log.e(TAG, "Interrupted", e);
		}
		return null;

	}

	/**
	 * Receives the value from the javascript.
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		returnValue = value;
		try {
			latch.countDown();
		} catch (Exception e) {
		}
	}

	/**
	 * Gets the interface name
	 * 
	 * @return
	 */
	public String getInterfaceName() {
		return this.interfaceName;
	}
}