package com.dzg.gank.ui.activity;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dzg.gank.App;
import com.dzg.gank.R;
import com.dzg.gank.ui.view.statusbar.StatusBarUtil;
import com.dzg.gank.ui.webview.WebViewActivity;
import com.dzg.gank.util.CommonUtils;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.nav_item_about);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        setFloating(toolbar, R.string.nav_item_about);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new AboutFragment()).commit();
        }
        StatusBarUtil.setColor(this, CommonUtils.getColor(R.color.dark_grey),0);
    }

    public static class AboutFragment extends Fragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.tab_about, container, false);

            View developersView = v.findViewById(R.id.developersView);
            View licensesView = v.findViewById(R.id.licensesView);
            View translatorsView = v.findViewById(R.id.translatorsView);
            View sourceCodeView = v.findViewById(R.id.sourceCodeView);

            String packageName = getActivity().getPackageName();
            String translator = getResources().getString(R.string.translator);

            try {
                String version = getActivity().getPackageManager().getPackageInfo(packageName, 0).versionName;
                ((TextView) v.findViewById(R.id.app_version)).setText(version);
            } catch (NameNotFoundException ignored) {
            }

            licensesView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createLicenseDialog();
                }
            });

            developersView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .title(R.string.about_developers_label)
                            .content(R.string.about_developers)
                            .positiveText(android.R.string.ok)
                            .show();

                    ((TextView) dialog.findViewById(R.id.md_content)).setMovementMethod(LinkMovementMethod.getInstance());
                }
            });

            sourceCodeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    NavUtil.startURL(getActivity(), getString(R.string.about_source));
                    Intent intent=new Intent();
                    intent.putExtra("mTitle","源码");
                    intent.putExtra("mUrl",getString(R.string.about_source));
                    intent.setClass(App.getInstance(), WebViewActivity.class);
                    startActivity(intent);
                }
            });

            if (translator.isEmpty()) {
                translatorsView.setVisibility(View.GONE);
            }

            return v;
        }

        private void createLicenseDialog() {
/*            Notices notices = new Notices();
            notices.addNotice(new Notice("material-dialogs", "https://github.com/afollestad/material-dialogs", "Copyright (c) 2014-2016 Aidan Michael Follestad", new MITLicense()));
            notices.addNotice(new Notice("StickyListHeaders", "https://github.com/emilsjolander/StickyListHeaders", "Emil Sjölander", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("PreferenceFragment-Compat", "https://github.com/Machinarius/PreferenceFragment-Compat", "machinarius", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("libsuperuser", "https://github.com/Chainfire/libsuperuser", "Copyright (C) 2012-2015 Jorrit \"Chainfire\" Jongma", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("picasso", "https://github.com/square/picasso", "Copyright 2013 Square, Inc.", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("materialdesignicons", "http://materialdesignicons.com", "Copyright (c) 2014, Austin Andrews", new SILOpenFontLicense11()));

            new LicensesDialog.Builder(getActivity())
                    .setNotices(notices)
                    .setIncludeOwnLicense(true)
                    .build()
                    .show();*/
        }
    }
    public void setFloating(android.support.v7.widget.Toolbar toolbar, @StringRes int details) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = getResources().getDimensionPixelSize(R.dimen.floating_height);
            params.width = getResources().getDimensionPixelSize(R.dimen.floating_width);
            params.alpha = 1.0f;
            params.dimAmount = 0.6f;
            params.flags |= 2;
            getWindow().setAttributes(params);

            if (details != 0) {
                toolbar.setTitle(details);
            }
            toolbar.setNavigationIcon(R.drawable.ic_close);
            setFinishOnTouchOutside(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.tabBackground));
        }
    }
}
