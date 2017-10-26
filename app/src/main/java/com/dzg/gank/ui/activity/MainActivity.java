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
import com.dzg.gank.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
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
    public void init(){
        mNavigationView.setNavigationItemSelectedListener(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
        fragmentTransaction.replace(R.id.content_frame, new MainFragment()).commit();
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
            case R.id.nav_item_framework:
                setTitle(R.string.app_name);
                break;
            case R.id.nav_item_movie:
                navFragment=new MovieFragmen();
                setTitle(R.string.nav_item_movie);
                break;
            case R.id.nav_item_douban:
                setTitle(R.string.nav_item_douban);
                navFragment=new DouBanFragment();
                break;
            case R.id.nav_item_baiqiu:
                setTitle(R.string.nav_item_baiqiu);
                navFragment=new BaiQiuFragment();
                break;
            case R.id.nav_item_settings:
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,BottomNavigationActivity.class);
                startActivity(intent);
                return true;
            case R.id.nav_item_support:
                ToastUtil.showToast("6");
                return true;
            case R.id.nav_item_about:
                startActivity(new Intent().setClass(MainActivity.this,AboutActivity.class));
                return true;
        }

        if (navFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
            try {
                transaction.replace(R.id.content_frame, navFragment).commit();
            } catch (IllegalStateException ignored) {
            }
        }
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_MENU){
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public DrawerLayout getDrawerLayout(){
        return mDrawer;
    }
}