package com.gustavogenovese.webview;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class WebViewActivity extends Activity {

    private static String TAG = "webview";

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
        setContentView(R.layout.webviewactivitylayout);
        
        final TextView infoText = (TextView)findViewById(R.id.infoText);
        
        
        final WebView webView = (WebView) findViewById(R.id.webViewControl);
        webView.getSettings().setJavaScriptEnabled(true);
        
        final SynchronousJavascriptInterface jsInterface = new SynchronousJavascriptInterface();
        webView.addJavascriptInterface(jsInterface, jsInterface.getInterfaceName());
        webView.loadUrl("http://www.google.com");
        webView.setWebViewClient(new WebViewClient(){

        	
			@Override
			public void onPageFinished(WebView view, String url) {
				infoText.setText(jsInterface.getJSValue(webView, "document.title"));
				
				Log.i(TAG, "Page finished loading");
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				
				if (Uri.parse(url).getHost().toLowerCase().contains("www.google.com")) {
		            return false;
		        }				
				return true;
			}
        });
        
    }

}

