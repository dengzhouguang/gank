package com.dzg.gank.ui.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dzg.gank.R;
import com.dzg.gank.ui.view.MyPageTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dengzhouguang on 2017/10/17.
 */

public class MainFragment extends Fragment {
    @BindView(R.id.view_pager_bottom_navigation)
    ViewPager mViewPager;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView mBNV;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main,container,false);
        ButterKnife.bind(this,view);
        init();
        return view;
    }
    private void init() {
        List<View> viewList =new ArrayList<>();
        View view1 = getLayoutInflater().inflate(R.layout.item_view_pager_1, null);
        View view2 = getLayoutInflater().inflate(R.layout.item_view_pager_2, null);
        View view3 = getLayoutInflater().inflate(R.layout.item_view_pager_3, null);
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        mViewPager.setAdapter(new CustomPagerAdapter(viewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBNV.setSelectedItemId(R.id.bottom_navigation_gank);
                        break;
                    case 1:
                        mBNV.setSelectedItemId(R.id.bottom_navigation_fuli);
                        break;
                    case 2:
                        mBNV.setSelectedItemId(R.id.bottom_navigation_search);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setPageTransformer(true, new MyPageTransformer());
        mViewPager.setOffscreenPageLimit(3);
        mBNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_gank:
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.bottom_navigation_fuli:
                        mViewPager.setCurrentItem(1);
                        return true;
                    case R.id.bottom_navigation_search:
                        mViewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };

        int[] colors = new int[]{getResources().getColor(R.color.trans_black_50),
                getResources().getColor(R.color.blue)
        };
        ColorStateList csl = new ColorStateList(states, colors);
        mBNV.setItemTextColor(csl);
        mBNV.setItemIconTintList(csl);
    }




     private class CustomPagerAdapter extends PagerAdapter{
         List<View> mList;
         public CustomPagerAdapter(List<View> list) {
             super();
             mList=list;
         }

         @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mList.get(position));
            return mList.get(position);
        }
    };
}
