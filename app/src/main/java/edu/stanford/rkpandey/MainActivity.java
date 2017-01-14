package edu.stanford.rkpandey;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.webview) WebView _webView;
    @BindView(R.id.toolbar) Toolbar _toolbar;

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, _toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            _webView.loadUrl("https://github.com/rpandey1234/AlgorithmHelper2/wiki/Trees");
        } else if (id == R.id.nav_gallery) {
            _webView.loadUrl("https://github.com/rpandey1234/AlgorithmHelper2/wiki/Lists");
        } else if (id == R.id.nav_slideshow) {
            _webView.loadUrl("https://github.com/rpandey1234/AlgorithmHelper2/wiki/Sorting");
        } else if (id == R.id.nav_manage) {
            _webView.loadUrl("https://github.com/rpandey1234/AlgorithmHelper2/wiki/Android");
        } else if (id == R.id.nav_send) {
            _webView.loadUrl("https://github.com/rpandey1234/AlgorithmHelper2/wiki");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
