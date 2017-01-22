package edu.stanford.algorithms;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by rahul on 1/21/17.
 */
public class WebViewDownloader extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        System.out.println("should override url loading");
        view.loadUrl(url);
        return true;
//        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        System.out.println("should override url loading, new API");
        return super.shouldOverrideUrlLoading(view, request);
    }
}
