package com.fotoable.gifkeyboard.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fotoable.gifkeyboard.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.droidlover.xdroidmvp.base.XFragmentAdapter;
import cn.droidlover.xdroidmvp.mvp.XActivity;
import cn.droidlover.xdroidmvp.mvp.XFragment;
import cn.droidlover.xdroidmvp.mvp.XLazyFragment;
import cn.droidlover.xdroidmvp.router.Router;

/**
 * Created by ouyangwenyuan on 2017/3/14.
 */

public class GuideActivity extends XActivity {
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.bt_done)
    Button doneBtn;
    List<Fragment> fragmentList = new ArrayList<>();
    XFragmentAdapter adapter;
    String[] titles = new String[]{"0", "1"};

    @Override
    public void initData(Bundle savedInstanceState) {

        fragmentList.clear();
        fragmentList.add(GuideFragment.newInstance(0));
        fragmentList.add(GuideFragment.newInstance(1));

        if (adapter == null) {
            adapter = new XFragmentAdapter(getSupportFragmentManager(), fragmentList, titles);
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                doneBtn.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.newIntent(GuideActivity.this).to(MainActivity.class).launch();
                finish();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    public Object newP() {
        return null;
    }

    public static class GuideFragment extends XFragment {
        private int index;
        @BindView(R.id.guide_what)
        TextView guideWhat;
        @BindView(R.id.guide_info)
        TextView guideInfo;
        @BindView(R.id.iv_guide)
        ImageView guideImg;

        @Override
        public void initData(Bundle savedInstanceState) {
            index = getArguments().getInt("index", 0);
            if (index == 0) {
                guideImg.setImageResource(R.mipmap.ic_launcher);
                guideWhat.setText(R.string.guide1_title);
                guideInfo.setText(R.string.guide1_info);
            } else {
                guideImg.setImageResource(R.mipmap.ic_launcher_round);
                guideWhat.setText(R.string.guide2_title);
                guideInfo.setText(R.string.guide2_info);
            }
        }

        @Override
        public int getLayoutId() {

            return R.layout.fragment_guide1;
        }

        @Override
        public Object newP() {
            return null;
        }

        public static GuideFragment newInstance(int index) {
            GuideFragment gf = new GuideFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("index", index);
            gf.setArguments(bundle);
            return gf;
        }
    }
}
