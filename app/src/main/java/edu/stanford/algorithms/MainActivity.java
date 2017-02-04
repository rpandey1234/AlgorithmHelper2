package edu.stanford.algorithms;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hotchemi.android.rate.AppRate;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String BASE_URL = "http://rkpandey.com/AlgorithmHelper/";
    public static final String INDEX_PATH = "index";
    public static final String TREES_PATH = "Trees";
    public static final String LISTS_PATH = "Lists";
    public static final String SORTING_PATH = "Sorting";
    public static final String GRAPHS_PATH = "Graphs";
    public static final String ABOUT_PATH = "About";
    public static final int MILLISECONDS_UNTIL_EXPIRY = 1000 * 60 * 60 * 24; // 24 hours
    public static final long UNSAVED = -1;
    public static final String FILE_PREFIX = "file://";
    public static final String ERROR_FILE_PATH = FILE_PREFIX + "/android_asset/error.html";

    @BindView(R.id.webview) WebView _webView;
    @BindView(R.id.toolbar) Toolbar _toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout _drawer;
    @BindView(R.id.nav_view) NavigationView _navigationView;
    @BindView(R.id.about_content) LinearLayout _aboutContent;
    @BindView(R.id.rate_us_button) Button _rateUsButton;

    private Stack<Integer> _navigationIds;
    private Map<Integer, String> _idPageMap;

    private boolean isExpired(long previousTime) {
        return System.currentTimeMillis() - previousTime > MILLISECONDS_UNTIL_EXPIRY;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        _idPageMap = new HashMap<>();
        _idPageMap.put(R.id.nav_home, BASE_URL + INDEX_PATH);
        _idPageMap.put(R.id.nav_trees, BASE_URL + TREES_PATH);
        _idPageMap.put(R.id.nav_lists, BASE_URL + LISTS_PATH);
        _idPageMap.put(R.id.nav_sorting, BASE_URL + SORTING_PATH);
        _idPageMap.put(R.id.nav_graphs, BASE_URL + GRAPHS_PATH);
        _idPageMap.put(R.id.nav_about, BASE_URL + ABOUT_PATH);

        // Enable responsive layout
        _webView.getSettings().setUseWideViewPort(true);
        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.addJavascriptInterface(new JavascriptInterfaceDownloader(this), "downloadHtml");
        _webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!isOnline()) {
                    // Do not save the page if we're offline
                    return;
                }
                String pageName;
                if (!url.equals(ERROR_FILE_PATH) && url.contains(FILE_PREFIX)) {
                    pageName = url.substring(url.lastIndexOf(File.separatorChar) + 1);
                } else if (url.contains(BASE_URL)) {
                    pageName = url.replace(BASE_URL, "");
                } else {
                    // Loading an external page into the webview
                    return;
                }
                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                long lastSavedTime = preferences.getLong(pageName, UNSAVED);
                if (lastSavedTime == UNSAVED || isExpired(lastSavedTime)) {
                    preferences.edit().putLong(pageName, System.currentTimeMillis()).apply();
                    _webView.loadUrl("javascript:window.downloadHtml.processHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>','" + pageName + "');");
                }
            }
        });
        setSupportActionBar(_toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, _drawer, _toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        _drawer.addDrawerListener(toggle);
        toggle.syncState();

        _webView.loadUrl(BASE_URL);
        _navigationIds = new Stack<>();
        _navigationView.setNavigationItemSelectedListener(this);

        _navigationView.setCheckedItem(R.id.nav_home);
        _navigationIds.push(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        if (_drawer.isDrawerOpen(GravityCompat.START)) {
            _drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (_webView.canGoBack()) {
            _webView.goBack();
            if (_idPageMap.containsValue(_webView.getUrl())) {
                // this is a navigation URL, so we should change the highlighted element in nav drawer
                _navigationIds.pop();
                _navigationView.setCheckedItem(_navigationIds.peek());
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == _navigationIds.peek()) {
            // User tapped on same page, don't do anything
            _drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        _navigationIds.push(id);
        String pageName = _idPageMap.get(id);
        if (pageName == null) {
            throw new IllegalStateException("Could not find the page");
        }
        if (isOnline()) {
            // Load from internet
            _webView.loadUrl(pageName);
        } else {
            // Load from local file
            String fileLocation = getFilesDir().getPath() + File.separator + pageName;
            if (new File(fileLocation).exists()) {
                _webView.loadUrl("file://" + fileLocation);
            } else {
                _webView.loadUrl(ERROR_FILE_PATH);
            }
        }
        _aboutContent.setVisibility(id == R.id.nav_about ? VISIBLE : GONE);
        LayoutParams layoutParams = _webView.getLayoutParams();
        if (id == R.id.nav_about) {
            layoutParams.height = Utility.getDeviceHeight(this) - 600;
        } else {
            layoutParams.height = LayoutParams.MATCH_PARENT;
        }
        _drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.rate_us_button)
    public void onRateUsButtonTap(View view) {
        AppRate.with(this).showRateDialog(this);
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }
}
