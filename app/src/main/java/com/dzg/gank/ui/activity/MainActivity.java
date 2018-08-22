package com.dzg.gank.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.dzg.gank.R;
import com.dzg.gank.ui.fragment.BaiQiuFragment;
import com.dzg.gank.ui.fragment.DouBanFragment;
import com.dzg.gank.ui.fragment.MainFragment;
import com.dzg.gank.ui.fragment.MovieFragmen;
import com.dzg.gank.ui.fragment.VideoFragment;
import com.dzg.gank.util.ToastUtil;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.content_frame)
    FrameLayout mFrameLayout;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        mNavigationView.setNavigationItemSelectedListener(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
        fragmentTransaction.replace(R.id.content_frame, MainFragment.getInstance()).commit();
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment navFragment = null;
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.nav_item_gank:
                navFragment = MainFragment.getInstance();
                setTitle(R.string.app_name);
                break;
            case R.id.nav_item_movie:
                navFragment = MovieFragmen.getInstance();
                setTitle(R.string.nav_item_movie);
                break;
            case R.id.nav_item_douban:
                setTitle(R.string.nav_item_douban);
                navFragment = DouBanFragment.getInstance();
                break;
            case R.id.nav_item_baiqiu:
                setTitle(R.string.nav_item_baiqiu);
                navFragment = BaiQiuFragment.getInstance();
                break;
            case R.id.nav_item_video:
                setTitle("短视频");
                navFragment = VideoFragment.getInstance();
                break;
            case R.id.nav_item_support:
                ToastUtil.showToast("支持");
                return true;
            case R.id.nav_item_about:
                startActivity(new Intent().setClass(MainActivity.this, AboutActivity.class));
                return true;
        }

        if (navFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
            transaction.replace(R.id.content_frame, navFragment).commit();
        }
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (NiceVideoPlayerManager.instance().onBackPressd()) return;
            if (NiceVideoPlayerManager.instance().getCurrentNiceVideoPlayer() != null && NiceVideoPlayerManager.instance().getCurrentNiceVideoPlayer().isPlaying()) {
                NiceVideoPlayerManager.instance().getCurrentNiceVideoPlayer().pause();
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawer;
    }
}
