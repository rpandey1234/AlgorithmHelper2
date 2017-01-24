package edu.stanford.algorithms;

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
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String BASE_URL = "https://algorithmhelper.herokuapp.com/";

    @BindView(R.id.webview) WebView _webView;
    @BindView(R.id.toolbar) Toolbar _toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout _drawer;
    @BindView(R.id.nav_view) NavigationView _navigationView;
    @BindView(R.id.about_content) RelativeLayout _aboutContent;
    @BindView(R.id.donate_button) Button _donateButton;

    private Stack<Integer> _navigationIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Open links clicked by user in our WebView
//        _webView.setWebViewClient(new WebViewClient());
//        _webView.setWebViewClient(new WebViewDownloader());
        System.out.println("set webview client");
        // Enable responsive layout
        _webView.getSettings().setUseWideViewPort(true);
        _webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                System.out.println("url: " + url);
                System.out.println("user agent: " + userAgent);
                System.out.println("content disposition: " + contentDisposition);
                System.out.println("contentLength: " + contentLength);
            }
        });
        setSupportActionBar(_toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, _drawer, _toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        _drawer.addDrawerListener(toggle);
        toggle.syncState();

        _navigationIds = new Stack<>();
        _navigationView.setNavigationItemSelectedListener(this);

        _webView.loadUrl(BASE_URL + "Trees");
        _navigationView.setCheckedItem(R.id.nav_trees);
        _navigationIds.push(R.id.nav_trees);
        _drawer.openDrawer(_navigationView);
    }

    @Override
    public void onBackPressed() {
        if (_drawer.isDrawerOpen(GravityCompat.START)) {
            _drawer.closeDrawer(GravityCompat.START);
        } else {
            if (_webView.canGoBack()) {
                _navigationIds.pop();
                _navigationView.setCheckedItem(_navigationIds.peek());
                _webView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        _navigationIds.push(id);
        String url = BASE_URL;
        if (id == R.id.nav_trees) {
            url += "Trees";
        } else if (id == R.id.nav_lists) {
            url += "Lists";
        } else if (id == R.id.nav_sorting) {
            url += "Sorting";
        } else if (id == R.id.nav_graphs) {
            url += "Graphs";
        } else if (id == R.id.nav_about) {
            // swap out content
            _webView.setVisibility(GONE);
            _aboutContent.setVisibility(VISIBLE);
            _drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        _webView.loadUrl(url);
        _webView.setVisibility(VISIBLE);
        _aboutContent.setVisibility(GONE);
        _drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.donate_button)
    public void onDonateButtonTap(View view) {
        System.out.println("Donate button tapped");
    }
}
