package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import ir.treeco.aftabe.Adapter.AdItemAdapter;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Custom.AutoScrollViewPager;
import ir.treeco.aftabe.View.Custom.MyCoordinatorLayout;

public class MainFragment extends Fragment {
    private FragmentPagerItemAdapter fragmentPagerItemAdapter;
    private ViewPager viewPager;
    public final static String FRAGMENT_TYPE = "fragment_type";
    private AutoScrollViewPager autoScrollViewPager;
    AdItemAdapter adItemAdapter;
    public MyCoordinatorLayout myCoordinatorLayout;
    public CollapsingToolbarLayout mCollapsingToolbarLayout;
    public AppBarLayout mAppBarLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        autoScrollViewPager = (AutoScrollViewPager) view.findViewById(R.id.ad_view_pager);
        adItemAdapter = new AdItemAdapter(getActivity());
        myCoordinatorLayout = (MyCoordinatorLayout) view.findViewById(R.id.my_coordinator_layout);

        fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getActivity())
                .add("تازه‌ها", PackagesFragment.class, new Bundler().putInt(FRAGMENT_TYPE, 0).get())
                .add("دانلود شده‌ها", PackagesFragment.class, new Bundler().putInt(FRAGMENT_TYPE, 1).get())
                .add("محبوب‌ترین‌ها", PackagesFragment.class, new Bundler().putInt(FRAGMENT_TYPE, 2).get())
                .add("test" , OnlineMenuFragment.class )
                .create()
        );

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentPagerItemAdapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar);
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.adtest);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
              Log.d("MainFragment" , verticalOffset + " vertical offset " + mCollapsingToolbarLayout.getHeight() );
                if(Math.abs(verticalOffset) == mCollapsingToolbarLayout.getHeight() && myCoordinatorLayout.isAllowForScrool()){
                    myCoordinatorLayout.setAllowForScrool(false);
                    Log.d("TAG", "set allow for scroll false");
                }


            }
        });
        setUpAds(autoScrollViewPager);

        return view;
    }

    private void setUpAds(AutoScrollViewPager autoScrollViewPager) {
        autoScrollViewPager.setAdapter(adItemAdapter);
        autoScrollViewPager.setOffscreenPageLimit(1);
        autoScrollViewPager.setInterval(5000);
        autoScrollViewPager.startAutoScroll();
    }
}
