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
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String BASE_URL = "https://algorithmhelper.herokuapp.com/";

    @BindView(R.id.webview) WebView _webView;
    @BindView(R.id.toolbar) Toolbar _toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout _drawer;
    @BindView(R.id.nav_view) NavigationView _navigationView;

    private Stack<Integer> _navigationIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Open links clicked by user in our WebView
        _webView.setWebViewClient(new WebViewClient());
        // Enable responsive layout
        _webView.getSettings().setUseWideViewPort(true);
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
        if (id == R.id.nav_trees) {
            _webView.loadUrl(BASE_URL + "Trees");
        } else if (id == R.id.nav_lists) {
            _webView.loadUrl(BASE_URL + "Lists");
        } else if (id == R.id.nav_sorting) {
            _webView.loadUrl(BASE_URL + "Sorting");
        } else if (id == R.id.nav_graphs) {
            _webView.loadUrl(BASE_URL + "Graphs");
        } else if (id == R.id.nav_send) {
            _webView.loadUrl(BASE_URL);
        }

        _drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
