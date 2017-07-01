package edu.stanford.algorithms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appsflyer.AppsFlyerLib;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.core.CrashlyticsCore;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hotchemi.android.rate.AppRate;
import io.fabric.sdk.android.Fabric;

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
    @BindView(R.id.fab_rating) FloatingActionButton _fabRating;

    private Stack<Integer> _navigationIds;
    private Map<Integer, String> _idPageMap;

    private boolean isExpired(long previousTime) {
        return System.currentTimeMillis() - previousTime > MILLISECONDS_UNTIL_EXPIRY;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up Crashlytics, disabled for debug builds
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(this, crashlyticsKit);
        AppsFlyerLib.getInstance().startTracking(this.getApplication(), "mJaxhcAiiPpiPBeJhckJYn");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        _idPageMap = new HashMap<>();
        _idPageMap.put(R.id.nav_home, BASE_URL + INDEX_PATH);
        _idPageMap.put(R.id.nav_trees, BASE_URL + TREES_PATH);
        _idPageMap.put(R.id.nav_lists, BASE_URL + LISTS_PATH);
        _idPageMap.put(R.id.nav_sorting, BASE_URL + SORTING_PATH);
        _idPageMap.put(R.id.nav_graphs, BASE_URL + GRAPHS_PATH);
        _idPageMap.put(R.id.nav_about, BASE_URL + ABOUT_PATH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            _webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            _webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        // Enable responsive layout
        _webView.getSettings().setUseWideViewPort(true);
        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        _webView.addJavascriptInterface(new JavascriptInterfaceDownloader(this), "downloadHtml");
        _webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!isOnline() || !_idPageMap.containsValue(url)) {
                    // Do not save the page if we're offline, or if this is not a navigation page
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

        int initialMenuItem = R.id.nav_home;
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            // Only relevant for API 25, Android app shortcuts
            String shortcutNavigation = intent.getExtras().getString("ShortcutNavigation");
            if (TextUtils.equals(getString(R.string.about), shortcutNavigation)) {
                initialMenuItem = R.id.nav_about;
            } else if (TextUtils.equals(getString(R.string.sorting), shortcutNavigation)) {
                initialMenuItem = R.id.nav_sorting;
            } else if (TextUtils.equals(getString(R.string.graphs), shortcutNavigation)) {
                initialMenuItem = R.id.nav_graphs;
            }
        }
        _navigationIds = new Stack<>();
        _navigationView.setNavigationItemSelectedListener(this);

        _navigationView.setCheckedItem(initialMenuItem);

        onNavigationItemSelected(initialMenuItem);
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
                if (!_navigationIds.empty()) {
                    // This check shouldn't really be necessary, I think?
                    _navigationView.setCheckedItem(_navigationIds.peek());
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        onNavigationItemSelected(item.getItemId());
        return true;
    }

    private void onNavigationItemSelected(int itemId) {
        // Handle navigation view item clicks here.
        if (!_navigationIds.isEmpty() && itemId == _navigationIds.peek()) {
            // User tapped on same page, don't do anything
            _drawer.closeDrawer(GravityCompat.START);
            return;
        }
        _navigationIds.push(itemId);
        String pageUrl = _idPageMap.get(itemId);
        if (pageUrl == null) {
            throw new IllegalStateException("Could not find the page");
        }
        if (isOnline()) {
            // Load from internet
            _webView.loadUrl(pageUrl);
            Answers.getInstance().logCustom(new CustomEvent(pageUrl));
        } else {
            // Load from local file
            String pageName = pageUrl.replace(BASE_URL, "");
            String fileLocation = getFilesDir().getPath() + File.separator + pageName;
            if (new File(fileLocation).exists()) {
                _webView.loadUrl("file://" + fileLocation);
                Answers.getInstance().logCustom(new CustomEvent("file://" + fileLocation));
            } else {
                Answers.getInstance().logCustom(new CustomEvent(ERROR_FILE_PATH));
                _webView.loadUrl(ERROR_FILE_PATH);
            }
        }
        _fabRating.setVisibility(itemId == R.id.nav_about ? VISIBLE : GONE);
        _drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.fab_rating)
    public void onRateUsButtonTap(View view) {
        Answers.getInstance().logCustom(new CustomEvent("Rate button tapped"));
        AppRate.with(this)
                .setTitle(R.string.app_rate_title)
                .setMessage(R.string.app_rate_message)
                .setShowLaterButton(false)
                .showRateDialog(this);
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
