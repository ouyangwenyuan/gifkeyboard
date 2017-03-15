/*
 * Copyright (c) 2017. @author ouyang copyright@fotoable Inc.  Anyone can copy this
 */

package com.fotoable.gifkeyboard.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.fotoable.gifkeyboard.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.droidlover.xdroidmvp.base.XFragmentAdapter;
import cn.droidlover.xdroidmvp.mvp.XActivity;
import cn.droidlover.xdroidmvp.router.Router;

/**
 * Created by ouyangwenyuan on 2017/3/6.
 */
public class MainActivity extends XActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.ll_search_view)
    View searchView;

    List<Fragment> fragmentList = new ArrayList<>();


    XFragmentAdapter adapter;


    @Override
    public void initData(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);

        fragmentList.clear();
        fragmentList.add(ImojiFragment.newInstance());
        fragmentList.add(GirlFragment.newInstance());
        fragmentList.add(ImojiFragment.newInstance());

        if (adapter == null) {
            String[] titles = {getString(R.string.tab1), getString(R.string.tab2), getString(R.string.tab3)};
            adapter = new XFragmentAdapter(getSupportFragmentManager(), fragmentList, titles);
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        tabLayout.setupWithViewPager(viewPager);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.newIntent(MainActivity.this).to(SearchActivity.class).launch();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getOptionsMenuId() {
        return R.menu.menu_enter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Router.newIntent(MainActivity.this).to(SettingsActivity.class).launch();
                break;
            case R.id.action_share:
                Router.newIntent(MainActivity.this).to(ShareListActivity.class).launch();
                break;
            case R.id.action_test:
                Router.newIntent(MainActivity.this).to(TestActivity.class).launch();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Object newP() {
        return null;
    }
}

